(ns rt-clj.repl
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [orchestra.spec.test :as st]))


(defn reset
  []
  (refresh))


(defn init
  []
  (alter-var-root #'s/*explain-out* (constantly expound/printer))
  (st/instrument))
