; # Example: canvas as a PPM image

(ns rt-clj.canvas-ppm-example
  {:nextjournal.clerk/visibility {:code :hide :result :show}}
  (:require [clojure.java.io :as io]
            [clojure.string]
            [nextjournal.clerk :as clerk]
            [rt-clj.canvas :as cv]
            [rt-clj.colors :as cs]))

; generate a red-blue gradient in a PPM image

(let [filename "examples/img/canvas-ppm-example.png"]
  (when (.exists (io/file filename))
    (clerk/image filename)))

{:nextjournal.clerk/visibility {:code :show :result :hide}}
(defn -main[]
  (let [ ;; generate a red-blue color gradient
        cols (mapv (fn [j]
                     (mapv (fn [i]
                             [i j (cs/color (/ i 255.) 0. (/ j 255.))]) (range 256))) (range 256))
        ;; fill the canvas with the gradient
        c (reduce (fn [c row]
                    (reduce (fn [c [i j col]]
                              (cv/assoc-at c i j col)) c row)) (cv/canvas 256 256) cols)]
    ;; print the PPM file
    (spit "./examples/img/canvas-ppm-example.ppm"
      (clojure.string/join "\n" (cv/ppm-rows c)))))
