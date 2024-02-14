(ns six
  (:require [clojure.string :refer [split]]
            [util :refer [split-lines with-input]]))

(def test-input
  "Time:      7  15   30
Distance:  9  40  200")

(defn ->long [s]
  (Long/parseLong s))

(defn ->options [t d]
  (->> (map #(* (- t %) %) (range 1 t))
       (filter #(> % d))
       count))

(defn solve [lines]
  (->> (interleave
        (->> (split (first lines) #"\s")
             rest
             (filter seq)
             (map ->long))
        (->> (split (second lines) #"\s")
             rest
             (filter seq)
             (map ->long)))
       (partition 2)
       (map #(->options (first %) (second %)))
       (apply *)))

(= (solve (split-lines test-input)) 288)

(= (with-input solve "src/inputs/six.txt") 1710720)

(defn solve2 [lines]
  (->options
   (->> (split (first lines) #"\s")
        rest
        (filter seq)
        (apply str)
        ->long)
   (->> (split (second lines) #"\s")
        rest
        (filter seq)
        (apply str)
        ->long)))

(= (solve2 (split-lines test-input)) 71503)

(= (with-input solve2 "src/inputs/six.txt") 35349468)
