(ns rt-clj.transformations-test
  (:require [clojure.test :refer :all]
            [rt-clj.transformations :refer :all]
            [rt-clj.matrices :as m]
            [rt-clj.tuples :as t]))

(deftest transformations-test
  (testing "Multiplying a point by a translation matrix"
    (is (= (t/point 2. 1. 7.)
           (m/mul (translation 5. -3. 2.) (t/point -3. 4. 5.)))))
  
  (testing "Multiplying a point by the inverse of a translation matrix"
    (is (= (t/point -8. 7. 3.)
           (m/mul (m/inverse (translation 5. -3. 2.)) (t/point -3. 4. 5.)))))
  
  (testing "Translation does not affect vectors"
    (is (= (t/vector 1. 2. 3.)
           (m/mul (translation 5. -3. 2.) (t/vector 1. 2. 3.)))))

  (testing "A scaling matrix applied to a point"
    (is (= (t/point -8. 18. 32.)
           (m/mul (scaling 2. 3. 4.)
                  (t/point -4. 6. 8.)))))
  
  (testing "A scaling matrix applied to a vector"
    (is (= (t/vector -8. 18. 32.)
           (m/mul (scaling 2. 3. 4.)
                  (t/vector -4. 6. 8.)))))

  (testing "Rotating a point around the x axis"
    (let [p (t/point 0. 1. 0.)
          half-quarter (rotation-x (/ Math/PI 4))
          full-quarter (rotation-x (/ Math/PI 2))]
      (is (t/eq? (t/point 0. (/ (Math/sqrt 2) 2) (/ (Math/sqrt 2) 2))
                 (m/mul half-quarter p)))
      (is (t/eq? (t/point 0. 0. 1.)
                 (m/mul full-quarter p)))))
  
  (testing "The inverse of an x-rotation rotates in the opposite direction"
    (let [p (t/point 0. 1. 0.)
          half-quarter (rotation-x (/ Math/PI 4))]
      (is (t/eq? (t/point 0. (/ (Math/sqrt 2) 2) (- 0 (/ (Math/sqrt 2) 2)))
                 (m/mul (m/inverse half-quarter) p)))))
  
  (testing "Rotating a point around the y axis"
    (let [p (t/point 0. 0. 1.)
          half-quarter (rotation-y (/ Math/PI 4))
          full-quarter (rotation-y (/ Math/PI 2))]
      (is (t/eq? (t/point (/ (Math/sqrt 2) 2) 0. (/ (Math/sqrt 2) 2))
                 (m/mul half-quarter p)))
      (is (t/eq? (t/point 1. 0. 0.)
                 (m/mul full-quarter p)))))
  
  (testing "Rotating a point around the z axis"
    (let [p (t/point 0. 1. 0.)
          half-quarter (rotation-z (/ Math/PI 4))
          full-quarter (rotation-z (/ Math/PI 2))]
      (is (t/eq? (t/point (- 0. (/ (Math/sqrt 2) 2)) (/ (Math/sqrt 2) 2) 0.)
                 (m/mul half-quarter p)))
      (is (t/eq? (t/point -1. 0. 0.)
                 (m/mul full-quarter p)))))

  (testing "Shearing transformation moves x in proportion to y"
    (is (= (t/point 5. 3. 4.)
           (m/mul (shearing 1. 0. 0. 0. 0. 0.) (t/point 2. 3. 4.)))))
  
  (testing "Shearing transformation moves x in proportion to z"
    (is (= (t/point 6. 3. 4.)
           (m/mul (shearing 0. 1. 0. 0. 0. 0.) (t/point 2. 3. 4.)))))
  
  (testing "Shearing transformation moves y in proportion to x"
    (is (= (t/point 2. 5. 4.)
           (m/mul (shearing 0. 0. 1. 0. 0. 0.) (t/point 2. 3. 4.)))))
  
  (testing "Shearing transformation moves y in proportion to z"
    (is (= (t/point 2. 7. 4.)
           (m/mul (shearing 0. 0. 0. 1. 0. 0.) (t/point 2. 3. 4.)))))
  
  (testing "Shearing transformation moves z in proportion to x"
    (is (= (t/point 2. 3. 6.)
           (m/mul (shearing 0. 0. 0. 0. 1. 0.) (t/point 2. 3. 4.)))))
  
  (testing "Shearing transformation moves z in proportion to y"
    (is (= (t/point 2. 3. 7.)
           (m/mul (shearing 0. 0. 0. 0. 0. 1.) (t/point 2. 3. 4.)))))

  (testing "Individual transformations are applied in sequence"
    (let [p (t/point 1. 0. 1.)
          r (rotation-x (/ Math/PI 2))
          s (scaling 5. 5. 5.)
          t (translation 10. 5. 7.)]
      (is (t/eq? (t/point 1. -1. 0.)
                 (m/mul r p)))
      (is (t/eq? (t/point 5. -5. 0.)
                 (m/mul s (m/mul r p))))
      (is (t/eq? (t/point 15. 0. 7.)
                 (m/mul t (m/mul s (m/mul r p)))))))

  (testing "The transformation matrix for the default orientation"
    (is (= (m/id 4)
           (view (t/point 0. 0. 0.)
                 (t/point 0. 0. -1.)
                 (t/vector 0. 1. 0.)))))
  
  (testing "A view transformation matrix looking in positive z direction"
    (is (= (scaling -1. 1. -1.)
           (view (t/point 0. 0. 0.)
                 (t/point 0. 0. 1.)
                 (t/vector 0. 1. 0.)))))
  
  (testing "The view transformation moves the world"
    (is (= (translation 0. 0. -8.)
           (view (t/point 0. 0. 8.)
                 (t/point 0. 0. 0.)
                 (t/vector 0. 1. 0.)))))
  
  (testing "An arbitrary view transformation"
    (is (m/eq? (m/matrix [[-0.50709 0.50709 0.67612 -2.36643]
                          [0.76772 0.60609 0.12122 -2.82843]
                          [-0.35857 0.59761 -0.71714 0.00000]
                          [0.00000 0.00000 0.00000 1.00000]])
               (view (t/point 1. 3. 2.)
                     (t/point 4. -2. 8.)
                     (t/vector 1. 1. 0.))))))
