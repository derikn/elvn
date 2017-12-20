(ns elvn.cards.development
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
# Development
  ")

(defcard
  "
## Principles

### Clojure and ClojureScript
One language to rule them all.
Data in and data out, all the way down from the browser to the DB, and back.

Clojure is VERY easy compared to JS or Java.
Just don't try to edit a file without [Parinfer](https://shaunlebron.github.io/parinfer/).
Bookmark the [cheat sheet](http://cljs.info/cheatsheet/), but you'll be up and running
with a fraction of what's in it.

Learn with a [REPL](http://planck-repl.org).

Here's some basics to get you on track, search for these topics first.

We process _immutable_ values with (mostly) _pure_ functions.

`#(+ %1 %2)` defines an anonymous function (a _lambda_) returning
the sum of its first and second arguments.

If you put something within parens, it's going to be evaluated.
So make sure the first element within parens is a function: `(verb arg1 arg2 ...)`.
`(+ 2 2)` adds 2 and 2, as would `(#(+ %1 %2) 2 2)`, but `(1 2 3)` throws.

Such expressions can be infinitely nested: `(+ (- 2 1) (* (/ 4 2) 3))`.
Balancing parenses could be a hell, but there are tools to help with this,
so you'll soon learn to love the prower of structural edit.
Also, the threading macros `->` and `->>` come handy.

`def` and `defn` bind names to things.
Use `let` expressions when you need local variables (bindings).

Learn `Maps`, `Vectors`, `Sequences` and some operations on them:
`get`/`assoc`/`dissoc`/`update`, `first` and `rest`, `map`, `filter`, and some `reduce`.

`Atoms` hold mutable values. Know when you hold a box (store), or its content (state).
`deref` (@) returns an atom content, `swap!` replaces an atom content with the result of
applying a given function to it (the content).

Functions can be passed, returned, or stored. Don't abuse this.

[Destructuring](https://gist.github.com/john2x/e1dca953548bfdfb9844) is only sugar,
but is valuable knowledge in order to understand other people coding.

### Live, Litterate Programming
If you constantly refresh your browser, or repeatedly input the same values in the same form,
then you're doing it wrong. If you aren't used to live coding, pair with someone
until you get your head around it. Beware though, there's no way back.

This project is more of a garden than a high tech building. Grow some feature
in your corner, changing the app while it is running, writing runnable documentation
along your code.

### Back To Front
We serve users, through an awesome UI, the rest is mechanics.
Make it work seamlessly within the browser runtime, validate/tune with the PO,
then port towards the back-end, without breaking the live/browser build.
See [cljc](https://clojure.org/guides/reader_conditionals) to learn how to
write portable code.

### Monorepo and Trunk Based Development
One single code repository, many build targets, controlled external dependencies.
Don't ever break HEAD. Use dynamic feature flipping (via dependecies, config or flags).
Branches are ephemeral (0-3 days). Don't wait to be finished, integrate constantly.
PRs are being diligently reviewed, locally tested and merged, and pushed to human testing.
Builds say what to deliver, not where and when.
  ")

(defcard
  "
## Step 1: Front-End

### Short Story
- Branch from HEAD
- Create a devcard (or copy one)

Interactively, in no particular order:
- Put up some test data
- Write queries and commands against it
- Compose screens and forms using the generic UI lib (antd/reagent)

Then:
- Assemble a story / user journey, mocking back-end processing
- Meet your PO, align
- Meet your peer, refactor
- Merge locally, test, send PR


### Setup
### First Card
### Test Data and Initial State
### UI Components
### Responding to user actions
### Mocking Back-End Processing
### Assembling a User Journey
### Writing Tests
### Refactoring
### Changing Shared Code
### Writing Specs and Checks
### Next Steps
  ")
