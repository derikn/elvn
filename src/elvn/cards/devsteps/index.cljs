(ns elvn.cards.devsteps.index
  (:require
   [elvn.lib.webapp :as w]
   [elvn.lib.http :as h]
   [antizer.reagent :as ant]
   [reagent.core :as r]
   [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard
  "
[Home](http://localhost:3449/cards.html#!/elvn.index)
  ")

(defcard
  "
# Step by Step Dev

Pre-reqs: assuming you see this card in your browser, you should be good good.
If not, see the project README.

  ")

(defcard
  "
### Create a git branch
From the project directory, in a terminal:
```sh
$ git branch stepdev
$ git checkout stepdev
```
  ")

(defcard
  "
### Create a devcard file
Create a `src/elvn/stepdev` folder to work in.
Create a file named `index.cljs` in it.
```sh
$ mkdir src/elvn/stepdev
$ cp src/elvn/index.cljs src/elvn/stepdev
```
Open the file in your editor, paste in the following namespace definition:
```clj
(ns elvn.stepdev.index
  (:require
    [antizer.reagent :as ant]
    [reagent.core :as r])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))

(defcard first-card
  \"# First card\")
```
Save the file.
Open the file `src/elvn/index.cljs` and add a require entry for your own file.
```clj
;; src/elvn/index.cljs

(ns elvn.index
  (:require
    elvn.stepdev.index ;; <<< ADD THIS

    ;; Entries below may be different in your own sr/elvn/index.cljs
    ;; DO NOT CHANGE THEM
    elvn.cards.optimistic
    elvn.cards.development
    elvn.cards.architecture

    elvn.users.index

    [antizer.reagent :as ant]
    [reagent.core :as r]
    [clojure.string :as string :refer [lower-case upper-case]])
  (:require-macros
    [devcards.core :as dc :refer [defcard deftest defcard-rg]]))
```
Save and close the file.
Re
In a new browser tab open your own card:
`http://localhost:3449/cards.html#!/elvn.stepdev.index`

You should see this:
  ")

(defcard first-card
  "# First card")


(defcard
  "
### Create a card

  ")

(defcard
  "
### UI, part I

  ")

(defcard
  "
### Data, part I


  ")
