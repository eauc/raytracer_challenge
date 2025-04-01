(ns rt-clj.shapes-test
  (:require [clojure.test :refer :all]
            [rt-clj.shapes :refer :all]
            [rt-clj.cubes :as cu]
            [rt-clj.groups :as gr]
            [rt-clj.rays :as r]
            [rt-clj.spheres :as s]
            [rt-clj.transformations :as tr]
            [rt-clj.triangles :as tg]
            [rt-clj.tuples :as t]))

(deftest shapes-test

  (testing "Converting a point from world to object space"
    (let [sphere (s/sphere (tr/translation 5. 0. 0.))
          g2 (gr/group (tr/scaling 2. 2. 2.) [sphere])
          g1 (gr/group (tr/rotation-y (/ Math/PI 2.)) [g2])]
      (is (t/eq? (t/point 0. 0. -1.)
                 (world->object (first (:children (first (:children g1))))
                                (t/point -2. 0. -10.))))))

  (testing "Converting a normal from object to world space"
    (let [sphere (s/sphere (tr/translation 5. 0. 0.))
          g2 (gr/group (tr/scaling 1. 2. 3.) [sphere])
          g1 (gr/group (tr/rotation-y (/ Math/PI 2.)) [g2])
          sqrt3-on3 (/ (Math/sqrt 3.) 3.)]
      (is (t/eq? (t/vector 0.285714 0.428571 -0.857142)
                 (object->world (first (:children (first (:children g1))))
                                (t/vector sqrt3-on3 sqrt3-on3 sqrt3-on3))))))

  (testing "Finding the normal on a child object"
    (let [sphere (s/sphere (tr/translation 5. 0. 0.))
          g2 (gr/group (tr/scaling 1. 2. 3.) [sphere])
          g1 (gr/group (tr/rotation-y (/ Math/PI 2.)) [g2])]
      (is (t/eq? (t/vector 0.285703 0.428543 -0.857160)
                 (normal (first (:children (first (:children g1))))
                         (t/point 1.7321 1.1547 -5.5774)
                         {})))))

  (testing "Boundaries of a sphere with translation"
    (let [{:keys [min max]} (bounds (s/sphere (tr/translation 0.5 1. 1.5)))]
      (is (t/eq? (t/point -0.5 0. 0.5)
                 min))
      (is (t/eq? (t/point 1.5 2. 2.5)
                 max))))

  (testing "Boundaries of a sphere with rotation"
    (let [bs (bounds (s/sphere (tr/rotation-x (/ Math/PI 4.))))]
      (is (t/eq? (t/point 1. (Math/sqrt 2.) (Math/sqrt 2.))
                 (:max bs)))
      (is (t/eq? (t/point -1. (- (Math/sqrt 2.)) (- (Math/sqrt 2.)))
                 (:min bs)))))

  (testing "Boundaries of a cube with scaling"
    (let [{:keys [min max]} (bounds (cu/cube (tr/scaling 0.5 1.5 2.)))]
      (is (t/eq? (t/point -0.5 -1.5 -2.)
                 min))
      (is (t/eq? (t/point 0.5 1.5 2.)
                 max))))

  (testing "Boundaries of a triangle"
    (let [{:keys [min max]} (bounds (tg/triangle (t/point -1. -2. -3.)
                                                 (t/point 1. 2. -3.)
                                                 (t/point 3. -2. -1.)))]
      (is (t/eq? (t/point -1. -2. -3.)
                 min))
      (is (t/eq? (t/point 3. 2. -1.)
                 max)))))

