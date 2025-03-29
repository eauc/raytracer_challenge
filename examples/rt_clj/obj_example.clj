; # Examples: OBJ files

(ns rt-clj.obj-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            [nextjournal.clerk :as clerk]
            [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.obj-files :as obj-files]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.planes :as pl]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]
            [rt-clj.worlds :as wo])
  (:import java.lang.Math))

(let [filename "examples/img/obj-teapot-low-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

(let [filename "examples/img/obj-teapot-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main []
  (let [floor (pl/plane (tr/rotation-x (/ Math/PI 2.))
                        (assoc mr/default-material
                               :color (co/color 0.3 0.1 0.3)
                               :reflective 1
                               :shininess 300))
        teapot (with-open [rdr (clojure.java.io/reader "./examples/obj/teapot-low.obj")]
                 (obj-files/parse-lines
                  (line-seq rdr)
                  (assoc mr/default-material
                         :color (co/color 0.6 0.6 0.6)
                         :reflective 1.
                         :shininess 300)))
        light (li/point-light (tu/point 50. 100. 100.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor (:group teapot)] [light])
        view (tr/view (tu/point 20. 40. 20.)
                      (tu/point 0. 0. 5.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* resolution 150) (* resolution 100) (/ Math/PI 3) view)]
    (spit "./examples/img/obj-teapot-low-example.ppm"
          (clojure.string/join
           "\n"
           (ca/ppm-rows (cm/render cam world {:parallel? true})))))

  (let [floor (pl/plane (tr/rotation-x (/ Math/PI 2.))
                        (assoc mr/default-material
                               :color (co/color 0.3 0.1 0.3)
                               :reflective 1
                               :shininess 300))
        teapot (with-open [rdr (clojure.java.io/reader "./examples/obj/teapot.obj")]
                 (obj-files/parse-lines
                  (line-seq rdr)
                  (assoc mr/default-material
                         :color (co/color 0.6 0.6 0.6)
                         :reflective 1.
                         :shininess 300)))
        light (li/point-light (tu/point 50. 100. 100.)
                              (co/color 1. 1. 1.))
        world (wo/world [floor (:group teapot)] [light])
        view (tr/view (tu/point 20. 40. 20.)
                      (tu/point 0. 0. 5.)
                      (tu/vector 0. 0. 1.))
        resolution 8
        cam (cm/camera (* resolution 75) (* resolution 50) (/ Math/PI 3) view)]
    (spit "./examples/img/obj-teapot-example.ppm"
          (clojure.string/join
           "\n"
           (ca/ppm-rows (cm/render cam world {:depth 2 :parallel? true}))))))
