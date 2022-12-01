(ns one
  (:require [clojure.string :refer [split]]))

(def input (slurp "src/one/input"))

(defn split-rn 
  ([s] (split-rn 1 s))
  ([n s] (->> "\r\n" (repeat n) (apply str) re-pattern (split s))))

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
