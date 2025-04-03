; # Cones

(ns rt-clj.cones
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:import java.lang.Math)
  (:require [rt-clj.intersections :as i]
            [rt-clj.matrices :as m]
            [rt-clj.materials :as mr]
            [rt-clj.shapes :as sh]
            [rt-clj.tuples :as t]))

; ## Bounds

(defn local-bounds
  [{:keys [^double minimum ^double maximum]}]
  (let [max-abs (max (Math/abs minimum)
                     (Math/abs maximum))]
    {:min (t/point (- max-abs) minimum (- max-abs))
     :max (t/point max-abs maximum max-abs)}))

; ## Intersections

; The same as cylinders, except the radius of the cone is the absolute value of `y`.

(defn check-cap
  [{:keys [origin direction]} ^double t ^double y]
  (let [x (+ (t/x origin) (* t (t/x direction)))
        z (+ (t/z origin) (* t (t/z direction)))]
    (>= (Math/abs y) (+ (Math/pow x 2.)
                        (Math/pow z 2.)))))

(defn intersect-caps
  [{:keys [closed? ^double minimum ^double maximum] :as c}
   {:keys [origin direction] :as ray}
   ints]
  (if (or (not closed?)
          (t/close? 0. (t/y direction)))
    ints
    (let [t-min (/ (- minimum (t/y origin)) (t/y direction))
          t-max (/ (- maximum (t/y origin)) (t/y direction))
          cap-min? (check-cap ray t-min minimum)
          cap-max? (check-cap ray t-max maximum)]
      (into
       []
       (concat
        ints
        (cond
          (and cap-min? cap-max?) [(i/intersection t-min c) (i/intersection t-max c)]
          cap-min? [(i/intersection t-min c)]
          cap-max? [(i/intersection t-max c)]
          :else []))))))

; The same as cylinders, except the formula for `a,b,c`.

; Also, the ray misses the cone when a & b are zero (not only a).

; The distance of the hit when a = 0 but b != 0 (the ray is parallel to one half of the cone but intersect the other) is slightly different.

(defn local-intersect
  [{:keys [minimum maximum] :as cne}
   {:keys [direction origin] :as ray}]
  (intersect-caps
   cne ray
   (let [a (- (+ (Math/pow (t/x direction) 2.)
                 (Math/pow (t/z direction) 2.))
              (Math/pow (t/y direction) 2.))
         b (- (+ (* 2 (t/x origin) (t/x direction))
                 (* 2 (t/z origin) (t/z direction)))
              (* 2 (t/y origin) (t/y direction)))]
     (if (and (t/close? a 0.)
              (t/close? b 0.))
       []
       (let [c (- (+ (Math/pow (t/x origin) 2.)
                     (Math/pow (t/z origin) 2.))
                  (Math/pow (t/y origin) 2.))
             disc (- (Math/pow b 2.) (* 4. a c))]
         (if (< disc 0.)
           []
           (if (t/close? a 0.)
             [(i/intersection (- (/ c (* 2. b))) cne)]
             (let [disc-sqrt (Math/sqrt disc)
                   t0 (/ (- 0. b disc-sqrt) (* 2. a))
                   t1 (/ (+ (- 0. b) disc-sqrt) (* 2. a))
                   y0 (+ (t/y origin) (* t0 (t/y direction)))
                   y1 (+ (t/y origin) (* t1 (t/y direction)))
                   y0-in-bounds (< minimum y0 maximum)
                   y1-in-bounds (< minimum y1 maximum)]
               (cond
                 (and y0-in-bounds y1-in-bounds) [(i/intersection t0 cne) (i/intersection t1 cne)]
                 y0-in-bounds [(i/intersection t0 cne)]
                 y1-in-bounds [(i/intersection t1 cne)]
                 :else [])))))))))

; ## Normal

; The same as cylinders, except the normal as an `y` component.

(defn local-normal
  [{:keys [^double minimum ^double maximum]} point _]
  (let [d (+ (Math/pow (t/x point) 2.)
             (Math/pow (t/z point) 2.))]
    (cond
      (and (< d 1) (>= (t/y point) (- maximum (double t/epsilon)))) (t/vector 0. 1. 0.)
      (and (< d 1) (<= (t/y point) (+ minimum (double t/epsilon)))) (t/vector 0. -1. 0.)
      :else (let [y (Math/sqrt (+ (Math/pow (t/x point) 2.)
                                  (Math/pow (t/z point) 2.)))]
              (t/vector (t/x point)
                        (if (< (t/y point) 0.) y (- y))
                        (t/z point))))))

; ## Creation

(defn cone
  ([transform material]
   (-> (sh/shape local-bounds local-intersect local-normal transform material)
       (assoc
        :closed? false
        :minimum (- (double t/infinity))
        :maximum t/infinity)))
  ([transform]
   (cone transform mr/default-material))
  ([]
   (cone (m/id 4) mr/default-material)))
