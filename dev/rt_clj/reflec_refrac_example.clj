(ns rt-clj.reflec-refrac-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
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
  (let [stripes (pt/stripes (co/color 0. 0.8 0.3) co/white)
        material (-> mr/default-material (assoc :pattern stripes))
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        checker (pt/checker (co/color 0. 0.3 0.8) co/white)
        material (-> mr/default-material (assoc :pattern checker))
        wall-1 (pl/plane (tr/translation 0. -10. 0.) material)
        rings (pt/rings (co/color 0.5 0.0 0.5) co/white)
        material (-> mr/default-material (assoc :pattern rings))
        wall-2 (pl/plane (ma/mul (tr/translation -10. 0. 0.)
                                 (tr/rotation-z (/ Math/PI 2))) material)
        sphere-1 (sp/sphere (tr/scaling 3. 3. 3.)
                            (assoc mr/default-material
                                   :color c/black
                                   :reflective 1.
                                   :specular 1.
                                   :shininess 300))
        sphere-2 (sp/sphere (ma/mul (tr/translation 2.25 -0.75 2.25)
                                    (tr/scaling 2. 2. 2.))
                            (assoc mr/default-material
                                   :color (c/color 0.5 0. 0.5)))
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall-1 wall-2
                         sphere-1 sphere-2] [light])
        view (tr/view (tu/point 5. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        cam (cm/camera 600 450 (/ Math/PI 3) view)
        cv (cm/render cam world)]
    (spit "./samples/reflection_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows cv))))


  (let [stripes (pt/stripes (co/color 0. 0.8 0.3) co/white)
        material (-> mr/default-material (assoc :pattern stripes))
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        checker (pt/checker (co/color 0. 0.3 0.8) co/white)
        material (-> mr/default-material (assoc :pattern checker))
        wall-1 (pl/plane (tr/translation 0. -10. 0.) material)
        rings (pt/rings (co/color 0.5 0.0 0.5) co/white)
        material (-> mr/default-material (assoc :pattern rings))
        wall-2 (pl/plane (ma/mul (tr/translation -10. 0. 0.)
                                 (tr/rotation-z (/ Math/PI 2))) material)
        sphere-1 (sp/sphere (tr/scaling 3. 3. 3.)
                            (assoc mr/default-material
                                   :color c/black
                                   :reflective 0.
                                   :transparency 1.
                                   :refractive-index 1.5
                                   :specular 1.
                                   :shininess 300))
        sphere-2 (sp/sphere (ma/mul (tr/translation 2. -1. 2.)
                                    (tr/scaling 2. 2. 2.))
                            (assoc mr/default-material
                                   :color (c/color 0.5 0. 0.5)))
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall-1 wall-2
                         sphere-1 sphere-2] [light])
        view (tr/view (tu/point 5. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        cam (cm/camera 600 450 (/ Math/PI 3) view)
        cv (cm/render cam world)]
    (spit "./samples/refraction_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows cv))))


  (let [checker (pt/checker (co/color 0. 0.3 0.8) co/white)
        material (-> mr/default-material (assoc :pattern checker))
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        plane (pl/plane (tr/rotation-x (/ Math/PI 2))
                        (assoc mr/default-material
                               :color c/black
                               :reflective 0.8
                               :transparency 0.8
                               :refraction-index 2.))
        sphere (sp/sphere (ma/mul (tr/translation 1. 1. -2.)
                                  (tr/scaling 2. 2. 2.))
                          (assoc mr/default-material
                                 :color (c/color 0.5 0. 0.5)))
        light-1 (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        light-2 (li/point-light (tu/point 10. 10. -0.01) (co/color 1. 1. 1.))
        world (wo/world [floor sphere plane] [light-1 light-2])
        view (tr/view (tu/point 5. 10. 3.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        cam (cm/camera 600 450 (/ Math/PI 3) view)
        cv (cm/render cam world)]
    (spit "./samples/fresnel_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows cv)))))
