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

(defn x ^double [v]
  (first v))

(defn y ^double [v]
  (second v))

(defn z ^double [v]
  (nth v 2))

(defn w ^double [v]
  (nth v 3))

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

(def epsilon
  (double 10e-6))

(def infinity
  (double 10e300))

(defn close? [^double a ^double b]
  (> (double epsilon) (Math/abs (- a b))))

; Then we need close equality of 2 tuples.

(defn eq? [a b]
  (every? true? (map close? a b)))

; Tuples support basic addition & substraction.

(def add (partial mapv +))

(def sub (partial mapv -))

; Vectors can be negated, multiplied and divided by scalars.

(def neg (partial sub zerov))

(defn mul [v ^double s]
  (mapv (fn [^double e] (* e s)) v))

(defn div [v ^double s]
  (mapv (fn [^double e] (/ e s)) v))

; We can get the dot and cross products of vectors.

(defn dot ^double [v w]
  (let [c (min (count v) (count w))]
      (loop [k 0
             sum 0.]
        (if (= k c)
          sum
          (let [^double a (nth v k)
                ^double b (nth w k)]
            (recur (inc k) (+ sum (* a b))))))))

(defn cross [[^double x1 ^double y1 ^double z1]
             [^double x2 ^double y2 ^double z2]]
  (vector (- (* y1 z2)
             (* y2 z1))
          (- (* z1 x2)
             (* z2 x1))
          (- (* x1 y2)
             (* x2 y1))))

; We can get the magnitude of a vector.

(defn mag ^double [v]
  (Math/sqrt (dot v v)))

; We can normalize a vector.

(defn norm [v]
  (div v (mag v)))

; ## Reflection

; Vectors can be reflected on a surface defined by a normal.

(defn reflect [in normal]
  (let [k (* 2. (dot in normal))]
    (mapv (fn [^double i ^double n] (- i (* k n))) in normal)))
