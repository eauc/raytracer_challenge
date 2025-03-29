; # Example: cylinders

(ns rt-clj.cylinders-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            [nextjournal.clerk :as clerk]
            [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.cylinders :as cy]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.matrices :as ma]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo]
            [rt-clj.planes :as pl])
  (:import java.lang.Math))


(let [filename "examples/img/cylinders-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
  (let [f-m (assoc mr/default-material
                   :color (co/color 0.2 0.2 0.2)
                   :reflective 0.
                   :transparency 1.
                   :refractive-index 2.
                   :shininess 300
                   :shadow? false)
        floor (pl/plane (ma/mul (tr/translation 0. 0. 0.25)
                                (tr/rotation-x (/ Math/PI 2.)))
                        f-m)
        w-m (assoc mr/default-material
                   :color (co/color 0.1 0.1 0.1)
                   :reflective 1.
                   :shininess 300)
        wall (pl/plane (tr/translation 0. -5. 0.)
                       w-m)
        cyl-1 (assoc (cy/cylinder (tr/rotation-x (/ Math/PI 1.8))
                                  (assoc mr/default-material
                                         :color (co/color 0.8 0.2 0.8)))
                     :closed? true
                     :minimum -8.
                     :maximum 3.)
        cyl-2 (assoc (cy/cylinder (ma/mul (tr/rotation-x (/ Math/PI 1.8))
                                          (tr/scaling 2. 1. 2.))
                                  (assoc mr/default-material
                                         :color (co/color 0.2 0.8 0.8)))
                     :minimum -4.5
                     :maximum 1.5)
        cyl-3 (assoc (cy/cylinder (ma/mul (tr/rotation-x (/ Math/PI 1.8))
                                          (tr/scaling 3. 1. 3.))
                                  (assoc mr/default-material
                                         :color (co/color 0.8 0.8 0.2)))
                     :minimum -1.
                     :maximum 1.)
        light-1 (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [floor wall cyl-1 cyl-2 cyl-3] [light-1])
        view (tr/view (tu/point 4. 8. 4.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* 150 resolution) (* 100 resolution) (/ Math/PI 3) view)]
    (spit "./examples/img/cylinders-example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))
