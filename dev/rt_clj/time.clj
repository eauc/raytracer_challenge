(ns rt-clj.time)


(def records (atom {}))


(defmacro rt-time
  [stamp expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr
         end# (. System (nanoTime))
         spent# (/ (double (- end# start#)) 1000000000.0)]
     (swap! records update ~stamp (fnil #(+ % spent#) 0.))
     ret#))


(defn reset
  []
  (reset! records {}))


(comment
  (rt-time :toto (+ 43 42)))
