; # Example: CSG shapes

; {:nextjournal.clerk/visibility {:code :hide :result :hide}}
; (set! *warn-on-reflection* true)
; (set! *unchecked-math* :warn-on-boxed)

(ns rt-clj.csg-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            ; [criterium.core :as criterium]
            [clj-async-profiler.core :as prof]
            [nextjournal.clerk :as clerk]
            [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.csg-shapes :as csg]
            [rt-clj.colors :as co]
            [rt-clj.cylinders :as cy]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]
            [rt-clj.cubes :as cu])
  (:import java.lang.Math))

(let [filename "examples/img/csg-spheres-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

(let [filename "examples/img/csg-cube-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

(let [filename "examples/img/csg-lens-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
  (let [sp1 (sp/sphere (tr/scaling 3. 3. 3.)
                       (assoc mr/default-material
                              :color (co/color 0.9 0.1 0.9)))
        sp2 (sp/sphere (ma/mul (tr/translation 2. 1. 2.)
                               (tr/scaling 2. 2. 2.))
                       (assoc mr/default-material
                              :color (co/color 0.9 0.9 0.1)))
        shape (csg/csg :difference sp1 sp2)
        light (li/point-light (tu/point 5. 10. 10.)
                              (co/color 1. 1. 1.))
        world (wo/world [shape] [light])
        view (tr/view (tu/point 8. 12. 4.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./examples/img/csg-spheres-example.ppm"
          (clojure.string/join
           "\n"
           (ca/ppm-rows (cm/render cam world)))))

  (let [cyl1 (assoc (cy/cylinder (ma/id 4)
                                 (assoc mr/default-material
                                        :color (co/color 1. 0.2 0.2)))
                    :closed? true
                    :minimum -2.
                    :maximum 2.)
        cyl2 (assoc (cy/cylinder (tr/rotation-z (/ Math/PI 2.))
                                 (assoc mr/default-material
                                        :color (co/color 1. 0.2 0.2)))
                    :closed? true
                    :minimum -2.
                    :maximum 2.)
        cyl3 (assoc (cy/cylinder (tr/rotation-x (/ Math/PI 2.))
                                 (assoc mr/default-material
                                        :color (co/color 1. 0.2 0.2)))
                    :closed? true
                    :minimum -2.
                    :maximum 2.)
        cyl (csg/csg :union cyl1
                     (csg/csg :union cyl2 cyl3))
        cube (cu/cube (tr/scaling 1.9 1.9 1.9)
                      (assoc mr/default-material
                             :color (co/color 0.2 1. 0.2)))
        sphere (sp/sphere (tr/scaling 2.6 2.6 2.6)
                          (assoc mr/default-material
                                 :color (co/color 0.2 0.2 1.)))
        shape (csg/csg :intersection sphere
                       (csg/csg :difference cube cyl))
        light (li/point-light (tu/point 5. 10. 10.)
                              (co/color 1. 1. 1.))
        world (wo/world [shape] [light])
        view (tr/view (tu/point 8. 12. 4.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
        ; cam-crit (cm/camera 1 1 (/ Math/PI 3) view)]
    ; (println "Start profiling...")
    ; (criterium/quick-bench
    ;  (clojure.string/join "\n" (ca/ppm-rows (cm/render cam-crit world))))
    (spit
     "./examples/img/csg-cube-example.ppm"
     (prof/profile
      (clojure.string/join
       "\n"
       (ca/ppm-rows (cm/render cam world))))))

  (let [walls (cu/cube (ma/mul (tr/translation 11 11 11)
                               (tr/scaling 20. 20. 20.))
                       (assoc mr/default-material
                              :color (co/color 0.5 0.2 0.8)))
        cub1 (cu/cube (tr/scaling 2. 2. 2.)
                      (assoc mr/default-material
                             :color (co/color 0.3 0.9 0.3)))
        cub2 (cu/cube (ma/mul (tr/translation 0. 3. 0.)
                              (ma/mul (ma/mul (tr/rotation-x (/ Math/PI 4.))
                                              (tr/rotation-z (/ Math/PI 4.)))
                                      (tr/scaling 2. 2. 2.)))
                      (assoc mr/default-material
                             :color (co/color 0.9 0.9 0.3)))
        cub (csg/csg (ma/mul (tr/rotation-x (/ Math/PI 7.))
                             (tr/rotation-z (- (/ Math/PI 7.))))
                     :difference cub1 cub2)
        sp1 (sp/sphere (tr/scaling 0.5 1. 1.)
                       (assoc mr/default-material
                              :color co/black
                              :refractive-index 1.5
                              :shininess 300
                              :transparency 1.
                              :shadow? false))
        sp2 (sp/sphere (ma/mul (tr/translation 0.4 0. 0.)
                               (tr/scaling 0.5 1. 1.))
                       (assoc mr/default-material
                              :color co/black
                              :refractive-index 1.5
                              :shininess 300
                              :transparency 1.
                              :shadow? false))
        lens (csg/csg (ma/mul (tr/translation 3.5 3.5 0.)
                              (ma/mul (tr/rotation-z (/ Math/PI 5.))
                                      (tr/scaling 2. 2. 2.)))
                      :intersection sp1 sp2)
        sp3 (sp/sphere (tr/scaling 1. 2. 2.)
                       (assoc mr/default-material
                              :color co/black
                              :reflective 1.
                              :shininess 300
                              :transparency 1.
                              :shadow? false))
        sp4 (sp/sphere (ma/mul (tr/translation 0.1 0. 0.)
                               (tr/scaling 1. 2. 2.))
                       (assoc mr/default-material
                              :color co/black
                              :reflective 1.
                              :shininess 300))
        mirror (csg/csg (ma/mul (tr/translation -3. 2. 3.)
                                (ma/mul (ma/mul (tr/rotation-y (/ Math/PI 7.))
                                                (tr/rotation-z (/ Math/PI 7.)))
                                        (tr/scaling 1.5 1.5 1.5)))
                        :difference sp3 sp4)
        l1 (li/point-light (tu/point 5. 10. 10.)
                           (co/color 0.6 0.6 0.6))
        l2 (li/point-light (tu/point -5. 10. 10.)
                           (co/color 0.4 0.4 0.4))
        world (wo/world [walls cub mirror lens] [l1 l2])
        view (tr/view (tu/point 8. 10. 4.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./examples/img/csg-lens-example.ppm"
          (clojure.string/join
           "\n"
           (ca/ppm-rows (cm/render cam world))))))
