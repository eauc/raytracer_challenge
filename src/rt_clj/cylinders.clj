; # Cylinders

(ns rt-clj.cylinders
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
  [{:keys [minimum maximum]}]
  {:min (t/point -1. minimum -1.)
   :max (t/point 1. maximum 1.)})

(defn check-cap
  [{:keys [origin direction]} t]
  (let [x (+ (t/x origin) (* t (t/x direction)))
        z (+ (t/z origin) (* t (t/z direction)))]
    (>= 1. (+ (Math/pow x 2.)
              (Math/pow z 2.)))))

; ## Intersections

; If the cylinder is closed, we need to calculate the intersections with the caps.

; `intersect-caps` checks to see if the given ray intersects the end caps of the given cylinder, and adds the points of intersection (if any) to the hits collection.

(defn intersect-caps
  [{:keys [closed? minimum maximum] :as cyl}
   {:keys [origin direction] :as ray}
   ints]
  (if (or (not closed?)
          (t/close? 0. (t/y direction)))
    ints
    (let [t-min (/ (- minimum (t/y origin)) (t/y direction))
          t-max (/ (- maximum (t/y origin)) (t/y direction))
          cap-min? (check-cap ray t-min)
          cap-max? (check-cap ray t-max)]
      (into
       []
       (concat
        ints
        (filter identity [(when cap-min? (i/intersection t-min cyl))
                          (when cap-max? (i/intersection t-max cyl))]))))))

; We first calculate a pseudo-discrimant, which is negative is the ray doesn't intersect the cylinder.

; Otherwise we use it to calculate roots and the intersections.

; We also need to calculate the `y` coordinate at each intersection and check it is between `minimum` and `maximum` properties for the cylinder. If not, the intersection is not valid.

(defn local-intersect
  [{:keys [minimum maximum] :as cyl}
   {:keys [direction origin] :as ray}]
  (intersect-caps
   cyl ray
   (let [a (* (+ (Math/pow (t/x direction) 2.)
                 (Math/pow (t/z direction) 2.))
              2.)]
     (if (t/close? a 0.)
       []
       (let [b (+ (* 2 (t/x origin) (t/x direction))
                  (* 2 (t/z origin) (t/z direction)))
             c (+ (Math/pow (t/x origin) 2.)
                  (Math/pow (t/z origin) 2.)
                  -1.)
             disc (- (Math/pow b 2.) (* 2. a c))]
         (if (< disc 0.)
           []
           (let [disc-sqrt (Math/sqrt disc)
                 t0 (/ (- 0. b disc-sqrt) a)
                 t1 (/ (+ (- 0. b) disc-sqrt) a)
                 y0 (+ (t/y origin) (* t0 (t/y direction)))
                 y1 (+ (t/y origin) (* t1 (t/y direction)))]
             (filterv
              identity
              [(when (< minimum y0 maximum) (i/intersection t0 cyl))
               (when (< minimum y1 maximum) (i/intersection t1 cyl))]))))))))

; ## Normal

; Finding the normal of a cylinder is quite easy, you just need to remove the `y` coordinate of the point on the surface.

; When the point is on one of the cylinder's cap, just return =+/-u[y]=.

(defn local-normal
  [{:keys [minimum maximum]} point _]
  (let [d (+ (Math/pow (t/x point) 2.)
             (Math/pow (t/z point) 2.))]
    (cond
      (and (< d 1) (>= (t/y point) (- maximum t/epsilon))) (t/vector 0. 1. 0.)
      (and (< d 1) (<= (t/y point) (+ minimum t/epsilon))) (t/vector 0. -1. 0.)
      :else (t/vector (t/x point) 0. (t/z point)))))

; ## Creation

(defn cylinder
  ([transform material]
   (-> (sh/shape local-bounds local-intersect local-normal transform material)
       (assoc
        :closed? false
        :minimum (- t/infinity)
        :maximum t/infinity)))
  ([transform]
   (cylinder transform mr/default-material))
  ([]
   (cylinder (m/id 4) mr/default-material)))
