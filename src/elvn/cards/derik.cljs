(ns elvn.cards.derik
  (:require
    [elvn.lib.webapp :as w]
    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

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
      r)))

(defcard
  "Hello this is some def card stuff")

(def account-data
  [#:account {:name "1010	Gross Invoiced Sales - Third Party"
              :id "1010"
              :type "Accounts"}
   #:account {:name "1010.01	Sales - SGD"
              :id "1010.01"
              :type "Accounts"}
   #:account {:name "1010.02	Sales - MYR"
              :id "1010.02"
              :type "Vendors"}
   #:account {:name "1010.03	Sales - USD"
              :id "1010.03"
              :type "Vendors"}
   #:account {:name "1010.04	Sales - Co Packaging Income"
              :id "1010.04"
              :type "Customers"}
   #:account {:name "1010.05	Rental Income - Office"
              :id "1010.05"
              :type "Customers"}
   #:account {:name "1010.06	Rental Income - Warehouse"
              :id "1010.06"
              :type "Customers"}
   #:account {:name "1010.07	Sales - Management Fees"
              :id "1010.07"
              :type "Customers"}
   #:account {:name "1010.08	Sales - Warehouse"
              :id "1010.08"
              :type "Customers"}])

(def tax-data
  [#:tax{:id "SGDSALES"
         :name "SGD Sales Tax"
         :display "7%"
         :rate 0.07}
   #:tax{:id "SGDALCH"
         :name "SGD Alcohol Tax"
         :display "5%"
         :rate 0.05}])

(def entry-data
  [#:entry {:id 1
            ;account-key
            :account/key "1010"
            :tax/key "SGDSALES"}
   #:entry {:id 2
            :account/key "1010.01"
            :tax/key "SGDALCH"}])

(def one-entry
  #:entry {:id 5
           :account/key "1010.08"
           :tax/key "SGDSALES"})

;test remapping the entries field
; (assoc-in data [:entries] one-entry)

(def data {:entries entry-data
           :accounts account-data
           :taxes tax-data})

(defcard display-entries
  (:entries data))

(def accounts-m
  (keyed-list account-data :account/id))

(def taxes-m
  (keyed-list tax-data :tax/id))

;Build Data TABLE

(defn entry-row-data
  [entry accounts-m taxes-m]
  (let [account (:account/name (get accounts-m (:account/key entry)))
        tax (:tax/name (get taxes-m (:tax/key entry)))
        entry-id (:entry/id entry)]
    {:key entry-id
     :account account
     :tax tax}))

;tests
(entry-row-data {:tax/key "SGDSALES", :entry/id 1, :account/key "1010"} accounts-m taxes-m)

(defn entry-table-data
  [db]
  (let [entries (get db :entries [])
        accounts-map (keyed-list (get db :accounts []) :account/id)
        taxes-map (keyed-list (get db :taxes []) :tax/id)]
    (map #(entry-row-data % accounts-map taxes-map) entries)))

;test
(get data :entries [])
(keyed-list (get data :accounts []) :account/id)
(keyed-list (get data :taxes []) :tax/id)
(map #(entry-row-data % accounts-m taxes-m) entry-data)
(entry-table-data data)

(defn entry-table-col
  [store]
  [{:title "Account Name"
    :dataIndex "account"
    :key "account"}
   {:title "Tax"
    :dataIndex "tax"
    :key "tax"}
   {:title ""
    :render
    #()}])


(defcard entries-data
  "Display entries data for table"
  (let [db {:entries entry-data
            :accounts account-data
            :taxes tax-data}]
    (entry-table-data db)))

;test creating an entry
(defn delete-agent
  [state k]
  (println "delete agent: " k)
  (let [agents-ori (get-in state [:data :agents])
        agents-new (w/delete agents-ori :agent/id k)]
    (assoc-in state [:data :agents] agents-new)))

(defn add-entry
  [state]
  (let [entries-ori (get-in state [:data :entries])
        entries-new (w/create entries-ori :entry/id #:entry{:id 3
                                                            :account/key "1010.08"
                                                            :tax/key "SGDSALES"})]
    (assoc-in state [:data :entries] entries-new)))

(defcard display-add-entry
  (add-entry #:entry{:id 3
                     :account/key "1010.08"
                     :tax/key "SGDSALES"}))

(defn entries-table
  [store data]
  [:div
   [ant/button {:type "primary"
                :icon "plus-circle"
                :on-click #(w/dispatch! store add-entry)}
      "New entry"]
   [ant/table {:dataSource (entry-table-data data)
               :columns (entry-table-col store)
               :row-key "key"
               :size "middle"
               :pagination {:show-total #(str "Total: " % " entries")}}]])

(defcard-rg entries-table-card
  (fn [store _]
    (let [{:keys [data]} @store
          locale-atom (r/atom "en_US")]
       [ant/locale-provider {:locale (ant/locales @locale-atom)}
        [entries-table store data]]))
  (r/atom {:data data})
  {:inspect-data true})
