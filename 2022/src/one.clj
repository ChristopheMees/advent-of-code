(ns one
  (:require [util :refer [split-rn]]))

(def input (slurp "src/one/input"))

(defn nums->sum [nums]
  (transduce (map parse-long) + nums))

(defn drop-min [coll n]
  (remove (partial = (apply min n coll)) (conj coll n)))

(defn top-cals [n]
  (transduce
   (comp (map split-rn) (map nums->sum))
   (completing drop-min)
   (range (* -1 n) 0)
   (split-rn 2 input)))

(comment
  (= (first (top-cals 1)) 70720)
  (= (reduce + (top-cals 3)) 207148)
  )
