(ns rt-clj.cones-test
  (:require [clojure.test :refer :all]
            [rt-clj.cones :refer :all]
            [rt-clj.rays :as r]
            [rt-clj.tuples :as t]))

(deftest cones-test

  (testing "Intersecting a cone with a ray"
    (are [origin direction t]
        (t/eq? t
               (mapv :t (local-intersect (cone) (r/ray origin (t/norm direction)))))
        (t/point 0. 0. -5.) (t/vector 0. 0. 1.) [5. 5.]
        (t/point 0. 0. -5.) (t/vector 1. 1. 1.) [8.660254 8.660254]
        (t/point 1. 1. -5.) (t/vector -0.5, -1. 1.) [4.550055 49.449944]))
  
  (testing "Intersecting a cone with a ray parallel to one of its halves"
    (is (t/eq? [0.353553]
               (mapv :t (local-intersect (cone) (r/ray (t/point 0. 0. -1.)
                                                       (t/norm (t/vector 0. 1. 1.))))))))
  
  (testing "Intersecting a cone's end caps"
    (let [c (assoc (cone)
                   :minimum -0.5
                   :maximum 0.5
                   :closed? true)]
      (are [origin direction cnt]
          (= cnt
             (count (local-intersect c (r/ray origin (t/norm direction)))))
          (t/point 0. 0. -5.) (t/vector 0. 1. 0.) 0
          (t/point 0. 0. -0.25) (t/vector 0. 1. 1.) 2
          (t/point 0. 0. -0.25) (t/vector 0. 1. 0.) 4)))

  (testing "Computing the normal vector on a cone"
    (are [point n]
        (= n
           (local-normal (cone) point {}))
        (t/point 0. 0. 0.) (t/vector 0. 0. 0.)
        (t/point 1. 1. 1.) (t/vector 1. (- (Math/sqrt 2.)) 1.)
        (t/point -1. -1. 0.) (t/vector -1. 1. 0.))))
