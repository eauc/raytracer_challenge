; # Matrix Transformations

(ns rt-clj.transformations
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:import java.lang.Math)
  (:require [rt-clj.matrices :as m]
            [rt-clj.tuples :as t]))

; ## Translation

; We can translate points by multiplying them by a translation matrix.

; Vectors are unchanged by translations.)

(defn translation [x y z]
  (m/matrix [[1. 0. 0. x]
             [0. 1. 0. y]
             [0. 0. 1. z]
             [0. 0. 0. 1.]]))

; ## Scaling

; We can scale points (objects) and vectors.

(defn scaling [x y z]
  (m/matrix [[x 0. 0. 0.]
             [0. y 0. 0.]
             [0. 0. z 0.]
             [0. 0. 0. 1.]]))

; ## Rotation

; Rotate points and vector around origin, left hand convention.

(defn rotation-x [t]
  (m/matrix [[1. 0. 0. 0.]
             [0. (Math/cos t) (- 0 (Math/sin t)) 0.]
             [0. (Math/sin t) (Math/cos t) 0.]
             [0. 0. 0. 1.]]))

(defn rotation-y [t]
  (m/matrix [[(Math/cos t) 0. (Math/sin t) 0.]
             [0. 1. 0. 0.]
             [(- 0. (Math/sin t)) 0 (Math/cos t) 0.]
             [0. 0. 0. 1.]]))

(defn rotation-z [t]
  (m/matrix [[(Math/cos t) (- 0. (Math/sin t)) 0. 0.]
             [(Math/sin t) (Math/cos t) 0. 0.]
             [0. 0. 1. 0.]
             [0. 0. 0. 1.]]))
; ##  Shearing

; When applied to a tuple, a shearing transformation changes each component of the tuple in proportion to the other two components. 

; That is to say, the x component changes in proportion to y and z , y changes in proportion to x and z , and z changes in proportion to x and y.

(defn shearing [xy xz yx yz zx zy]
  (m/matrix [[1. xy xz 0.]
             [yx 1. yz 0.]
             [zx zy 1. 0.]
             [0. 0. 0. 1.]]))

; ## View

; A view is defined by:
; - a point `from` representing the origin of the ray.
; - a point `to` representing the center of the view.
; - a vector `up` representing the vertical direction.

; To calculate the view transformation we must:
; - calculate the forward vector `from -> to`.
; - calculate the left direction vector: in a left hand referential this is `forward x up`
; - the true up vector is `left x forward` - 
;   we can pass any vector vaguely pointing up, 
;   the transformation will calculate the correct `up` vector for a left hand orthogonal referential.
; - the orientation matrix is computed from `left/forward/true-up`.
; - we then translate the result with the invert of `from`, which "pushes" the world away from the view.

(defn view [from to up]
  (let [forward (t/norm (t/sub to from))
        left (t/cross forward (t/norm up))
        true-up (t/cross left forward)
        orientation (m/matrix [[(t/x left) (t/y left) (t/z left) 0.]
                               [(t/x true-up) (t/y true-up) (t/z true-up) 0.]
                               [(- (t/x forward)) (- (t/y forward)) (- (t/z forward)) 0.]
                               [0. 0. 0. 1.]])]
    (m/mul orientation (translation (- (t/x from)) (- (t/y from)) (- (t/z from))))))
