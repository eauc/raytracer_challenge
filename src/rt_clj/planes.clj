; # Planes

(ns rt-clj.planes
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.intersections :as i]
            [rt-clj.shapes :as sh]
            [rt-clj.tuples :as t]))

; ## Bounds

(def local-bounds
  (constantly {:min (t/point (- (double t/infinity)) (- (double t/epsilon)) (- (double t/infinity)))
               :max (t/point t/infinity t/epsilon t/infinity)}))

; ## Intersections

; The normalized plane is `y=0`, with normal vector `n=[0,1,0]`.

; There are 4 cases to consider:
; - the ray is parallel to the plane: no hit.
; - the ray is coplanar to the plane: no hit (planes are infinitely thins).
; - the ray origin is above the plane.
; - the ray origin is below the plane.

(defn local-intersect [p {:keys [origin direction]}]
  (if (t/close? 0. (t/y direction))
    []
    (let [t (- (/ (t/y origin) (t/y direction)))]
      [(i/intersection t p)])))

; ## Normal

; The local-normal of plane is always `[0 1 0]`.

(defn local-normal [_ _ _]
  (t/vector 0. 1. 0.))

; ## Creation

; Planes are records implementing Shape protocol.

(def plane
  (partial sh/shape local-bounds local-intersect local-normal))
