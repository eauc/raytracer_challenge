(ns rt-clj.obj-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.obj-files :as obj-files]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.planes :as pl]
            [rt-clj.patterns :as pt]
            [rt-clj.triangles :as tg]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]
            [rt-clj.time :as rtt])
  (:import java.lang.Math))

(comment
  (let [floor (pl/plane (tr/rotation-x (/ Math/PI 2.))
                        (assoc mr/default-material
                               :color (co/color 0.3 0.1 0.3)
                               :reflective 1
                               :shininess 300))
        teapot (with-open [rdr (clojure.java.io/reader "./obj/teapot-low.obj")]
                 (obj-files/parse-lines
                   (line-seq rdr)
                   (assoc mr/default-material
                          :color (co/color 0.6 0.6 0.6)
                          :reflective 1.
                          :shininess 300)))
        light (li/point-light (tu/point 50. 100. 100.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor (:group teapot)] [light])
        view (tr/view (tu/point 20. 40. 20.)
                      (tu/point 0. 0. 5.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (rtt/reset)
    (spit "./samples/obj_teapot_low_example.ppm"
          (clojure.string/join
            "\n"
            (rtt/rt-time :total (ca/ppm-rows (cm/render cam world {:parallel? true})))))
    @rtt/records)

  (let [floor (pl/plane (tr/rotation-x (/ Math/PI 2.))
                        (assoc mr/default-material
                               :color (co/color 0.3 0.1 0.3)
                               :reflective 1
                               :shininess 300))
        teapot (with-open [rdr (clojure.java.io/reader "./obj/teapot.obj")]
                 (obj-files/parse-lines
                   (line-seq rdr)
                   (assoc mr/default-material
                          :color (co/color 0.6 0.6 0.6)
                          :reflective 1.
                          :shininess 300)))
        light (li/point-light (tu/point 50. 100. 100.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor (:group teapot)] [light])
        view (tr/view (tu/point 20. 40. 20.)
                      (tu/point 0. 0. 5.)
                      (tu/vector 0. 0. 1.))
        resolution 8
        cam (cm/camera (* resolution 75) (* resolution 50) (/ Math/PI 3) view)]
    (rtt/reset)
    (spit "./samples/obj_teapot_example.ppm"
          (clojure.string/join
            "\n"
            (rtt/rt-time
              :total
              (ca/ppm-rows (cm/render cam world {:depth 2 :parallel? true})))))
    @rtt/records))
