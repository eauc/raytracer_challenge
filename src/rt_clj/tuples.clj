; # Tuples

(ns rt-clj.tuples
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:import java.lang.Math)
  (:refer-clojure :exclude [vector vector?]))

; ## Creation

; We can create tuples, access their coordinates, and check whether they are points or vectors.

; Tuples are simple clojure vectors.

(def tuple clojure.core/vector)

(def x first)

(def y second)

(def z #(nth % 2))

(def w #(nth % 3))

(defn point? [tup]
  (= 1.0 (w tup)))

(defn vector? [tup]
  (= 0.0 (w tup)))

; We can also create point and vectors directly.

(defn point [x y z]
  (tuple x y z 1.0))

(defn vector [x y z]
  (tuple x y z 0.0))

(def origin (point 0. 0. 0.))

(def zerov (vector 0. 0. 0.))

; ## Basic operations

; We need to define close equality for 2 floating-point scalars.

(def epsilon 10e-6)

(def infinity 10e300)

(defn close? [a b]
  (> epsilon (Math/abs (- a b))))

; Then we need close equality of 2 tuples.

(defn eq? [a b]
  (every? true? (map close? a b)))

; Tuples support basic addition & substraction.

(def add (partial mapv +))

(def sub (partial mapv -))

; Vectors can be negated, multiplied and divided by scalars.

(def neg (partial sub zerov))

(defn mul [v s]
  (mapv #(* % s) v))

(defn div [v s]
  (mapv #(/ % s) v))

; We can get the dot and cross products of vectors.

(defn dot [v w]
  (reduce + (map * v w)))

(defn cross [[x1 y1 z1]
             [x2 y2 z2]]
  (vector (- (* y1 z2)
             (* y2 z1))
          (- (* z1 x2)
             (* z2 x1))
          (- (* x1 y2)
             (* x2 y1))))

; We can get the magnitude of a vector.

(defn mag [v]
  (Math/sqrt (dot v v)))

; We can normalize a vector.

(defn norm [v]
  (div v (mag v)))

; ## Reflection

; Vectors can be reflected on a surface defined by a normal.

(defn reflect [in normal]
  (sub in (mul normal (* 2 (dot in normal)))))
