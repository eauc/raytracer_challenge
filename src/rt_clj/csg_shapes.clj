; # CSG Shapes

(ns rt-clj.csg-shapes
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.groups :as gr]
            [rt-clj.matrices :as m]
            [rt-clj.shapes :as sh]))

; [[file:../samples/csg_spheres_example.png]]
;
; [[file:../samples/csg_cube_example.png]]
;
; [[file:../samples/csg_lens_example.png]]

; ## Intersections

; A CSG union preserves all intersections on the exterior of both shapes.
;
; A CSG intersect preserves all intersections where both shapes overlap.
;
; A CSG difference preserves all intersections not exclusively inside the object on the right.

; Given a set of intersections, produce a subset of only those intersections that conform to the operation of the current CSG object.

; A ray should intersect a CSG object if it intersects any of its children.

(defmulti intersection-allowed (fn [op _ _ _] op))

(defmethod intersection-allowed :default
  [_ _ _ _]
  false)

(defmethod intersection-allowed :union
  [_ lhit inl inr]
  (or (and lhit (not inr))
      (and (not lhit) (not inl))))

(defmethod intersection-allowed :intersection
  [_ lhit inl inr]
  (or (and lhit inr)
      (and (not lhit) inl)))

(defmethod intersection-allowed :difference
  [_ lhit inl inr]
  (or (and lhit (not inr))
      (and (not lhit) inl)))

(defn includes?
  [{:keys [left right children] :as parent} child]
  (cond
    (not (nil? children))
    (some #(includes? % child) children)
    (not (nil? left))
    (or (includes? left child)
        (includes? right child))
    :else
    (= parent child)))

(defn filter-intersections
  [{:keys [operation left]} ints]
  (loop [[{:keys [object] :as int} & rest] ints
         inl false
         inr false
         result []]
    (if (nil? int)
      result
      (let [lhit (includes? left object)
            allowed? (intersection-allowed operation lhit inl inr)
            result' (if allowed? (conj result int) result)
            inl' (if lhit (not inl) inl)
            inr' (if lhit inr (not inr))]
        (recur rest inl' inr' result')))))

(defn local-intersect
  [{:keys [left right] :as shape} ray]
  (filter-intersections
   shape
   (sort-by
    :t
    (concat (sh/intersect left ray)
            (sh/intersect right ray)))))

; ## Creation

; A CSG shape is composed of an operation and two operand shapes.

(defn csg
  ([transform operation left right]
   (let [local-bounds (constantly (gr/children-bounds [left right]))
         shape (-> (sh/shape local-bounds local-intersect identity transform)
                   (assoc
                    :operation operation
                    :left left
                    :right right))]
     (assoc shape
            :left (assoc left :parent shape)
            :right (assoc right :parent shape))))
  ([operation left right]
   (csg (m/id 4) operation left right)))
