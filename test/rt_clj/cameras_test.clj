(ns rt-clj.cameras-test
  (:require [clojure.test :refer :all]
            [rt-clj.cameras :refer :all]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.matrices :as m]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as t]
            [rt-clj.worlds :as w]))

(deftest cameras-test
  (testing "Constructing a camera"
    (let [cam (camera 160 120 (/ Math/PI 2))]
      (is (= 160
             (:hsize cam)))
      (is (= 120
             (:vsize cam)))
      (is (= (/ Math/PI 2)
             (:fov cam)))
      (is (= (m/id 4)
             (:transform cam)))))

  (testing "The pixel size for a horizontal canvas"
    (is (t/close? 0.01
                  (:pixel-size (camera 200. 125. (/ Math/PI 2))))))
  
  (testing "The pixel size for a horizontal canvas"
    (is (t/close? 0.01
                  (:pixel-size (camera 125. 200. (/ Math/PI 2))))))

  (testing "Construct a ray through the center of the canvas"
    (let [cam (camera 201. 101 (/ Math/PI 2))
          ray (pixel-ray cam 100 50)]
      (is (= (t/point 0. 0. 0.)
             (:origin ray)))
      (is (t/eq? (t/vector 0. 0. -1.)
                 (:direction ray)))))
  
  (testing "Construct a ray through a corner of the canvas"
    (let [cam (camera 201. 101 (/ Math/PI 2))
          ray (pixel-ray cam 0 0)]
      (is (= (t/point 0. 0. 0.)
             (:origin ray)))
      (is (t/eq? (t/vector 0.66519 0.33259 -0.66851)
                 (:direction ray)))))
  
  (testing "Construct a ray when the camera is transformed"
    (let [cam (camera 201. 101 (/ Math/PI 2)
                      (m/mul (tr/rotation-y (/ Math/PI 4))
                             (tr/translation 0. -2. 5.)))
          ray (pixel-ray cam 100 50)]
      (is (= (t/point 0. 2. -5.)
             (:origin ray)))
      (is (t/eq? (t/vector (/ (Math/sqrt 2) 2) 0. (- (/ (Math/sqrt 2) 2)))
                 (:direction ray)))))

  (testing "Rendering a world with a camera"
    (let [world (w/default-world)
          cam (camera 11. 11. (/ Math/PI 2)
                      (tr/view (t/point 0. 0. -5.)
                               (t/point 0. 0. 0.)
                               (t/vector 0. 1. 0.)))]
      (is (t/eq? (co/color 0.38066, 0.47583, 0.2855)
                 (ca/get-at (render cam world) 5 5))))))
