(ns elvn.cards.dev-todo
  (:require
    [elvn.lib.webapp :as w]
    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "# Dev Todo")

(defonce todos (r/atom (sorted-map)))
(defonce counter (r/atom 0))

(defn add-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id {:id id :title text :done false})))

(defn delete [id]
  (swap! todos dissoc id)
  (println todos))

(defn toggle [id]
  (swap! todos update-in [id :done] not))

(defonce init (do
                (add-todo "Learn clojure")
                (add-todo "Finish To Do List")
                (add-todo "Buy humidifier")))

(defn todo-input
  [store]
  (let [ save #(add-todo (:input @store))
         clear #(swap! store assoc :input "")]
     [:div
       [ant/input-group
        {:compact true}
        [ant/input {:style {:width "80%"}
                    :value (:input @store)
                    :placeholder "next things to do"
                    :on-change #(swap! store assoc :input (-> % .-target .-value))
                    :on-key-down #(case (.-which %)
                                    13 (comp clear save)
                                    nil)}]
        [ant/button {:style {:width "20%"}
                     :type "primary"
                     :on-click (comp clear save)}
                    "Add"]]]))
(defn todo-item [i]
  (let [editing (r/atom false)]
    (fn []
      [:div {:style {:list-style "none"}}
        [ant/input-group
         {:compact true}
         [:input {:type "checkbox"
                     :checked (:done i)
                     :on-change #(toggle (:id i))}]
         [:label {:style {:padding "0 20px"
                          :width "30%"}}
                 (:title i)]
         [ant/button {:type "secondary"
                      :on-click #(delete (:id i))}
                     "Delete"]]])))

(defcard-rg todo-app-card
  ""
  (fn [store _]
    (let [items (vals @todos)
          done (->> items (filter :done) count)]
      [:div
       [:section#todoapp
        (todo-input store)
        (when (-> items count pos?)
          [:div
           (for [i items] ^{:key i} [todo-item i])])]]))

  (r/atom {})
  {:inspect-data true})
