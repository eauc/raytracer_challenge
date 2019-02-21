(ns rt-clj.planes-spheres-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.intersections :as in]
            [rt-clj.lights :as li]
            [rt-clj.matrices :as ma]
            [rt-clj.materials :as mr]
            [rt-clj.planes :as pl]
            [rt-clj.rays :as ra]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]))

(comment
  (let [w-material (-> mr/default-material
                       (assoc :color (co/color 1. 0.9 0.9)
                              :specular 0.))
        floor (pl/plane (ma/id 4) w-material)
        wall (pl/plane (ma/mul
                         (tr/translation 0. 0. 5.)
                         (tr/rotation-x (/ Math/PI 2)))
                       w-material)
        middle (sp/sphere (tr/translation -0.5 1. 0.5)
                          (-> mr/default-material
                              (assoc :color (co/color 0.1 1. 0.5)
                                     :diffuse 0.7
                                     :specular 0.3)))
        right (sp/sphere (ma/mul (tr/translation 1.5 0.5 -0.5)
                                 (tr/scaling 0.5 0.5 0.5))
                         (-> mr/default-material
                             (assoc :color (co/color 0.5 1. 0.1)
                                    :diffuse 0.7
                                    :specular 0.3)))
        light (li/point-light (tu/point -10. 10. -10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall
                         middle right]
                        [light])
        cam (cm/camera 400 200 (/ Math/PI 3)
                       (tr/view (tu/point 0. 1.5 -5.)
                                (tu/point 0. 1. 0.)
                                (tu/vector 0. 1. 0.)))
        cv (cm/render cam world)]
    ;; print the PPM file
    (spit "./samples/planes_spheres_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows cv)))))
