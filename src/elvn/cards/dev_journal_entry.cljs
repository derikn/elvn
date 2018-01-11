(ns elvn.cards.dev-journal-entry
  (:require
    [elvn.lib.webapp :as w]
    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

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


(defn action-change-input
  [state v]
  (assoc state :account-input v))

(defn accounts-list [accounts]
  (let [types (set (map :account/type accounts))]
    (for [type types]
      ; <Select ...> <OptGroup ... > <SelectOption ..> ...
      [ant/select-opt-group {:label type
                             :key type}
       (for [ac (filter #(= type (:account/type %)) accounts)]
         ^{:key (:account/id ac)} [ant/select-option {:value (:account/name ac)} (:account/name ac)])])))



(defn je-table-cols
  [store]
  (let [{:keys [form data]} @store
        {:keys [values status error]} form
        {:keys [account-input tax-input]} values]
    [{:title "Account"
      :render
      (fn [_r]
        (r/as-element
         [ant/select {:mode "combobox"
                      :placeholder "Select an account"
                      :style {:width 300}
                      :default-value (:value account-input)
                      :on-change #(w/dispatch! store w/change-input
                                               :account-input %
                                               :valid/always)}


          (accounts-list (:accounts data))]))}
     {:title "Tax"
      :render
      (fn [_r]
        (r/as-element
          [ant/select {:placeholder "Select Tax"
                       :style {:width 300}}
           [ant/select-option {:value "7"} "7 %"]]))}]))


(defn je-table
  [store]
  [:div
   [ant/table {:columns (je-table-cols store)
               :dataSource [{:account nil
                             :tax nil
                             :key 1}]
               :row-key "key"
               :size "middle"}]])

(defcard-rg journal-entry-card
  "Journal Entry

  ```
  [#:account {:name ...
                :id ...
                :type ...
              ...}]

  === equivalent to the following ===

  [{:account/name: ...
    :account/id: ...
    :account/type: ...}]
  ```

  "
  (fn [store _]
    (je-table store))
  (r/atom {:form {:values {:account-input {:value ""}
                           :tax-input {:value ""}}}
           :data {:accounts account-data}})
  {:inspect-data true})
