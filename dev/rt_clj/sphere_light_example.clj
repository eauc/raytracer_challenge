(ns rt-clj.sphere-light-example
  (:require [rt-clj.canvas :as ca]
            [rt-clj.colors :as co]
            [rt-clj.intersections :as in]
            [rt-clj.lights :as li]
            [rt-clj.materials :as mr]
            [rt-clj.rays :as ra]
            [rt-clj.spheres :as sp]
            [rt-clj.transformations :as tr]
            [rt-clj.tuples :as tu]))

(comment
  (let [miss-col (co/color 0. 0. 0.)
        l (li/point-light (tu/point 10. 10. -10.) (co/color 1. 1. 1.))
        m (-> mr/default-material
              (assoc :color (co/color 1. 0.2 1.)))
        s (-> (sp/sphere (tr/scaling 1. 1. 0.5))
              (assoc :material m))
        e (tu/point 3. 0. 0.)
        h 512
        w 512
        sc-min -3.
        sc-max 3.
        sc-width (- sc-max sc-min)
        pixel-step-w (/ sc-width w)
        pixel-step-h (/ sc-width h)
        pixel (fn [i j]
                (let [screen-p (tu/point -3.
                                         (+ -3. (* i pixel-step-w))
                                         (+ -3. (* j pixel-step-h)))
                      ray-d (tu/norm (tu/sub screen-p e))
                      ray (ra/ray e ray-d)
                      hit? (in/hit (sp/intersect s ray))]
                  (if (nil? hit?)
                    miss-col
                    (let [position (ra/pos ray (:t hit?))
                          normal (sp/normal s position)]
                      (mr/lighting m l position ray-d normal)))))
        pixs (mapv (fn[i]
                     (mapv (fn [j]
                             (pixel i j))
                           (range w)))
                   (range h))
        cv (reduce (fn [c i]
                     (reduce (fn [c j]
                               (ca/assoc-at c i j (get-in pixs [i j])))
                             c (range w)))
                   (ca/canvas w h) (range h))]
    ;; print the PPM file
    (spit "./samples/sphere_light_example.ppm"
      (clojure.string/join "\n" (ca/ppm-rows cv)))))
