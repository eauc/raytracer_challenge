(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [orchestra.spec.test :as st]
            [nextjournal.clerk :as clerk]))

(defn reset
  []
  (refresh))

(defn init
  []
  (println "**** REPL init")
  (alter-var-root #'s/*explain-out* (constantly expound/printer))
  (st/instrument))

(defn test-post-load-hook [test-plan]
  (init)
  test-plan)
