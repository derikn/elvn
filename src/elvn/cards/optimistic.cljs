(ns elvn.cards.optimistic
  (:require
   [elvn.lib.webapp :as w]
   [elvn.lib.http :as h]
   [antizer.reagent :as ant]
   [reagent.core :as r]
   [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "
[Home](http://localhost:3449/cards.html#!/elvn.index)
  ")

(defcard
  "
# Blocking vs. Optimistic transactions

The usual way to transact with the back-end is to block the UI until we get a
response (eg. show a spinner). Sometimes, we really need a response from the
back-end before we can continue on the UI side.
Nevertheless, several factors make this the exception:

- we use a local store in the front-end
- we often implement client side business rules in a SPA
- we need server sent events (notifications, chat, ...)
- we run mostly on broadband connections
- network communications are asynchronous by nature

Therefore, we can design our apps to leverage on a more optimistic transaction
model.

### Blocking submit:
- Send request to the back-end and block the UI, wait for response.
- If the back-end returns an error, deal with it, and unblock the UI.
- Else update the local store with the response data, and unblock the UI.

### Optimistic submit:
- Update the local store (renders UI) and send a message to the back-end.
We are done.
- When we receive an error message from the back-end, we deal with it
(eg. revert the local state and warn the user).
- When we receive a confirmation message, we do nothing or we silently update
the local store with the back-end data.

Not shown here, we may have to deal with more edge cases
(server unreachable or timing out, concurrent updates, ...).
We'd need to cater for these anyway, wether we transact optimistically or not.
Also, blocking/unblocking the UI adds complexity and is the source of many UI bugs.
  ")

;;;;;;;;;;;; Blocking

(defn action-change-input
  [state v]
  (assoc state :input v))

(defn action-blocking-send
  [state]
  (w/send! state
           {:msg-type ::blocking-msg
            :payload (:input state)})
  (assoc state :block-ui true))

;;; Server-side processing.
(defmethod h/process ::blocking-msg
  [{:keys [payload] :as msg}]
  (assoc msg :error (= "error" payload)))
;;; ---

(defn event-blocking-msg-received
  [state v]
  (if (:error v)
    (do
      (js/alert "Server error, message not processed.")
      (dissoc state :block-ui))
    (-> state
        (update-in [:messages] conj (:payload v))
        (dissoc :input)
        (dissoc :block-ui))))

(defmethod w/received ::blocking-msg
  [store msg]
  (w/dispatch! store event-blocking-msg-received msg))

(defcard-rg blocking-example
  "
Enter some text and press 'Send'.
Wait ~2s for the server to respond.
Type and send 'error' to generate a back-end error.
  "
  (fn [store _]
    (let [state @store
          {:keys [input messages block-ui]} state]
      [:div
       [:br]
       [ant/input-group
        {:compact true}
        [ant/input
         {:style {:width "80%"}
          :disabled block-ui
          :value input
          :on-change #(w/dispatch! store
                                   action-change-input
                                   (-> % .-target .-value))}]
        [ant/button
         {:style {:width "20%"}
          :type "primary"
          :disabled (or (nil? input)
                        (= "" input))
          :loading block-ui
          :on-click #(w/dispatch! store
                                  action-blocking-send)}
         "Send"]]
       [ant/table
        {:size "middle"
         :row-key "key"
         :dataSource (map-indexed #(clj->js {:key %1 :text %2}) messages)}
        [ant/table-column
         {:title "Messages"
          :dataIndex "text"
          :key "text"}]]
       [:br]]))

  (-> {:input ""
       :messages '("three" "two" "one")}
      (r/atom)
      (w/connect-mock 2000)))

  ; {:inspect-data true})

;;;;;;;;;;; Optimistic

(defn action-optimistic-send
  [state]
  (w/send! state
           {:msg-type ::optimistic-msg
            :payload (:input state)})
  (-> state
      (update-in [:messages] conj (:input state))
      (assoc :input "")))

;;; Server-side processing.
(defmethod h/process ::optimistic-msg
  [{:keys [payload] :as msg}]
  (assoc msg :error (= "error" payload)))
;;; ---

(defn event-optimistic-msg-received
  [state v]
  (if (:error v)
    (do
      (js/alert "Server error, message will be discarded.")
      (update-in state [:messages] #(remove #{"error"} %)))
    state))

(defmethod w/received ::optimistic-msg
  [store msg]
  (w/dispatch! store event-optimistic-msg-received msg))

(defcard-rg optimistic-example
  "
Enter some text and press 'Send'.
The server is very slow (5s) but you won't notice.
Type and send 'error' to generate a back-end error.
You can continue to send while the back-end is processing.
  "
  (fn [store _]
    (let [state @store
          {:keys [input messages block-ui]} state]
      [:div
       [:br]
       [ant/input-group
        {:compact true}
        [ant/input
         {:style {:width "80%"}
          :value input
          :on-change #(w/dispatch! store
                                   action-change-input
                                   (-> % .-target .-value))}]
        [ant/button
         {:style {:width "20%"}
          :type "primary"
          :disabled (or (nil? input)
                        (= "" input))
          :on-click #(w/dispatch! store
                                  action-optimistic-send)}
         "Send"]]
       [ant/table
        {:size "middle"
         :row-key "key"
         :dataSource (map-indexed #(clj->js {:key %1 :text %2}) messages)}
        [ant/table-column
         {:title "Messages"
          :dataIndex "text"
          :key "text"}]]
       [:br]]))

  (-> {:input ""
       :messages '("three" "two" "one")}
      (r/atom)
      (w/connect-mock 5000)))

  ; {:inspect-data true})
