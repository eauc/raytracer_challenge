; # Example: cubes

{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)

(ns rt-clj.cubes-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            [criterium.core :as criterium]
            [clj-async-profiler.core :as prof]
            [nextjournal.clerk :as clerk]
            [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.cubes :as cu]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo])
  (:import java.lang.Math))

(let [filename "examples/img/cubes-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
  ;; stripes
  (let [material (assoc mr/default-material
                        :color (co/color 0.8 0. 0.8)
                        :ambient 0.)
        room (cu/cube (tr/scaling 40. 40. 40.) material)
        material (assoc mr/default-material
                        :color (co/color 0. 0.8 0.3)
                        :ambient 0.)
        floor (cu/cube (ma/mul
                        (tr/scaling 41. 41. 39.)
                        (ma/mul
                         (tr/rotation-y (/ Math/PI 4))
                         (tr/rotation-z (/ Math/PI 4)))) material)
        material (assoc mr/default-material
                        :color co/black
                        :reflective 1.
                        :specular 1.
                        :shininess 300)
        cube (cu/cube (ma/mul
                       (tr/scaling 2. 2. 2.)
                       (ma/mul
                        (tr/rotation-z (/ Math/PI 5))
                        (tr/rotation-y (/ Math/PI 4)))) material)
        light (li/point-light (tu/point 20. 7. 20.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor room cube] [light])
        view (tr/view (tu/point 7. 9. 5.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)
        cam-crit (cm/camera 1 1 (/ Math/PI 3) view)]
    (println "Start profiling...")
    (criterium/quick-bench
     (clojure.string/join "\n" (ca/ppm-rows (cm/render cam-crit world))))
    (prof/profile
     (spit
      "./examples/img/cubes-example.ppm"
      (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world)))))))
