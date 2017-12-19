(defproject elvn "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [devcards "0.2.4"]
                 [antizer "0.2.2"]
                 [reagent "0.7.0"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.7" :exclusions [org.clojure/clojure]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]

  :source-paths ["src"]

  :cljsbuild {:builds [{:id "devcards"
                        :source-paths ["dev" "src"]
                        :figwheel {:devcards true}
                        :compiler {:main       "elvn.dev"
                                   :asset-path "js/compiled/devcards_out"
                                   :output-to  "resources/public/js/compiled/elvn_devcards.js"
                                   :output-dir "resources/public/js/compiled/devcards_out"
                                   :source-map-timestamp true}}
                       {:id "dev"
                        :source-paths ["src"]
                        :figwheel true
                        :compiler {:main       "elvn.core"
                                   :asset-path "js/compiled/out"
                                   :output-to  "resources/public/js/compiled/elvn_dev.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}]}
  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.10.0-alpha2"]
                                  [binaryage/devtools "0.9.2"]
                                  [figwheel-sidecar "0.5.9"]]
                   :source-paths ["src" "dev"]}})
