; # Examples: patterns

; {:nextjournal.clerk/visibility {:code :hide :result :hide}}
; (set! *warn-on-reflection* true)
; (set! *unchecked-math* :warn-on-boxed)

(ns rt-clj.patterns-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            [nextjournal.clerk :as clerk]
            [rt-clj.cameras :as cm]
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

; ## Stripes

(let [filename "examples/img/patterns-stripes-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

; ## Gradient

(let [filename "examples/img/patterns-gradient-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

; ## Rings

(let [filename "examples/img/patterns-rings-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

; ## Checker

(let [filename "examples/img/patterns-checker-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
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
      "./examples/img/patterns-stripes-example.ppm"
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
    (spit "./examples/img/patterns-gradient-example.ppm"
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
    (spit "./examples/img/patterns-rings-example.ppm"
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
    (spit "./examples/img/patterns-checker-example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))
