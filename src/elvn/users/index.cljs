(ns elvn.users.index
  (:require
   [elvn.lib.webapp :as w]
   [antizer.reagent :as ant]
   [reagent.core :as r]
   [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "
[Home](http://localhost:3449/cards.html#!/elvn.index)
  ")

(defcard index-card
  "
# USERS")

(defcard
  "
# Problem statement
We want to manage records about the persons and companies using the system,
as well as their relationships (eg. employee-employer). We also want to assign
to persons different sets of permissions (roles) towards the system.
  ")

(def td-companies
  [#:company{:id "BLONDIE"
             :name "Blondie"
             :country "EN"}
   #:company{:id "CLASH"
             :name "The Clash"
             :country "EN"}
   #:company{:id "NIRVANA"
             :name "Nirvana"
             :country "US"}
   #:company{:id "PIL"
             :name "Public Image Ltd"
             :country "EN"}
   #:company{:id "JOYDIVISION"
             :name "Joy Division"
             :country "EN"}])

(defcard
  "
## Companies
Attributes: Company name, country, id.
Key: id.
  "
  td-companies)

(def td-persons
  [#:person{:first-name "John"
            :last-name "Lydon"
            :e-mail "john-lydon@gmail.com"
            :phone #:phone{:country-code "65" :number "8888 9999"}
            :company/key "PIL"}
   #:person{:first-name "Joe"
            :last-name "Strummer"
            :e-mail "joe-strummer@gmail.com"
            :phone #:phone{:country-code "35" :number "9999 8888"}
            :company/key "CLASH"}
   #:person{:first-name "Kurt"
            :last-name "Cobain"
            :e-mail "kurt-cobain@gmail.com"
            :phone #:phone{:country-code "01" :number "9999 7777"}
            :company/key "NIRVANA"}])

(defcard
  "
## Persons
Attributes: First and last names, e-mail, phone number, company key.
Key: e-mail.
Rels: A person works for a company.
  "
  td-persons)

(def td-roles
  [#:role{:name "Singer"}
   #:role{:name "Guitarist"}])

(defcard
  "
## Roles
Attributes: role name.
Key: role name.
Rels: A person can have several roles, for several companies.
  "
  td-roles)

(def td-role-rels
  [#:role-rel{:company/key "NIRVANA"
              :person/key "kurt-cobain@gmail.com"
              :role/key "Singer"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"
              :role/key "Guistarist"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"
              :role/key "Singer"}
   #:role-rel{:company/key "CLASH"
              :person/key "joe-strummer@gmail.com"
              :role/key "Singer"}])

(defcard
  "
## Role relationship
Key: person key + company key + role key.
  "
  td-role-rels)

;;;;;;;

(defcard
  "
## Generic/sample operations over entities
  ")

(defcard filtsort-persons
  "Filter persons on first name initial 'J', sorted by first name + last name."
  (vec (w/filt-sort td-persons
                    (filter #(= "J" (first (:person/first-name %)))) ;a transducer
                    (fn [{:keys [a-first-name a-last-name]} ;a comparator
                         {:keys [b-first-name b-last-name]}]
                      (compare (str a-first-name a-last-name)
                               (str b-first-name b-last-name))))))

(defcard persons-keyed-list
  "A tagged/keyed list for lookups and upserts."
  (w/keyed-list td-persons :person/e-mail))

(defcard delete-person
  "Remove entity by key 'joe-strummer@gmail.com'."
  (w/delete td-persons :person/e-mail "joe-strummer@gmail.com"))

(defcard create-person
  "Insert entity 'debbie-harry@gmail.com'."
  (w/create td-persons :person/e-mail
            #:person{:first-name "Debbie"
                     :last-name "Harry"
                     :e-mail "debbie-harry@gmail.com"
                     :phone #:phone{:country-code "33" :number "6666 7777"}
                     :company/key "BLONDIE"}))

(defcard create-person-duplicate
  "Insert duplicate."
  (try
    (w/create td-persons :person/e-mail
              #:person{:first-name "Kurt"
                       :last-name "Cobain"
                       :e-mail "kurt-cobain@gmail.com"
                       :phone #:phone{:country-code "01" :number "9999 7777"}
                       :company/key "NIRVANA"})
    (catch js/Error e (str "Error: " (.-message e)))))

(defcard change-person
  "Change 'Cobain' to 'Late Cobain'."
  (w/change td-persons :person/e-mail "kurt-cobain@gmail.com"
            #:person{:first-name "Kurt"
                     :last-name "Late Cobain"
                     :e-mail "kurt-cobain@gmail.com"
                     :phone #:phone{:country-code "01" :number "9999 7777"}
                     :company/key "NIRVANA"}))

(defcard change-person-not-found
  "Change person key: key not found (use change-key function instead)."
  (try
    (w/change td-persons :person/e-mail "kurt-cobain@gmail.com"
              #:person{:first-name "Kurt"
                       :last-name "Late Cobain"
                       :e-mail "kurt-latecobain@gmail.com"
                       :phone #:phone{:country-code "01" :number "9999 7777"}
                       :company/key "NIRVANA"})
    (catch js/Error e (str "Error: " (.-message e)))))

(defcard change-person-key
  "Change 'kurt-cobain@gmail.com' to 'kurt-latecobain@gmail.com'."
  (w/change-key td-persons :person/e-mail "kurt-cobain@gmail.com"
                #:person{:first-name "Kurt"
                         :last-name "Late Cobain"
                         :e-mail "kurt-latecobain@gmail.com"
                         :phone #:phone{:country-code "01" :number "9999 7777"}
                         :company/key "NIRVANA"}))

;;;;;;;

(defn new-person
  [state]
  (println "new person")
  state)

(defn delete-person
  [state k]
  (println "delete person: " k)
  state)

(defn edit-person
  [state k]
  (println "edit person: " k)
  state)

(defcard
  "
## Draft UI
  ")

(defn person-row-data
  [person companies-map]
  (let [company (:company/name (get companies-map
                                    (:company/key person)))
        full-name (str (:person/first-name person)
                       " " (:person/last-name person))
        phone (str "+" (get-in person [:person/phone :phone/country-code])
                   " " (get-in person [:person/phone :phone/number]))
        e-mail (:person/e-mail person)]
    {:key e-mail
     :name full-name
     :e-mail e-mail
     :phone phone
     :company company}))


(defn persons-table-data
  [db]
  (let [persons (get db :persons [])
        companies-map (w/keyed-list (get db :companies []) :company/id)]
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
   {:title ""
    :render
    (fn [_ r]
      (r/as-element
       [ant/button-group {:size "small"}
        [ant/button {:type "danger"
                     :icon "user-delete"
                     :on-click #(w/dispatch! store delete-person (aget r "key"))}]
        [ant/button {:type "primary"
                     :icon "edit"
                     :on-click #(w/dispatch! store edit-person (aget r "key"))}]]))}])

(defcard persons-data
  "Persons data for table"
  (let [db {:persons td-persons
            :companies td-companies}]
    (persons-table-data db)))

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

(defcard-rg persons-table-card
  ""
  (fn [store _]
    (let [{:keys [data]} @store
          locale-atom (r/atom "en_US")]
       [ant/locale-provider {:locale (ant/locales @locale-atom)}
        [persons-table store data]]))
  (r/atom {:data {:persons td-persons
                  :companies td-companies}}))
  ; {:inspect-data true})

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
           :data {:companies td-companies}}))
  ; {:inspect-data true})

(defcard
  "
## Calling overseas
## Wrapping up
  ")
