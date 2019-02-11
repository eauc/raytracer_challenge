(defproject rt-clj "_"
  :description "Ray tracer challenge in Clojure"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [expound "0.7.2"]]
  :plugins [[lein-ancient "0.6.15"]
            [lein-pprint "1.2.0"]
            [refactor-nrepl "2.4.0"]]
  :profiles {:dev {:env {:dev true}
                   :source-paths ["dev" "test"]
                   :dependencies [[orchestra "2019.02.06-1"]
                                  [org.clojure/tools.namespace "0.2.11"]]}
             :kaocha {:dependencies [[lambdaisland/kaocha "0.0-389"]]}}
  :aliases {"kaocha" ["with-profile" "+kaocha" "run" "-m" "kaocha.runner"]}
  :repl-options {:init-ns rt-clj.repl
                 :init (init)})
