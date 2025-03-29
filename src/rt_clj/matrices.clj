; # Matrices

(ns rt-clj.matrices
  {:nextjournal.clerk/visibility {:result :hide}
   :nextjournal.clerk/toc true}
  (:require [rt-clj.tuples :as t]))

; ## Creation

; Matrices are stored as simple vectors of rows.

(def matrix identity)

(def height count)

(def width (comp count first))

; We can inspect any element.

(defn get-at [m i j]
  (get-in m [i j]))

; ## Equality

; Matrices very similar members are equals.

(defn eq? [a b]
  (every? true? (map t/eq? a b)))

; ## Transposition

; Invert the rows & cols of a matrix.

(defn transpose [m]
  (let [w (width m)]
    (mapv (fn [col]
            (mapv #(get % col) m)) (range w))))

; ## Multiplication

; We can multiply matrices.

; Element `[i,j]` is the dot product of A's row `[i]` & B's col `[j]`.


(defn mul-tuple [m t]
  (mapv #(t/dot % t) m))

(defn mul [a b]
  (if (not (vector? (first b)))
    (mul-tuple a b)
    (mapv #(mul-tuple (transpose b) %) a)))

; ### Indentity matrix

; Multiplying any matrix or tuple by the identity leaves them unchanged.

(defn id [n]
  (mapv #(vec (concat (repeat % 0.) [1.] (repeat (dec (- n %)) 0.))) (range n)))

; ## Inversion

; Inverting matrices starts with finding the determinant.

; ### Submatrices

; Finding the determinant of matrices larger than 2x2, involves finding the submatrices.

; A submatrice of A for element [i,j] is the matrix obtained by removing row i and col j of A.)

(defn- drop-nth [v n]
  (vec (concat (subvec v 0 n) (subvec v (inc n)))))

(defn subm [m i j]
  (mapv #(drop-nth % j) (drop-nth m i)))

; ### Minors & Cofactors
;
; The minor of an element at row i and column j is the determinant of the submatrix at [i,j].

(declare det)

(defn minor [m i j]
  (det (subm m i j)))

; The cofactor of [i,j] is the minor of [i,j], negated if `i+j` is odd.

(defn cofactor [m i j]
  (let [mi (minor m i j)]
    (if (odd? (+ i j))
      (- 0 mi)
      mi)))

; ### Determinant

; For 2x2 matrices, the determinant is `a.d - b.c`

; For larger matrices:
; - we extract the first row.
; - we calculate the vector of the cofactors for each element of the first row.
; - the determinant is the dot product of the first row with the cofactors vector.

(defn det [m]
  (let [w (width m)]
    (if (and (= 2 (height m))
             (= 2 w))
      (let [[[a b][c d]] m]
        (- (* a d) (* b c)))
      (let [cofs (mapv #(cofactor m 0 %) (range w))]
        (t/dot (first m) cofs)))))

; ### Inverse

; To calculate the inverse of a matrix:
; - we calculate the matrix of the cofactors.
; - we transpose those cofactors.
; - we divide each element by the determinant.

(defn cofactors [m]
  (let [h (height m)
        w (width m)
        rw (range w)]
    (mapv (fn [i]
            (mapv #(cofactor m i %) rw)) (range h))))

(defn inverse [m]
  (let [cfs (cofactors m)
        t-cfs (transpose cfs)
        d (t/dot (first m) (first cfs))]
    (if (t/close? 0 d)
      nil
      (mapv #(t/div % d) t-cfs))))

; A matrix is invertible if the determinant is not 0.

(defn invertible? [m]
  (not (t/close? 0 (det m))))
