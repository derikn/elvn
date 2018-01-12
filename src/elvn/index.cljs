(ns elvn.index
  (:require
    elvn.cards.optimistic
    elvn.cards.development
    elvn.cards.architecture

    elvn.cards.dev-demo
    elvn.cards.dev-todo
    elvn.cards.dev-journal-entry
    elvn.cards.derik
    todomvc.core
    elvn.cards.devsteps.index

    elvn.users.index
    elvn.stepdev.index

    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "# Eeleven")

(defcard
  "
## Tech Topics
- [Development](http://localhost:3449/cards.html#!/elvn.cards.td-derik-je)
- [Architecture](http://localhost:3449/cards.html#!/elvn.cards.architecture)
- [Blocking vs. Optimistic transactions](http://localhost:3449/cards.html#!/elvn.cards.optimistic)
  ")

(defcard
  "
## Functional modules

- [Users](http://localhost:3449/cards.html#!/elvn.users.index)
  ")
