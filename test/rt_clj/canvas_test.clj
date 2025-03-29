(ns rt-clj.canvas-test
  (:require [clojure.test :refer :all]
            [rt-clj.canvas :refer :all]
            [rt-clj.colors :as c]))

(deftest canvas-test
  (testing "Canvas creation"
    (let [c (canvas 10 20)]
      (is (= 10
             (width c)))
      (is (= 20
             (height c)))))

  (testing "Writing pixels to canvas"
    (is (= (c/color 0.1 0.2 0.3)
           (-> (canvas 10 20)
               (assoc-at 5 13 (c/color 0.1 0.2 0.3))
               (get-at 5 13)))))

  (testing "Constructing the PPM header"
    (is (= ["P3"
            "5 3"
            "255"]
           (take 3 (ppm-rows (canvas 5 3))))))

  (testing "Constructing the PPM pixel data"
    (let [c1 (c/color 1.5 0. 0.)
          c2 (c/color 0. 0.5 0.)
          c3 (c/color -0.5 0. 1.)
          c (-> (canvas 5 3)
                (assoc-at 0 0 c1)
                (assoc-at 2 1 c2)
                (assoc-at 4 2 c3))]
      (is (= ["255 0 0 0 0 0 0 0 0 0 0 0 0 0 0"
              "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0"
              "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255"
              ""]
             (subvec (ppm-rows c) 3)))))

  (testing "Splitting long lines in PPM files"
    (let [cv (canvas 10 2 (c/color 1. 0.8 0.6))]
      (is (= ["255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204"
              "153 255 204 153 255 204 153 255 204 153 255 204 153"
              "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204"
              "153 255 204 153 255 204 153 255 204 153 255 204 153"
              ""]
             (subvec (ppm-rows cv) 3))))))
