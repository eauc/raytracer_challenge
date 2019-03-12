(ns rt-clj.camera-world-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.intersections :as in]
            [rt-clj.lights :as li]
            [rt-clj.matrices :as ma]
            [rt-clj.materials :as mr]
            [rt-clj.rays :as ra]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]))

(comment
  (let [w-material (-> mr/default-material
                       (assoc :color (co/color 1. 0.9 0.9)
                              :specular 0.))
        floor (sp/sphere (tr/scaling 10. 0.01 10.)
                         w-material)
        left-wall (sp/sphere (ma/mul
                               (tr/translation 0. 0. 5.)
                               (ma/mul
                                 (tr/rotation-y (- (/ Math/PI 4)))
                                 (ma/mul
                                   (tr/rotation-x (/ Math/PI 2))
                                   (tr/scaling 10. 0.01 10.))))
                             w-material)
        right-wall (sp/sphere (ma/mul
                                (tr/translation 0. 0. 5.)
                                (ma/mul
                                  (tr/rotation-y (/ Math/PI 4.))
                                  (ma/mul
                                    (tr/rotation-x (/ Math/PI 2.))
                                    (tr/scaling 10. 0.01 10.))))
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
        left (sp/sphere (ma/mul (tr/translation -1.5 0.33 -0.75)
                                (tr/scaling 0.33 0.33 0.33))
                        (-> mr/default-material
                            (assoc :color (co/color 1. 0.8 0.1)
                                   :diffuse 0.7
                                   :specular 0.3)))
        light (li/point-light (tu/point -10. 10. -10.) (co/color 1. 1. 1.))
        world (wo/world [floor left-wall right-wall
                         middle left right]
                        [light])
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3)
                       (tr/view (tu/point 0. 1.5 -5.)
                                (tu/point 0. 1. 0.)
                                (tu/vector 0. 1. 0.)))]
    ;; print the PPM file
    (spit "./samples/camera_world_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))
