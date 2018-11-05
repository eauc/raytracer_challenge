(defproject rt-clj "_"
  :description "Ray tracer challenge in Clojure"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [expound "0.7.1"]]
  :plugins [[lein-ancient "0.6.15"]
            [lein-pprint "1.2.0"]
            [refactor-nrepl "2.4.0-SNAPSHOT"]]
  :profiles {:dev {:env {:dev true}
                   :source-paths ["dev" "test"]
                   :dependencies [[orchestra "2018.08.19-1"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [lambdaisland/kaocha "0.0-248"]]}}
  :aliases {"kaocha" ["with-profile" "+dev" "run" "-m" "kaocha.runner"]}
  :repl-options {:init-ns rt-clj.repl
                 :init (init)})

