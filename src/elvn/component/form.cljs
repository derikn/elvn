(ns elvn.component.form
  (:require
    [reagent.core :as r]
    [antizer.reagent :as ant]))

;; Form component from ant

;; input with validate
;; all arg required and in order
;; to use:
;; (form/init-input [name id validation)
;; name string
;; id string
;; validateion collection (see https://ant.design/components/form/#Validation-Rules)

(defn input-form [args]
  (fn [props]
    (let [ant-input (ant/get-form)
          name (get-in args [0 0])
          id (get-in args [0 1])
          validation (get-in args [0 2])]
      [ant/form
       [ant/form-item {:label name}
        (ant/decorate-field ant-input id {:rules validation}
          [ant/input
           {:placeholder name
            :id id}])]])))

(defn init-input [args]
  (ant/create-form (input-form [args])))
