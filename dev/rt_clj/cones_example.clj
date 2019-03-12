(ns rt-clj.cones-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.cones :as cn]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.patterns :as pt]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]
            [rt-clj.planes :as pl]
            [rt-clj.colors :as c])
  (:import java.lang.Math))


(comment
  (let [f-m (assoc mr/default-material
                   :color (co/color 0.2 0.2 0.2)
                   :reflective 0.
                   :transparency 1.
                   :refractive-index 2.
                   :shininess 300
                   :shadow? false)
        floor (pl/plane (ma/mul (tr/translation 0. 0. -1.75)
                                (tr/rotation-x (/ Math/PI 2)))
                        f-m)
        w-m (assoc mr/default-material
                   :color (co/color 0.1 0.1 0.1)
                   :reflective 1.
                   :shininess 300)
        wall (pl/plane (tr/translation 0. -6. 0.)
                        w-m)
        cone-1 (assoc (cn/cone (ma/mul (tr/rotation-x (/ Math/PI 1.8))
                                       (tr/scaling 0.3 1. 0.3))
                               (assoc mr/default-material
                                      :color (co/color 0.8 0.2 0.8)))
                      :minimum -3.)
        cone-2 (assoc (cn/cone (ma/mul (tr/rotation-x (/ Math/PI 1.8))
                                       (tr/scaling 2. 1. 2.))
                                  (assoc mr/default-material
                                         :color (co/color 0.2 0.8 0.8)))
                     :minimum -2.5
                     :maximum -1.5)
        cone-3 (assoc (cn/cone (ma/mul (tr/rotation-x (/ Math/PI 1.8))
                                       (tr/scaling 2.5 1. 2.5))
                                  (assoc mr/default-material
                                         :color (co/color 0.8 0.8 0.2)))
                     :minimum -2.
                     :maximum -1.5)
        light-1 (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall cone-1 cone-2 cone-3] [light-1])
        view (tr/view (tu/point 4. 8. 2.5)
                      (tu/point 0. 0. -1.5)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* 150 resolution) (* 100 resolution) (/ Math/PI 3) view)
        cv (cm/render cam world)]
    (spit "./samples/cones_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows cv)))))
