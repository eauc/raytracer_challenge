; # Cubes

(ns rt-clj.cubes
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:import java.lang.Math)
  (:require [rt-clj.intersections :as i]
            [rt-clj.shapes :as sh]
            [rt-clj.tuples :as t]))

; ## Bounds

(def local-bounds
  (constantly {:min (t/point -1. -1. -1.)
               :max (t/point 1. 1. 1.)}))

; ## Intersections

; This works by treating a cube as it were composed of six planes, one for each face of the cube.

; Intersecting a ray with that cube involves testing it against each of the planes,
; and if the ray intersects them in just the right way, it means that the ray intersects the cube, as well.
; - The first step is to find the t values of all the places where the ray intersects those planes.
; - For each pair of planes, there will be a minimum t closest to the ray origin, and a maximum t farther away.
; - Focus on the largest of all the minimum t values and the smallest of all the maximum t values.
; - The intersection of the ray with that square will always be those two points: the largest minimum t value and the smallest maximum t value.
; - If the largest minimum t value is greater than the smallest maximum t value, the ray misses the cube.

(defn check-axis
  [^double origin ^double direction ^double min ^double max]
  (let [t-min-numerator (- min origin)
        t-max-numerator (- max origin)
        parallel? (< (Math/abs direction) (double t/epsilon))
        t-min (if parallel?
                (* t-min-numerator (double t/infinity))
                (/ t-min-numerator direction))
        t-max (if parallel?
                (* t-max-numerator (double t/infinity))
                (/ t-max-numerator direction))]
    (if (> t-min t-max)
      [t-max t-min]
      [t-min t-max])))

(def project
  (juxt t/x t/y t/z))

(defn local-intersect
  [{:keys [local-bounds] :as cube}
   {:keys [origin direction]}] ;; ray
  (let [{:keys [min max]} (local-bounds cube)
        [x-min y-min z-min] (project min)
        [x-max y-max z-max] (project max)
        [^double x-t-min ^double x-t-max] (check-axis (t/x origin) (t/x direction) x-min x-max)
        [^double y-t-min ^double y-t-max] (check-axis (t/y origin) (t/y direction) y-min y-max)
        [^double z-t-min ^double z-t-max] (check-axis (t/z origin) (t/z direction) z-min z-max)
        t-min (clojure.core/max x-t-min y-t-min z-t-min)
        t-max (clojure.core/min x-t-max y-t-max z-t-max)]
    (if (> t-min t-max)
      []
      [(i/intersection t-min cube)
       (i/intersection t-max cube)])))

; ## Normal

;  Each face of a cube is a plane with its own normal. This normal will be the same at every point on the corresponding face.

(defn local-normal
  [_ point _]
  (let [x-abs (Math/abs (t/x point))
        y-abs (Math/abs (t/y point))
        z-abs (Math/abs (t/z point))
        maxc (max x-abs y-abs z-abs)]
    (condp = maxc
      x-abs (t/vector (t/x point) 0. 0.)
      y-abs (t/vector 0. (t/y point) 0.)
      (t/vector 0. 0. (t/z point)))))

; ## Creation

; An axis-aligned bounding box, or AABB, is a box with a special property: its sides are all aligned with the scene’s axes. 

; Two are aligned with the x axis, two with the y axis, and two with the z axis.

(def cube
  (partial sh/shape local-bounds local-intersect local-normal))
