(ns elvn.dev
  (:require
    elvn.core

    elvn.index
    elvn.users.index

    elvn.cards.optimistic
    elvn.cards.dev-workflow

    [cljs.spec.alpha :as s]
    [clojure.spec.test.alpha :as stest]
    [clojure.test.check :as tc]))

(enable-console-print!)

; (stest/instrument 'elvn-clt-lib.core/dispatch!)
