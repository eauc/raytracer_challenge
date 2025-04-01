(ns rt-clj.rays-test
  (:require [clojure.test :refer :all]
            [rt-clj.rays :refer :all]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as t]))

(deftest rays-test
  (testing "Creating and querying a ray"
    (let [o (t/point 1. 2. 3.)
          d (t/vector 4. 5. 6.)
          r (ray o d)]
      (is (= o
             (:origin r)))
      (is (= d
             (:direction r)))))

  (testing "Computing a point from a distance"
    (let [r (ray (t/point 2. 3. 4.) (t/vector 1. 0. 0.))]
      (is (t/eq? (t/point 2. 3. 4.)
                 (pos r 0)))
      (is (t/eq? (t/point 3. 3. 4.)
                 (pos r 1)))
      (is (t/eq? (t/point 1. 3. 4.)
                 (pos r -1)))
      (is (t/eq? (t/point 4.5 3. 4.)
                 (pos r 2.5)))))

  (testing "Translating a ray"
    (let [r (ray (t/point 1. 2. 3.) (t/vector 0. 1. 0.))
          mt (tr/translation 3. 4. 5.)
          r2 (transform r mt)]
      (is (t/eq? (t/point 4. 6. 8.)
             (:origin r2)))
      (is (t/eq? (t/vector 0. 1. 0.)
             (:direction r2)))))

  (testing "Scaling a ray"
    (let [r (ray (t/point 1. 2. 3.) (t/vector 0. 1. 0.))
          mt (tr/scaling 2. 3. 4.)
          r2 (transform r mt)]
      (is (t/eq? (t/point 2. 6. 12.)
             (:origin r2)))
      (is (t/eq? (t/vector 0. 3. 0.)
             (:direction r2))))))
