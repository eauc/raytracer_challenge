(ns rt-clj.lights-test
  (:require [clojure.test :refer :all]
            [rt-clj.lights :refer :all]
            [rt-clj.colors :as c]
            [rt-clj.tuples :as t]))

(deftest lights-test
  (testing "A point light has a position and intensity"
    (let [intensity (c/color 1. 1. 1.)
          position (t/point 0. 0. 0.)
          light (point-light position intensity)]
      (is (= position
             (:position light)))
      (is (= intensity
             (:intensity light))))))
