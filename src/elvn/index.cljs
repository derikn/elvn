(ns elvn.index
  (:require
   [elvn.lib.webapp :as w]
   [elvn.lib.http :as h]
   [antizer.reagent :as ant]
   [reagent.core :as r]
   [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "# Eeleven")

(defcard
  "
## Development

- [Workflow](http://localhost:3449/cards.html#!/elvn.cards.dev_workflow)
- [Blocking vs. Optimistic transactions](http://localhost:3449/cards.html#!/elvn.cards.optimistic)
  ")

(defcard
  "
## Functional modules

- [Users](http://localhost:3449/cards.html#!/elvn.users.index)
  ")
