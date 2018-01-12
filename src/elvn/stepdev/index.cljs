(ns elvn.stepdev.index
  (:require
    [antizer.reagent :as ant]
    [elvn.lib.webapp :as w]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard index-card
  "
# DERIKS TABLE STUFF")

; (defcard
;   "
; # Problem statement
; We want to manage records about the persons and companies using the system,
; as well as their relationships (eg. employee-employer). We also want to assign
; to persons different sets of permissions (roles) towards the system.
;   ")
;

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

(def td-companies
  [#:company{:id "PALOITHK"
             :name "Palo IT HK"
             :country "EN"}
   #:company{:id "FACEBOOK"
             :name "The Clash"
             :country "EN"}
   #:company{:id "GOOGLE"
             :name "Nirvana"
             :country "US"}
   #:company{:id "TWITTER"
             :name "Public Image Ltd"
             :country "EN"}
   #:company{:id "SWIRE"
             :name "Joy Division"
             :country "EN"}])

(def td-persons
  [#:person{:first-name "John"
            :last-name "Lydon"
            :age "23"
            :e-mail "john-lydon@gmail.com"
            :phone #:phone{:country-code "65" :number "8888 9999"}
            :company/key "PIL"}
   #:person{:first-name "Joey"
            :last-name "Ramones"
            :age "23"
            :e-mail "joey-ramones@gmail.com"
            :phone #:phone{:country-code "35" :number "9999 4444"}
            :company/key "RAMONES"}
   #:person{:first-name "Joe"
            :last-name "Strummer"
            :age "23"
            :e-mail "joe-strummer@gmail.com"
            :phone #:phone{:country-code "35" :number "9999 8888"}
            :company/key "CLASH"}
   #:person{:first-name "Kurt"
            :last-name "Cobain"
            :age "23"
            :e-mail "kurt-cobain@gmail.com"
            :phone #:phone{:country-code "01" :number "9999 7777"}
            :company/key "NIRVANA"}])

(def td-role-rels
  [#:role-rel{:company/key "NIRVANA"
              :person/key "kurt-cobain@gmail.com"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"}])


(def td-accounts
  [#:account{:id "1010"
             :name "1010	Gross Invoiced Sales - Third Party"
             :type "Corporate"}
   #:account{:id "1010.01"
             :name "1010.01	Sales - SGD"
             :type "Corporate"}
   #:account{:id "1010.02"
             :name "1010.02	Sales - MYR"
             :type "Corporate"}
   #:account{:id "1010.03"
             :name "1010.03	Alcohol Sales - SGD"
             :type "Corporate"}
   #:account{:id "1010.04"
             :name "1010.04	Sales - Co Packaging Income"
             :type "Corporate"}])

(def td-company-taxes
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

;;;;;;
(defn line-row-data
  [line accounts-map taxes-map]
  (let [account (:account/name (get accounts-map
                                    (:account/key line)))
        tax (:tax/name (get taxes-map
                           (:tax/key line)))
        id (:id line)]
    {:key id
     :account account
     :tax tax}))

(defn person-row-data
  [person companies-map]
  (let [company (:company/name (get companies-map
                                    (:company/key person)))
        full-name (str (:person/first-name person)
                       " " (:person/last-name person))
        phone (str "+" (get-in person [:person/phone :phone/country-code])
                   " " (get-in person [:person/phone :phone/number]))
        e-mail (:person/e-mail person)
        age (:person/age person)]
    {:key e-mail
     :name full-name
     :age age
     :e-mail e-mail
     :phone phone
     :company company}))


(defn line-table-data
  [db]
  (let [line (get db :line [])
        accounts-map (keyed-list (get db :accounts []) :account/id)
        taxes-map (keyed-list (get db :taxes []) :tax/id)]
    (map #(line-row-data % accounts-map taxes-map) line)))

(defn persons-table-data
  [db]
  (let [persons (get db :persons [])
        companies-map (keyed-list (get db :companies []) :company/id)]
    (map #(person-row-data % companies-map) persons)))


(defn persons-table-cols
  [store]
  [{:title "Name"
    :dataIndex "name"
    :key "name"}
   {:title "E-mail"
    :dataIndex "e-mail"
    :key "e-mail"}
   {:title "Phone"
    :dataIndex "phone"
    :key "phone"}
   {:title "Company"
    :dataIndex "company"
    :key "company"}
   {:title "Age"
    :dataIndex "age"}
   {:title "Actions"
    :render
    (fn [r]
      (r/as-element
       [ant/button-group {:size "small"}
        [ant/button {:type "danger"
                     :icon "user-delete"
                     :on-click #(w/dispatch! store delete-person (aget r "key"))}]
        [ant/button {:type "primary"
                     :icon "edit"
                     :on-click #(w/dispatch! store edit-person (aget r "key"))}]]))}])

(defn persons-table
  [store data]
  [:div
   [ant/button {:type "primary"
                :icon "plus-circle"
                :on-click #(w/dispatch! store new-person)}
      "New person..."]
   [ant/table {:dataSource (persons-table-data data)
               :columns (persons-table-cols store)
               :row-key "key"
               :size "middle"
               :pagination {:show-total #(str "Total: " % " persons")}}]])

(defn records-table
  [store data]
  [:div
   [ant/table {:dataSource
               :columns
               :row-key "key"
               :size "middle"
               :pagination {:show-total #(str "Total: " % " Records")}}]])

(defcard accounts-keyed-list
  "A tagged/keyed list for lookups and upserts."
  (w/keyed-list td-accounts :account/name))

(defcard records-list
  (td-accounts))

(defcard-rg persons-table-card
  (fn [store _]
    (let [{:keys [data]} @store
          locale-atom (r/atom "en_US")]
       [ant/locale-provider {:locale (ant/locales @locale-atom)}
        [persons-table store data]]))
  (r/atom {:data {:persons td-persons
                  :companies td-companies}})
  {:inspect-data true})

;;;;;;;;;

(defn blank?
  [v]
  (or (nil? v) (= "" v)))

(defn too-short?
  [v]
  (or (nil? v) (< (.-length v) 2)))

(defmethod w/validate :valid/name
  [v _]
  (if (= :changed (:status v))
    (cond
      (blank? (:value v))
      (-> v (assoc :error "Mandatory value") (assoc :status :error))
      (too-short? (:value v))
      (-> v (assoc :error "Value too short") (assoc :status :error))
      :else (dissoc v :error))
    v))

(defmethod w/validate :valid/e-mail ;;TODO validate e-mail
  [v _]
  (if (= :changed (:status v))
    (cond
      (blank? (:value v))
      (-> v (assoc :error "Mandatory value") (assoc :status :error))
      (too-short? (:value v))
      (-> v (assoc :error "Value too short") (assoc :status :error))
      :else (dissoc v :error))
    v))

(defmethod w/validate :valid/phone ;;TODO validate phone
  [v _]
  (if (= :changed (:status v))
    (cond
      (blank? (:value v))
      (-> v (assoc :error "Mandatory value") (assoc :status :error))
      (too-short? (:value v))
      (-> v (assoc :error "Value too short") (assoc :status :error))
      :else (dissoc v :error))
    v))

(defn validate-person
  [state]
  (-> state
      (update-in [:form :values :first-name] #(w/validate % :valid/name))
      (update-in [:form :values :last-name] #(w/validate % :valid/name))
      (update-in [:form :values :country-code] #(w/validate % :valid/name))
      (update-in [:form :values :phone] #(w/validate % :valid/phone))
      (update-in [:form :values :e-mail] #(w/validate % :valid/e-mail))
      (update-in [:form :values :company] #(w/validate % :valid/name))
      (w/form-status)))

(defn submit-person
  [state]
  (validate-person state)
  (if (= :error (get-in state [:form :status]))
    (assoc-in state [:form :error] "The form contains errors.")
    (do
      (println "submit person")
      (assoc-in state [:form :error] nil))))

(defn prefix-error
  [status]
  (when (= :error status)
    (r/as-element [ant/icon {:type "exclamation-circle-o"
                             :style {:color "red"}}])))

(defn cancel-form
  [state]
  (println "Cancel form")
  state)

(defn person-form
  [store]
  (let [{:keys [form data]} @store
        {:keys [values status error]} form
        {:keys [first-name last-name e-mail
                country-code phone company]} values
        companies (w/filt-sort (:companies data)
                               identity ;; no filter
                               #(compare (:company/name %1) (:company/name %2)))]
    [:div
     [ant/form {:on-submit #(do
                              (.persist %)
                              (w/dispatch! store submit-person)
                              (.preventDefault %))
                :layout "vertical"}
      [ant/form-item {:label "* First name"
                      :help (:error first-name)}
       [ant/input {:value (:value first-name)
                   :prefix (prefix-error (:status first-name))
                   :on-change #(w/dispatch! store w/change-input
                                            :first-name (-> % .-target .-value)
                                            :valid/name)}]]
      [ant/form-item {:label "* Last name"
                      :help (:error last-name)}
       [ant/input {:value (:value last-name)
                   :prefix (prefix-error (:status last-name))
                   :on-change #(w/dispatch! store w/change-input
                                            :last-name (-> % .-target .-value)
                                            :valid/name)}]]
      [ant/form-item {:label "* E-mail"
                      :help (:error e-mail)}
       [ant/input {:value (:value e-mail)
                   :prefix (prefix-error (:status e-mail))
                   :on-change #(w/dispatch! store w/change-input
                                            :e-mail (-> % .-target .-value)
                                            :valid/e-mail)}]]
      [ant/form-item {:label "* Phone"
                      :help (:error phone)}
       [ant/input-group {:compact true}
        [ant/select {:style {:width "10%"}
                     :default-value (:value country-code)
                     :on-change #(w/dispatch! store w/change-input
                                              :country-code %
                                              :valid/always)}
         (for [n (range 99)]
           ^{:key (str n)} [ant/select-option {:value (str n)} (str "+" n)])]
        [ant/input {:style {:width "90%"}
                    :value (:value phone)
                    :prefix (prefix-error (:status phone))
                    :on-change #(w/dispatch! store w/change-input
                                             :phone (-> % .-target .-value)
                                             :valid/phone)}]]]
      [ant/form-item {:label "* Company"
                      :help (:error phone)}
        [ant/select {:default-value (:value company)
                     :on-change #(w/dispatch! store w/change-input
                                              :company %
                                              :valid/name)}
         (for [c companies]
           ^{:key (:company/id c)} [ant/select-option {:value (:company/id c)} (:company/name c)])]]
      [ant/button {:type "secondary"
                   :on-click #(w/dispatch! store cancel-form)}
       "Cancel"]
      [ant/button {:type "primary"
                   :html-type "submit"}
       "Save"]
      [:span error]]
     [:br]]))

(defcard-rg person-form-card
  ""
  (fn [store _]
    (person-form store))
  (r/atom {:form {:values {:first-name {:value "Ian"}
                           :last-name {:value "Curtis"}
                           :e-mail {:value "ian-curtis@gmail.com"}
                           :phone {:value "7777 4444"}
                           :country-code {:value "33"}
                           :company {:value "JOYDIVISION"}}}
           :data {:companies td-companies}})
  {:inspect-data true})

(defcard
  "
## Calling overseas
## Wrapping up
  ")
