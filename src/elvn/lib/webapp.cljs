(ns elvn.lib.webapp
  (:require
   [elvn.lib.http :as h]
   [cljs.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [cljs.core.async :refer [put! chan <! >! timeout close!]])
  (:require-macros
   [cljs.core.async.macros :refer [go go-loop]]))

;;; dispatch

;; TODO: error management
;; TODO: trace

(s/def ::store
  (s/with-gen
    (s/and #(satisfies? IDeref %)
           #(satisfies? ISwap %)
           #(map? (deref %)))
    #(gen/fmap atom (s/gen map?))))

(s/def ::transition
  (s/with-gen
    (s/fspec :args (s/cat :state map?
                          :args (s/* any?))
             :ret map?)
    #(gen/return (fn [s & m] s))))

(s/fdef dispatch!
  :args (s/cat :store ::store
               :transition ::transition
               :args (s/* any?))
  :ret nil?)

(defn dispatch!
  "Apply a 'transition' to 'store', with zero or more 'args'.
   'transition': a function that takes a map (the current state),
   zero or more arguments, and returns a map (the new state).
   'store': an atom (or any compatible type).
   'args': anything.
   Eval to: nil."
  [store transition & args]
  (apply swap! (concat [store transition] args))
  nil)

;; send/receive

(defmulti received
  (fn [store {:keys [msg-type]}] msg-type))

(defn send!
  [state msg]
  (put! (:send-channel state) msg)
  nil)

(defn connect-mock
  ([store]
   (connect-mock store 16))
  ([store delay]
   (let [c (chan)]
     (swap! store assoc :send-channel c)
     (go-loop [m (<! c)]
       (.setTimeout js/window
                    #(received store (h/process m))
                    delay)
       (recur (<! c)))
     store)))

;;; data fns

;; TODO specs/check
;; TODO cljc?

(defn filt-sort
  "Takes a coll, a filtering transducer, and an optional comparator.
   Returns coll, filtered and sorted.
   Pass 'identity' for x-filt if no filtering wanted."
  ([coll x-filt]
   (into [] x-filt coll))
  ([coll x-filt f-sort]
   (sort f-sort (filt-sort coll x-filt))))

(defn mkey
  "Takes a key or seq of keys, and a map.
   Returns a map entry of the form [(k o) o]."
  [k o]
  (if (seqable? k)
    [(vec (map o k)) o]
    [(k o) o]))

(defn keyed-list
  "Takes a seq of maps and a key, or a seq of keys.
   Returns a map of the form {keys1 map1, ..., keysN mapN}.
   If a seq of keys was provided, keys1...N are vectors (composite keys).
   Throws if keys are not unique."
  [maps ks]
  (let [r (into {} (map (partial mkey ks) maps))]
    (if (= (count r) (count maps))
      r
      (throw (js/Error. "Keys are not unique")))))

(defn delete
  "Takes a coll of maps, a key or seq of keys, and a key value.
   Returns coll without the item with key value.
   Does nothing if key value not found."
  [coll k i]
  (into [] (vals (dissoc (keyed-list coll k) i))))

(defn create
  "Takes a coll of maps, a key or seq of key, and a map.
   Returns the coll with the map inserted.
   Throws if the key is already in coll."
  [coll k m]
  (let [tl (keyed-list coll k)
        [i _]  (mkey k m)
        dup (get tl i)]
    (if dup
      (throw (js/Error. "Duplicate key."))
      (into [] (vals (assoc tl i m))))))

(defn change
  "Takes a coll of maps, a key or seq of keys, a key value, and a map.
   Returns coll with the item with key value changed to the map value.
   Throws if key value not found."
  [coll k i v]
  (let [tl (keyed-list coll k)
        [i _]  (mkey k v)
        e (get tl i)]
    (if-not e
      (throw (js/Error. "Key not found."))
      (into [] (vals (assoc tl i v))))))

(defn change-key
  "Takes a coll of maps, a key or seq of keys, a key _old_ value, and a map.
   Returns coll with the item with key value _replaced_ by the map value.
   Throws if old key value not found."
  [coll k e v]
  (let [tl (keyed-list coll k)
        [i _] (mkey k v)
        o (get tl e)]
    (if-not o
      (throw (js/Error. "Key not found."))
      (into [] (vals (assoc (dissoc tl e) i v))))))

;;; forms

(defn backup-input
  "Backup the value of the current ':value' of form's input 'k' into 'old-value'."
  [state k]
  (if (= :virgin (get-in state [:form :values k :status] :virgin))
    (assoc-in state [:form :values k :old-value]
              (get-in state [:form :values k :value]))
    state))

;; Validators take a form input map and a kw to dispatch on.
;; They return the input map, eventually with an error status and a message.
(defmulti validate
  (fn [_ k] k))
  ; :default (fn [e _] e))

(defmethod validate :valid/always
  [v _]
  v)

(defn input-status
  "Take current 'state' map containing a ':form' map,
   an input 'k', a value 'v', and a 'validator' kw.
   Return the new state map, where the input has been validated and its status updated."
  [state k v validator]
  (let [e (get-in state [:form :values k])
        o (:old-value e)
        r (update e :status #(if (= o v) :clean :changed))
        r (validate r validator)]
    (assoc-in state [:form :values k] r)))

(defn form-status
  "Take current 'state' map containing a ':form' map,
   Return the new state map, where the form status is updated.
   It doesn't validate the form."
  [state]
  (let [c (map :status (vals (get-in state [:form :values])))
        r (cond
            (some #(= :error %) c) :error
            (some #(= :changed %) c) :changed
            :else :clean)]
    (assoc-in state [:form :status] r)))

(defn change-input
  "Take current 'state' map containing a ':form' map,
   an input 'k', a value 'v', and an optional 'validator' kw (defaults to ':valid/always').
   Return the new state map, where the input has been, changed and validated and its status updated.
   The form status is updated accordingly."
  ([state k v]
   (change-input k v :valid/always))
  ([state k v validator]
   (-> state
     (backup-input k)
     (assoc-in [:form :values k :value] v)
     (input-status k v validator)
     (form-status))))

;;; utils

(s/fdef throttle
  :args (s/cat :f fn? :args (s/* any?))
  :ret int?)

(def throttle
  ;; Delay a function application by 200ms.
  ;; Deduplicate calls made within the interval.
  ;; IMPORTANT: needs uncontrolled component.
  ;; Use :default-value instead of :value.
  (let [h (volatile! nil)]
    (fn [f & args]
      (when @h (.clearTimeout js/window @h))
      (vreset! h (.setTimeout js/window
                              #(do
                                 (vreset! h nil)
                                 (apply f args))
                              200)))))
