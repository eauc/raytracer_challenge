; # Lights

(ns rt-clj.lights
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true})

; ## Creation

; A point light has a position and intensity

(defn point-light [position intensity]
  {:position position
   :intensity intensity})
