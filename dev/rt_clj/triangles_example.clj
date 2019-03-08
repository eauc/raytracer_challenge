(ns rt-clj.triangles-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.planes :as pl]
            [rt-clj.patterns :as pt]
            [rt-clj.triangles :as tg]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo])
  (:import java.lang.Math))

(comment
  (let [floor (pl/plane (tr/rotation-x (/ Math/PI 2.))
                        (assoc mr/default-material
                               :color (co/color 0.6 0.6 0.6)))
        wall (pl/plane (tr/translation 0. -2. 0.)
                       (assoc mr/default-material
                              :color (co/color 0.3 0.3 0.3)
                              :reflective 1.
                              :shininess 300))
        t1 (tg/triangle (tu/point 0. 0. 1.)
                        (tu/point 0. 1. 0.)
                        (tu/point 1. 0. 0.)
                        (assoc mr/default-material
                               :color (co/color 0.9 0.2 0.9)))
        t2 (tg/triangle (tu/point 0. 0. 1.)
                        (tu/point 0. -1. 0.)
                        (tu/point 1. 0. 0.)
                        (assoc mr/default-material
                               :color (co/color 0.2 0.9 0.9)))
        t3 (tg/triangle (tu/point 0. 0. 1.)
                        (tu/point 0. -1. 0.)
                        (tu/point -1. 0. 0.)
                        (assoc mr/default-material
                               :color (co/color 0.9 0.9 0.2)))
        t4 (tg/triangle (tu/point 0. 0. 1.)
                        (tu/point 0. 1. 0.)
                        (tu/point -1. 0. 0.)
                        (assoc mr/default-material
                               :color (co/color 0.2 0.9 0.2)))
        light (li/point-light (tu/point 5. 10. 10.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor wall
                         t1 t2 t3 t4] [light])
        view (tr/view (tu/point 2. 4. 2.)
                      (tu/point 0. 0. 1.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./samples/triangles_example.ppm"
          (clojure.string/join
            "\n"
            (ca/ppm-rows (cm/render cam world {:parallel? true}))))))
