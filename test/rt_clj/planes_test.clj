(ns rt-clj.planes-test
  (:require [clojure.test :refer :all]
            [rt-clj.planes :refer :all]
            [rt-clj.intersections :as i]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as m]
            [rt-clj.rays :as r]
            [rt-clj.shapes :as sh]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as t]))

(deftest planes-test
  (testing "A plane's default transformation"
    (is (= (m/id 4)
           (:transform (plane)))))
  
  (testing "Changing a plane's transformation"
    (let [t (tr/translation 2. 3. 4.)
          s (plane t)]
      (:transform s)))

  (testing "A plane has a default material"
    (is (= mr/default-material
           (:material (plane)))))

  (testing "Intersect with a ray parallel to the plane"
    (is (= []
           (local-intersect
             (plane)
             (r/ray (t/point 0. 10. 0.) (t/vector 0. 0. 1.))))))
  
  (testing "Intersect with a coplanar plane"
    (is (= []
           (local-intersect
             (plane)
             (r/ray (t/point 0. 0. 0.) (t/vector 0. 0. 1.))))))
  
  (testing "A ray intersecting a plane from above"
    (let [xs (local-intersect
               (plane)
               (r/ray (t/point 0. 1. 0.) (t/vector 0. -1. 0.)))]
      (is (= 1
             (count xs)))
      (is (= 1.
             (:t (first xs))))
      (is (= (plane)
             (:object (first xs))))))
  
  (testing "A ray intersecting a plane from below"
    (let [xs (local-intersect
               (plane)
               (r/ray (t/point 0. -1. 0.) (t/vector 0. 1. 0.)))]
      (is (= 1
             (count xs)))
      (is (= 1.
             (:t (first xs))))
      (is (= (plane)
             (:object (first xs))))))

  (testing "The normal of a plane is constant everywhere"
    (is (= (t/vector 0. 1. 0.)
           (local-normal (plane) (t/point 0. 0. 0.) {})))
    (is (= (t/vector 0. 1. 0.)
           (local-normal (plane) (t/point 10. 0. -10.) {})))
    (is (= (t/vector 0. 1. 0.)
           (local-normal (plane) (t/point -5. 0. 150.) {})))))
