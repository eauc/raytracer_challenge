(ns rt-clj.groups-example
  (:require [rt-clj.cameras :as cm]
            [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.cylinders :as cy]
            [rt-clj.groups :as gr]
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
  (let [mat (assoc mr/default-material
                   :color (co/color 0.8 0.2 0.8))
        cyl (assoc (cy/cylinder (tr/scaling 0.5 1. 0.5) mat)
                   :minimum -1.
                   :maximum 1.)
        sph (sp/sphere (ma/mul (tr/translation 0. 1. 0)
                               (tr/scaling 0.5 0.5 0.5)) mat)
        grp-1 (gr/group (tr/translation 1.732050 0. 0.) [cyl sph])
        grp-0 (gr/group (ma/id 4) [grp-1
                                   (gr/group (tr/rotation-z (* 1. (/ Math/PI 3.))) [grp-1])
                                   (gr/group (tr/rotation-z (* 2. (/ Math/PI 3.))) [grp-1])
                                   (gr/group (tr/rotation-z (* 3. (/ Math/PI 3.))) [grp-1])
                                   (gr/group (tr/rotation-z (* 4. (/ Math/PI 3.))) [grp-1])
                                   (gr/group (tr/rotation-z (* 5. (/ Math/PI 3.))) [grp-1])])
        grp (gr/group (ma/id 4) [grp-0
                                 (gr/group (ma/mul (tr/translation 2.5 0. 0.)
                                                   (tr/rotation-x (* 3. (/ Math/PI 4.)))) [grp-0])
                                 (gr/group (ma/mul (tr/translation -2.5 0. 0.)
                                                   (tr/rotation-x (/ Math/PI 4.))) [grp-0])])
        light (li/point-light (tu/point 10. 10. 10.) (co/color 1. 1. 1.))
        world (wo/world [grp] [light])
        view (tr/view (tu/point 5. 10. 6.)
                      (tu/point 0. 0. 0.)
                      (tu/vector 0. 0. 1.))
        resolution 4
        cam (cm/camera (* 150 resolution) (* 100 resolution) (/ Math/PI 3) view)]
    (spit
      "./samples/groups_example.ppm"
      (clojure.string/join
        "\n" (ca/ppm-rows (cm/render cam world {:parallel? true}))))))
