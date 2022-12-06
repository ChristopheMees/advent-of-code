(ns four
  (:require [clojure.java.io :refer [reader]]
            [clojure.set :refer [intersection]]
            [clojure.string :refer [split]]))

(defn range-set [start end]
  (into #{} (range start (inc end))))

(defn rangestr->set [s]
  (->> (split s #"-")
       (map parse-long)
       (apply range-set)))

(defn line->overlap? [line pred]
  (->> (split line #",")
       (map rangestr->set)
       pred))

(defn calculate [map-fn]
  (with-open [rdr (reader "src/four/input")]
    (->> (line-seq rdr)
         (map map-fn)
         (filter identity)
         count)))

(defn line->total-overlap? [line]
  (line->overlap? line #(= (apply min (map count %)) (count (apply intersection %)))))

(comment
  (= (time (calculate line->total-overlap?)) 534)
  )

(defn line->any-overlap? [line]
  (line->overlap? line #(-> (apply intersection %) empty? not)))

(comment 
  (= (time (calculate line->any-overlap?)) 841)
  )
