(ns elvn.cards.dev-journal-entry
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

(def line-entries
  [{:id 1
    :account/key "1010"
    :tax/key "SGDSALES"}
   {:id 2
    :account/key "1010.01"
    :tax/key "SGDALCH"}])

(comment
  "Build the line row data by line, then the whole table")


(def my-accounts-map
  (keyed-list account-data :account/id))

(def my-taxes-map
  (keyed-list tax-data :tax/id))


(defn line-row-data
  [line accounts-map taxes-map]
  (let [account (:account/name (get accounts-map
                                    (:account/key line)))
        tax (:tax/name ((get taxes-map
                           (:tax/key line))))
        id (:id line)]
    {:key id
     :account account
     :tax tax}))

(def data {:data {:lines line-entries
                  :accounts account-data
                  :taxes tax-data}})

(defn line-table-data
  [db]
  (let [line (get-in db [:data :lines] [])
        accounts-map (keyed-list (get-in db [:data :accounts] []) :account/id)
        taxes-map (keyed-list (get-in db [:data :taxes] []) :tax/id)]
    (map #(line-row-data % accounts-map taxes-map) line)))


(defn action-change-input
  [state v]
  (assoc state :account-input v))

(defn company-accounts [accounts]
  (let [types (set (map :account/type accounts))]
    (for [type types]
      ; <Select ...> <OptGroup ... > <SelectOption ..> ...
      [ant/select-opt-group {:label type
                             :key type}
       (for [ac (filter #(= type (:account/type %)) accounts)]
         ^{:key (:account/id ac)} [ant/select-option {:value (:account/name ac)} (:account/name ac)])])))


(defn je-table-cols
  [store]
  (let [{:keys [form data row-data]} @store
        {:keys [values status error]} form
        {:keys [data]} row-data
        {:keys [account-input tax-input]} values]
    [{:title "Account"
      :render
      :dataIndex "account"
      (fn [_r]
        (r/as-element
         [ant/select {:mode "combobox"
                      :placeholder "Select an account"
                      :style {:width 300}
                      :default-value (:value account-input)
                      :on-change #(w/dispatch! store w/change-input
                                               :account-input %
                                               :valid/always)}


          (company-accounts (:accounts data))]))}
     {:title "Tax"
      :dataIndex "tax"
      :render
      (fn [_r]
        (r/as-element
          [ant/select {:placeholder "Select Tax"
                       :style {:width 300}
                       :on-change #(w/dispatch! store w/change-input
                                                :tax-input %
                                                :valid/always)}
           [ant/select-option {:value "7"} "7 %"]]))}]))


(defn je-table
  [store]
  [:div
   [ant/table {:columns (je-table-cols store)
               :dataSource ({:key 1
                             :account nil
                             :tax nil})
               :row-key "key"
               :size "middle"}]])

(defcard-rg journal-entry-card
  "This is the journal entry card built with fake data"
  (fn [store _]
    (je-table store))
  (r/atom {:form {:values {:account-input {:value ""}
                           :tax-input {:value ""}}}
           :data {:accounts account-data}
           :row-data data})
  {:inspect-data true})
