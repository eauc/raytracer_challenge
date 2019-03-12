(ns rt-clj.patterns-example
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
            [rt-clj.planes :as pl])
  (:import java.lang.Math))

(comment
  ;; stripes
  (let [stripes (pt/stripes (co/color 0. 0.8 0.3) co/white
                            (tr/scaling 0.5 0.5 0.5))
        material (-> mr/default-material (assoc :pattern stripes))
        sphere (sp/sphere (tr/scaling 4. 4. 4.) material)
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor sphere] [light])
        view (tr/view (tu/point 7. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit
      "./samples/patterns_stripes_example.ppm"
      (clojure.string/join
        "\n" (ca/ppm-rows (cm/render cam world {:parallel? true})))))


  (let [gradient (pt/gradient (co/color 1. 0. 0.) (co/color 0. 0. 1.)
                              (ma/mul (tr/translation 1. 0. 0.)
                                      (tr/scaling 2. 2. 2.)))
        material (-> mr/default-material (assoc :pattern gradient))
        sphere (sp/sphere (tr/scaling 4. 4. 4.) material)
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor sphere] [light])
        view (tr/view (tu/point 7. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./samples/patterns_gradient_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world)))))


  (let [rings (pt/rings (co/color 0. 0.8 0.3) co/white
                        (tr/scaling 0.33 0.33 0.33))
        material (-> mr/default-material (assoc :pattern rings))
        sphere (sp/sphere (tr/scaling 4. 4. 4.) material)
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        world (wo/world [floor sphere] [light])
        view (tr/view (tu/point 12. 7. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./samples/patterns_rings_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world)))))


  (let [checker (pt/checker (co/color 0. 0.3 0.8) co/white)
        material (-> mr/default-material (assoc :pattern checker))
        sphere (sp/sphere (tr/scaling 4. 4. 4.) material)
        floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        material)
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor sphere] [light])
        view (tr/view (tu/point 12. 7. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./samples/patterns_checker_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))
