(ns three
  (:require [clojure.java.io :refer [reader]]
            [clojure.set :refer [intersection]]))

(defn char->prio [char]
  (let [n (int char)]
    (if (> n (int \Z)) (- n 96) (- n 38))))

(defn colls->prio [colls] 
  (->> (map #(into #{} %) colls)
       (reduce intersection)
       first
       char->prio))

(defn split-line [line]
  (split-at (/ (count line) 2) line))

(defn calculate-compartment []
  (with-open [rdr (reader "src/three/input")]
    (transduce (comp (map split-line) (map colls->prio)) + (line-seq rdr))))

(comment
  (= (time (calculate-compartment)) 8401)
  )

(defn calculate-badge []
  (with-open [rdr (reader "src/three/input")]
    (->> (line-seq rdr)
         (partition 3) 
         (transduce (map colls->prio) +))))

(comment 
  (= (time (calculate-badge)) 2641)
  )
