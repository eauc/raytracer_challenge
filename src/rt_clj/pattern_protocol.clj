; # Pattern

(ns rt-clj.pattern-protocol
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.matrices :as mat]))

; ## Generic Patterns

; Objects can have patterns and both can have transformations.

(defprotocol Pattern
  (pattern-at [pattern point] "pattern color at point"))

(defn pattern-at-shape [{p-inverse-t :inverse-t :as pattern}
                        {s-inverse-t :inverse-t} ;; shape
                        w-point]
  (let [o-point (mat/mul-t s-inverse-t w-point)
        p-point (mat/mul-t p-inverse-t o-point)]
    (pattern-at pattern p-point)))
