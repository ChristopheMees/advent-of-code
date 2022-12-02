(ns one
  (:require [clojure.java.io :refer [reader]]))

(defn nums->sum [nums]
  (transduce (map parse-long) + nums))

(defn drop-min [coll n]
  (remove (partial = (apply min n coll)) (conj coll n)))

(def lines->sums (comp
                  (partition-by #(-> % count (= 0)))
                  (filter #(-> % first count (> 0)))
                  (map nums->sum)))

(defn top-cals [n coll]
  (transduce
   lines->sums
   (completing drop-min)
   (range (* -1 n) 0)
   coll))

(defn calculate [n]
  (with-open [rdr (reader "src/one/input")]
    (top-cals n (line-seq rdr))))

(comment 
  (= (time (first (calculate 1))) 70720)
  (= (time (reduce + (calculate 3))) 207148)
  )
