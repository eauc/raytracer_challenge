(ns rt-clj.canvas-ppm-example
  (:require [rt-clj.canvas :as cv]
            [rt-clj.colors :as cs]))

(comment
  (let [ ;; generate a red-blue color gradient
        cols (mapv (fn [j]
                     (mapv (fn [i]
                             [i j (cs/color (/ i 255.) 0. (/ j 255.))]) (range 256))) (range 256))
        ;; fill the canvas with the gradient
        c (reduce (fn [c row]
                    (reduce (fn [c [i j col]]
                              (cv/assoc-at c i j col)) c row)) (cv/canvas 256 256) cols)]
    ;; print the PPM file
    (spit "./samples/canvas_ppm_example.ppm"
      (clojure.string/join "\n" (cv/ppm-rows c)))))
