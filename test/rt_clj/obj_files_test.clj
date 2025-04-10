(ns rt-clj.obj-files-test
  (:require [clojure.test :refer :all]
            [clojure.walk]
            [rt-clj.obj-files :refer :all]
            [rt-clj.groups :as gr]
            [rt-clj.materials :as mr]
            [rt-clj.triangles :as tg]
            [rt-clj.tuples :as t]))

(def vector-type (Class/forName "[D"))
(def matrix-type (Class/forName "[[D"))

(defn cmp-format
  [data]
  (clojure.walk/prewalk
   (fn [node]
     (cond
       (map? node) (dissoc node :local-bounds)
       (= (type node) vector-type) (into [] node)
       (= (type node) matrix-type) (mapv #(into [] %) node)
       :else node))
   data))

(deftest obj-files-test

  (testing "Ignoring unrecognized lines"
    (is (= (cmp-format {:group (assoc (gr/group) :material mr/default-material)
                        :normals []
                        :vertices []})
           (cmp-format (parse-lines ["There was a young lady named Bright"
                                     "who traveled much faster than light."
                                     "She set out one day"
                                     "in a relative way,"
                                     "and came back the previous night."])))))

  (testing "Vertex records"
    (is (= (cmp-format [(t/point -1. 1. 0.)
                        (t/point -1. 0.5 0.)
                        (t/point 1. 0. 0.)
                        (t/point 1. 1. 0.)])
           (cmp-format
            (:vertices (parse-lines ["v -1 1 0"
                                     "v -1.0000 0.5000 0.0000"
                                     "v 1 0 0"
                                     "v 1 1 0"]))))))

  (testing "Vertex normal records"
    (let [normals (:normals (parse-lines ["vn 0 0 1"
                                          "vn 0.707 0 -0.707"
                                          "vn 1 2 3"]))]
      (is (t/eq? (t/vector 0. 0. 1.)
                 (nth normals 0)))
      (is (t/eq? (t/vector 0.707 0. -0.707)
                 (nth normals 1)))
      (is (t/eq? (t/vector 1. 2. 3.)
                 (nth normals 2)))))

  (testing "Parsing triangle faces"
    (let [result (parse-lines ["v -1 1 0"
                               "v -1 0 0"
                               "v 1 0 0"
                               "v 1 1 0"
                               "f 1 2 3"
                               "f 1 3 4"])]
      (is (= (cmp-format
              (gr/with-children
                (assoc (gr/group) :material mr/default-material)
                [(tg/triangle (nth (:vertices result) 0)
                              (nth (:vertices result) 1)
                              (nth (:vertices result) 2))
                 (tg/triangle (nth (:vertices result) 0)
                              (nth (:vertices result) 2)
                              (nth (:vertices result) 3))]))
             (cmp-format
              (:group result))))))

  (testing "Triangulating polygons"
    (let [result (parse-lines ["v -1 1 0"
                               "v -1 0 0"
                               "v 1 0 0"
                               "v 1 1 0"
                               "v 0 2 0"
                               "f 1 2 3 4 5"])]
      (is (= (cmp-format
              (gr/with-children
                (assoc (gr/group) :material mr/default-material)
                [(tg/triangle (nth (:vertices result) 0)
                              (nth (:vertices result) 1)
                              (nth (:vertices result) 2))
                 (tg/triangle (nth (:vertices result) 0)
                              (nth (:vertices result) 2)
                              (nth (:vertices result) 3))
                 (tg/triangle (nth (:vertices result) 0)
                              (nth (:vertices result) 3)
                              (nth (:vertices result) 4))]))
             (cmp-format
              (:group result))))))

  (testing "Faces with normals"
    (let [result (parse-lines ["v 0 1 0"
                               "v -1 0 0"
                               "v 1 0 0"
                               "vn -1 0 0"
                               "vn 1 0 0"
                               "vn 0 1 0"
                               "f 1//3 2//1 3//2"
                               "f 1/0/3 2/102/1 3/14/2"])]
      (is (= (cmp-format
              (gr/with-children
                (assoc (gr/group) :material mr/default-material)
                [(tg/smooth-triangle (nth (:vertices result) 0)
                                     (nth (:vertices result) 1)
                                     (nth (:vertices result) 2)
                                     (nth (:normals result) 2)
                                     (nth (:normals result) 0)
                                     (nth (:normals result) 1))
                 (tg/smooth-triangle (nth (:vertices result) 0)
                                     (nth (:vertices result) 1)
                                     (nth (:vertices result) 2)
                                     (nth (:normals result) 2)
                                     (nth (:normals result) 0)
                                     (nth (:normals result) 1))]))
             (cmp-format
              (:group result))))))

  (testing "Triangles in groups"
    (let [result (parse-lines ["v -1 1 0"
                               "v -1 0 0"
                               "v 1 0 0"
                               "v 1 1 0"
                               "g FirstGroup"
                               "f 1 2 3"
                               "g SecondGroup"
                               "f 1 3 4"])]
      (is (= (cmp-format
              (gr/with-children
                (assoc (gr/group) :material mr/default-material)
                [(gr/with-children
                   (assoc (gr/group) :name "FirstGroup")
                   [(tg/triangle (nth (:vertices result) 0)
                                 (nth (:vertices result) 1)
                                 (nth (:vertices result) 2))])
                 (gr/with-children
                   (assoc (gr/group) :name "SecondGroup")
                   [(tg/triangle (nth (:vertices result) 0)
                                 (nth (:vertices result) 2)
                                 (nth (:vertices result) 3))])]))
             (cmp-format
              (:group result)))))))
