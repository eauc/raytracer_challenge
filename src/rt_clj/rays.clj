; # Rays

(ns rt-clj.rays
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.matrices :as m]
            [rt-clj.tuples :as t]))

; ## Creation
;
; Rays have a point as origin and a vector as direction.

(defn ray [origin direction]
  {:origin origin
   :direction direction})

; ## Basic operations

; We can get the point at any distance from a ray's origin.

(defn pos [r t]
  (t/add (:origin r) (t/mul (:direction r) t)))

; ## Transformations

; Translating a ray only translates the origin and doesn't change the direction.

; Scaling a ray scales both the origin and direction.

(defn transform [{:keys [origin direction]} t]
  {:origin (m/mul t origin)
   :direction (m/mul t direction)})
