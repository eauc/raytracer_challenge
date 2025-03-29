(ns rt-clj.spheres-test
  (:require [clojure.test :refer :all]
            [rt-clj.spheres :refer :all]
            [rt-clj.intersections :as i]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as m]
            [rt-clj.rays :as r]
            [rt-clj.shapes :as sh]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as t]))

(deftest spheres-test
  (testing "A sphere's default transformation"
    (is (= (m/id 4)
           (:transform (sphere)))))
  
  (testing "Changing a sphere's transformation"
    (let [t (tr/translation 2. 3. 4.)
          s (sphere t)]
      (:transform s)))

  (testing "A sphere has a default material"
    (is (= mr/default-material
           (:material (sphere)))))

  (testing "A ray intersects a sphere at two points"
    (let [ra (r/ray (t/point 0. 0. -5.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= 4.
             (:t (first xs))))
      (is (= 6.
             (:t (second xs))))))
  
  (testing "A ray intersects a sphere at a tangent"
    (let [ra (r/ray (t/point 0. 1. -5.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= 5.
             (:t (first xs))))
      (is (= 5.
             (:t (second xs))))))
  
  (testing "A ray misses a sphere"
    (let [ra (r/ray (t/point 0. 2. -5.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 0
             (count xs)))))
  
  (testing "A ray originates inside a sphere"
    (let [ra (r/ray (t/point 0. 0. 0.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= -1.
             (:t (first xs))))
      (is (= 1.
             (:t (second xs))))))
  
  (testing "A sphere is behind a ray"
    (let [ra (r/ray (t/point 0. 0. 5.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= -6.
             (:t (first xs))))
      (is (= -4.
             (:t (second xs))))))
  
  (testing "Intersect sets the object on the intersection"
    (let [ra (r/ray (t/point 0. 0. -5.) (t/vector 0. 0. 1.))
          s (sphere)
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= s
             (:object (first xs))))
      (is (= s
             (:object (second xs))))))

  (testing "Intersecting a scaled sphere with a ray"
    (let [ra (r/ray (t/point 0. 0. -5.) (t/vector 0. 0. 1.))
          s (sphere (tr/scaling 2. 2. 2.))
          xs (sh/intersect s ra)]
      (is (= 2
             (count xs)))
      (is (= 3.
             (:t (first xs))))
      (is (= 7.
             (:t (second xs))))))
  
  (testing "Intersecting a translated sphere with a ray"
    (let [ra (r/ray (t/point 0. 0. -5.) (t/vector 0. 0. 1.))
          s (sphere (tr/translation 5. 0. 0.))
          xs (sh/intersect s ra)]
      (is (= 0
             (count xs)))))

  (testing "The normal on a sphere at a point on the x axis"
    (is (= (t/vector 1. 0. 0.)
           (sh/normal (sphere) (t/point 1. 0. 0.) {}))))
  
  (testing "The normal on a sphere at a point on the y axis"
    (is (= (t/vector 0. 1. 0.)
           (sh/normal (sphere) (t/point 0. 1. 0.) {}))))
  
  (testing "The normal on a sphere at a point on the z axis"
    (is (= (t/vector 0. 0. 1.)
           (sh/normal (sphere) (t/point 0. 0. 1.) {}))))
  
  (testing "The normal on a sphere at a non-axial point"
    (let [p (/ (Math/sqrt 3.) 3.)]
      (is (= (t/vector p p p)
             (sh/normal (sphere) (t/point p p p) {})))))

  (testing "Computing the normal on a translated sphere"
    (is (t/eq? (t/vector 0. 0.70711 -0.70711)
               (sh/normal (sphere (tr/translation 0. 1. 0.))
                          (t/point 0, 1.70711, -0.70711)
                          {}))))
  
  (testing "Computing the normal on a scaled sphere"
    (is (t/eq? (t/vector 0. 0.97014 -0.24254)
               (sh/normal (sphere (tr/scaling 1. 0.5 1.))
                          (t/point 0, 0.70711, -0.70711)
                          {})))))
