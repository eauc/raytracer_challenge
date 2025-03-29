; # Groups

(ns rt-clj.groups
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.cubes :as cu]
            [rt-clj.matrices :as m]
            [rt-clj.shapes :as sh]
            [rt-clj.tuples :as t]))

; ## Bounds

(def local-bounds
  (constantly {:min (t/point (- t/epsilon) (- t/epsilon) (- t/epsilon))
               :max (t/point t/epsilon t/epsilon t/epsilon)}))

(defn children-bounds
  [cs]
  (let [bs (map sh/bounds cs)
        min-xs (map (comp t/x :min) bs)
        min-ys (map (comp t/y :min) bs)
        min-zs (map (comp t/z :min) bs)
        max-xs (map (comp t/x :max) bs)
        max-ys (map (comp t/y :max) bs)
        max-zs (map (comp t/z :max) bs)]
    {:min (t/point (apply min min-xs)
                   (apply min min-ys)
                   (apply min min-zs))
     :max (t/point (apply max max-xs)
                   (apply max max-ys)
                   (apply max max-zs))}))

; ## Children

; Children contains a reference to their parent group.

(defn with-parent
  [sh {:keys [material] :as p}]
  (let [new-sh (cond-> sh
                 :always (assoc :parent p)
                 (some? material) (assoc :material material))]
    (assoc new-sh :children (mapv #(with-parent % new-sh) (:children new-sh)))))

(defn with-children
  [gr cs]
  (let [new-gr (cond-> gr
                 (seq cs)
                 (assoc :local-bounds (constantly (children-bounds cs))))]
    (assoc new-gr :children (mapv #(with-parent % new-gr) cs))))

; ## Intersections

; Intersecting a ray with a empty group should always return no intersections.

; Otherwise, it should returns the conjunction of all intersections with each child shape, sorted by increasing distance.

; It should correctly apply the group and its children transformations.

(defn local-intersect
  [{:keys [children] :as g} r]
  (let [bounds-miss? (empty? (cu/local-intersect g r))]
    (if bounds-miss?
      []
      (into []
            (sort-by :t
                     (reduce (fn [ints c]
                               (concat ints (sh/intersect c r))) '() children))))))

; ## Creation

(defn group
  ([transform children]
   (with-children
     (sh/shape local-bounds local-intersect identity transform nil)
     children))
  ([transform]
   (group transform []))
  ([]
   (group (m/id 4) [])))
