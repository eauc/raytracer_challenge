; # Shapes

(ns rt-clj.shapes
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.matrices :as m]
            [rt-clj.materials :as mr]
            [rt-clj.rays :as r]
            [rt-clj.tuples :as t]))

; ## World transformations

; `world->object` takes a point in world space and transform it to object space, taking into consideration any parent objects between the two spaces.

(defn world->object
  [{:keys [parent inverse-t] :as shape} point]
  (if (nil? shape)
    point
    (m/mul-t inverse-t (world->object parent point))))

; `object->world` takes a normal vector in object space and transform it to world space, taking into consideration any parent objects between the two spaces.

(defn object->world
  [{:keys [trans-inverse-t parent]} v]
  (let [[x y z] ((juxt t/x t/y t/z) (m/mul-t trans-inverse-t v))
        new-v (t/norm (t/vector x y z))]
    (if (nil? parent)
      new-v
      (object->world parent new-v))))

; ## Bounds
;
; To calculate the world-boudaries of a shape:
; - calculate all 8 corners of the boundaries in local space.
; - transform each corner into world space.
; - take the min and max of all =x, y, z= coordinates.

(defn bounds
  [{:keys [local-bounds transform] :as shape}]
  (let [{:keys [min max]} (local-bounds shape)
        coords (juxt t/x t/y t/z)
        [x-min y-min z-min] (coords min)
        [x-max y-max z-max] (coords max)
        corners (map #(m/mul-t transform %)
                     [(t/point x-min y-min z-min)
                      (t/point x-min y-min z-max)
                      (t/point x-min y-max z-min)
                      (t/point x-min y-max z-max)
                      (t/point x-max y-min z-min)
                      (t/point x-max y-min z-max)
                      (t/point x-max y-max z-min)
                      (t/point x-max y-max z-max)])
        xs (map t/x corners)
        ys (map t/y corners)
        zs (map t/z corners)]
    {:min (t/point (apply clojure.core/min xs)
                   (apply clojure.core/min ys)
                   (apply clojure.core/min zs))
     :max (t/point (apply clojure.core/max xs)
                   (apply clojure.core/max ys)
                   (apply clojure.core/max zs))}))

; ## Intersections

; To calculate the world-intersect, we must first transform the ray in the object coordinates.

(defn intersect [{:keys [inverse-t local-intersect] :as shape} ra]
  (let [local-ray (r/transform ra inverse-t)]
    (local-intersect shape local-ray)))

; ## Normal

; `shape/normal` must find the normal on a child object of a group, taking into account transformations on both the child object and the parent(s).

; To calculate the world-normal, we must :
; - first transform the intersection point into object-world.
; - then calculate the local-normal in object-world easily.
; - we must then transform this normal back, using the transpose inverse of the transformation matrix of the object.
; - this calculation results in a wrong =w= component, so we just trop it.
; - the resulting vector is also not normalized anymore, so we normalize the result.

(defn normal [{:keys [local-normal] :as shape} world-point hit]
  (let [local-point (world->object shape world-point)
        local-normal (local-normal shape local-point hit)]
    (object->world shape local-normal)))

; ## Creation

(defn shape
  ([local-bounds local-intersect local-normal transform material]
   (let [inverse-t (m/inverse transform)]
     {:local-bounds local-bounds
      :local-intersect local-intersect
      :local-normal local-normal
      :material material
      :transform transform
      :inverse-t inverse-t
      :trans-inverse-t (m/transpose inverse-t)}))
  ([local-bounds local-intersect local-normal transform]
   (shape local-bounds local-intersect local-normal transform mr/default-material))
  ([local-bounds local-intersect local-normal]
   (shape local-bounds local-intersect local-normal (m/id 4) mr/default-material)))
