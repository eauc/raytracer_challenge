; # Example: reflection and refraction

; {:nextjournal.clerk/visibility {:code :hide :result :hide}}
; (set! *warn-on-reflection* true)
; (set! *unchecked-math* :warn-on-boxed)

(ns rt-clj.reflec-refrac-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            ; [criterium.core :as criterium]
            [clj-async-profiler.core :as prof]
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
            [rt-clj.planes :as pl]
            [rt-clj.cylinders :as cy])
  (:import java.lang.Math))

; ## Reflection

(let [filename "examples/img/reflection-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

; ## Refraction

(let [filename "examples/img/refraction-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

; ## Fresnel

(let [filename "examples/img/fresnel-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
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
                                   :color co/black
                                   :reflective 1.
                                   :specular 1.
                                   :shininess 300))
        sphere-2 (sp/sphere (ma/mul (tr/translation 2.25 -0.75 2.25)
                                    (tr/scaling 2. 2. 2.))
                            (assoc mr/default-material
                                   :color (co/color 0.5 0. 0.5)))
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall-1 wall-2
                         sphere-1 sphere-2] [light])
        view (tr/view (tu/point 5. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./examples/img/reflection-example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world)))))

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
                                   :color co/black
                                   :reflective 0.
                                   :transparency 1.
                                   :refractive-index 1.5
                                   :specular 1.
                                   :shininess 300
                                   :shadow? false))
        sphere-2 (sp/sphere (ma/mul (tr/translation 2. -1. 2.)
                                    (tr/scaling 2. 2. 2.))
                            (assoc mr/default-material
                                   :color (co/color 0.5 0. 0.5)))
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall-1 wall-2
                         sphere-1 sphere-2] [light])
        view (tr/view (tu/point 5. 10. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
        ; cam-crit (cm/camera 1 1 (/ Math/PI 3) view)]
    ; (println "Start profiling...")
    ; (criterium/quick-bench
    ;  (clojure.string/join "\n" (ca/ppm-rows (cm/render cam-crit world))))
    (spit
     "./examples/img/refraction-example.ppm"
     (prof/profile
      (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))

  (let [floor (pl/plane (ma/mul (tr/translation 0. 0. -10.)
                                (tr/rotation-x (/ Math/PI 2)))
                        (assoc mr/default-material
                               :color (co/color 0.8 0.8 0.8)))
        cyl (cy/cylinder (ma/mul (tr/translation -30. -60 0.)
                                 (ma/mul (tr/rotation-y (/ Math/PI 4.))
                                         (tr/rotation-x (/ Math/PI 2.))))
                         (assoc mr/default-material
                                :color (co/color 0.8 0.2 0.8)))
        plane (pl/plane (tr/rotation-x (/ Math/PI 2))
                        (assoc mr/default-material
                               :color co/black
                               :reflective 0.8
                               :transparency 0.8
                               :refraction-index 2.
                               :shadow? false))
        light-1 (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor cyl plane] [light-1])
        view (tr/view (tu/point 0. 10. 3.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
        ; cam-crit (cm/camera 1 1 (/ Math/PI 3) view)]
    ; (println "Start profiling...")
    ; (criterium/quick-bench
    ;  (clojure.string/join "\n" (ca/ppm-rows (cm/render cam-crit world))))
    (spit
     "./examples/img/fresnel-example.ppm"
     (prof/profile
      (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world)))))))
