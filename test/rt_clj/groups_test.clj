(ns rt-clj.groups-test
  (:require [clojure.test :refer :all]
            [rt-clj.groups :refer :all]
            [rt-clj.matrices :as m]
            [rt-clj.rays :as r]
            [rt-clj.shapes :as sh]
            [rt-clj.spheres :as s]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as t]))

(deftest groups-test

  (testing "Adding children to a group"
    (let [g (with-children
              (group (tr/translation 1. 2. 3.))
              [(s/sphere)])]
      (is (m/eq? (tr/translation 1. 2. 3.)
                 (:transform (:parent (first (:children g))))))))

  (testing "Intersecting a ray with an empty group"
    (is (= []
           (local-intersect (group) (r/ray (t/point 0. 0. 0.) (t/vector 0. 0. 1.))))))

  (testing "Intersecting a ray with a nonempty group"
    (let [s1 (s/sphere)
          s2 (s/sphere (tr/translation 0. 0. -3.))
          s3 (s/sphere (tr/translation 5. 0. 0.))
          g (with-children (group) [s1 s2 s3])
          r (r/ray (t/point 0. 0. -5.) (t/vector 0. 0. 1.))]
      (is (= [(nth (:children g) 1)
              (nth (:children g) 1)
              (nth (:children g) 0)
              (nth (:children g) 0)]
             (mapv :object (local-intersect g r))))))

  (testing "Intersecting a transformed group"
    (let [g (with-children
              (group (tr/scaling 2. 2. 2.))
              [(s/sphere (tr/translation 5. 0. 0.))])
          r (r/ray (t/point 10. 0. -10.) (t/vector 0. 0. 1.))]
      (is (= 2
             (count (sh/intersect g r))))))

  (testing "The boundaries of a group are the union of its children"
    (let [gr (with-children (group)
               [(s/sphere (tr/translation 1. 2. 3.))
                (s/sphere (tr/scaling 0.5 1.5 0.5))
                (s/sphere (tr/rotation-x (/ Math/PI 4.)))])
          bs ((:local-bounds gr) gr)]
      (is (t/eq? (t/point -1. -1.5 (- (Math/sqrt 2.)))
                 (:min bs)))
      (is (t/eq? (t/point 2. 3. 4.)
             (:max bs))))))
