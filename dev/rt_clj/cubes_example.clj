(ns rt-clj.cubes-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.cubes :as cu]
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
                        :color c/black
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
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./samples/cubes_example.ppm"
          (clojure.string/join "\n" (ca/ppm-rows (cm/render cam world))))))
