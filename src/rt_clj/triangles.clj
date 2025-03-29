; # Triangles

(ns rt-clj.triangles
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.intersections :as i]
            [rt-clj.matrices :as m]
            [rt-clj.materials :as mr]
            [rt-clj.shapes :as sh]
            [rt-clj.tuples :as t]))

; ## Bounds

(defn local-bounds
  [{:keys [p1 p2 p3]}]
  (let [[[x1 y1 z1] [x2 y2 z2] [x3 y3 z3]] (map (juxt t/x t/y t/z) [p1 p2 p3])]
    {:min (t/point (min x1 x2 x3) (min y1 y2 y3) (min z1 z2 z3))
     :max (t/point (max x1 x2 x3) (max y1 y2 y3) (max z1 z2 z3))}))

; ## Intersection

; A ray that misses a triangle should not add any intersections to the intersection list.

; A ray that strikes a triangle should add exactly one intersection to the list.

; The specific algorithm that we’ll implement is the Möller–Trumbore algorithm:
; - cross the ray direction with e2,
; - then dot the result with e1 to produce the determinant.
; - if the result is close to zero, then the ray is parallel to the triangle and misses.

; An intersection record may have u and v properties, to help identify where on a triangle the intersection occurred, relative to the triangle’s corners.

(defn local-intersect
  [{:keys [p1 e1 e2] :as triangle}
   {:keys [origin direction]}]
  (let [dir><e2 (t/cross direction e2)
        d (t/dot e1 dir><e2)]
    (if (t/close? 0. d)
      []
      (let [f (/ 1. d)
            p1->origin (t/sub origin p1)
            u (* f (t/dot p1->origin dir><e2))]
        (if-not (<= 0. u 1.)
          []
          (let [origin><e1 (t/cross p1->origin e1)
                v (* f (t/dot direction origin><e1))]
            (if (or (> 0 v)
                    (< 1 (+ u v)))
              []
              [(assoc (i/intersection
                       (* f (t/dot e2 origin><e1))
                       triangle)
                      :u u :v v)])))))))

; ## Normal

; The triangle’s precomputed normal is used for every point on the triangle.

(defn local-normal
  [{:keys [normal]} _ _]
  normal)

; ## Creation

; We pre-compute 2 edges vectors and the normal vector at creation.

(defn triangle
  ([p1 p2 p3 material]
   (let [e1 (t/sub p2 p1)
         e2 (t/sub p3 p1)]
     (-> (sh/shape local-bounds local-intersect local-normal (m/id 4) material)
         (assoc
          :p1 p1 :p2 p2 :p3 p3
          :e1 e1 :e2 e2
          :normal (t/norm (t/cross e2 e1))))))
  ([p1 p2 p3]
   (triangle p1 p2 p3 mr/default-material)))

; ## Smooth Triangles

; A smooth triangle should store the triangle’s three vertex points, as well as the normal vector at each of those points.

; When computing the normal vector on a smooth triangle, use the intersection’s u and v properties to interpolate the normal.

(defn smooth-local-normal
  [{:keys [n1 n2 n3]} _ {:keys [u v]}]
  (t/add (t/add (t/mul n1 (- 1 u v))
                (t/mul n2 u))
         (t/mul n3 v)))

(defn smooth-triangle
  ([p1 p2 p3 n1 n2 n3 material]
   (let [e1 (t/sub p2 p1)
         e2 (t/sub p3 p1)]
     (-> (sh/shape local-bounds local-intersect smooth-local-normal (m/id 4) material)
         (assoc
          :p1 p1 :p2 p2 :p3 p3
          :n1 n1 :n2 n2 :n3 n3
          :e1 e1 :e2 e2))))
  ([p1 p2 p3 n1 n2 n3]
   (smooth-triangle p1 p2 p3
                    n1 n2 n3
                    mr/default-material)))
