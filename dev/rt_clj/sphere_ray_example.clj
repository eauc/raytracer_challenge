(ns rt-clj.sphere-ray-example
  (:require [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.intersections :as in]
            [rt-clj.rays :as ra]
            [rt-clj.shapes :as sh]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]))

(comment
  (let [miss-col (co/color 0. 0. 0.)
        hit-col (co/color 1. 0. 0.)
        s (sp/sphere (tr/scaling 1. 0.5 1.))
        o (tu/point 3. 0. 0.)
        h 256
        w 256
        sc-min -3.
        sc-max 3.
        sc-width (- sc-max sc-min)
        pixel-step-w (/ sc-width w)
        pixel-step-h (/ sc-width h)
        hits (mapv (fn[i]
                     (mapv (fn [j]
                             (let [screen-p (tu/point -3.
                                                      (+ -3. (* i pixel-step-w))
                                                      (+ -3. (* j pixel-step-h)))
                                   ray-d (tu/sub screen-p o)
                                   ray (ra/ray o ray-d)]
                               (in/hit (sh/intersect s ray)))) (range w))) (range h))
        cv (reduce (fn [c i]
                     (reduce (fn [c j]
                               (ca/assoc-at c i j (if (get-in hits [i j])
                                                    hit-col
                                                    miss-col)))
                             c (range w)))
                   (ca/canvas w h) (range h))]
    ;; print the PPM file
    (spit "./samples/sphere_ray_example.ppm"
      (clojure.string/join "\n" (ca/ppm-rows cv)))))
