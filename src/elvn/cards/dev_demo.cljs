(ns elvn.cards.dev-demo
  (:require
    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "# Dev Demo")

(defcard-rg ui-component
  "We can do without ui library!"
  (fn [store _]
    [:div
     [:h1 (str "Value: " (:value @store))]
     [:input {:value (:input @store)
              :on-change #(swap! store assoc :input (-> % .-target .-value))}]
     [:button {:on-click #(swap! store
                                 (fn [state]
                                   (-> state
                                       (assoc :value (:input @store))
                                       (dissoc :input))))}
      "Press me"]])
  (r/atom {})
  {:inspect-data true})

;;;;;;;;;;;

(defcard-rg ui-example
  "Example card"
  ;; Render function
  (fn [store _]
    [:div
     [:h1 (:value @store)]
     [ant/button
      {:type "primary"
       :on-click #(swap! store update :value (fn [v] (+ v 100)))}
      "Add 100"]
     [:h1 (str (:list @store))]
     [ant/button
      {:type "primary"
       :on-click #(swap! store update :list (fn [v] (conj v 100)))}
      "Conjoin 100"]])
  ;; Store initialisation
  (r/atom {:value 123
           :list [1 2 3 4 5 6]})
  ;; Card options
  {:inspect-data true})
