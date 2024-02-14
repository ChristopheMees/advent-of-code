(ns four
  (:require [clojure.math :refer [pow]]
            [clojure.set :as set]
            [clojure.string :refer [split]]
            [util :refer [split-lines with-input]]))

(def test-input
  "Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11")

(defn line->matches [line]
  (->> (split line #" \| ")
       (map #(->> (split % #"\s")
                  (filter seq)
                  set))
       (apply set/intersection)
       count))

(defn solve [lines]
  (->> (map #(split % #": ") lines)
       (map second)
       (map line->matches)
       (filter (complement zero?))
       (map #(pow 2 (dec %)))
       (reduce +)
       int))

(= (solve (split-lines test-input)) 13)

(= (with-input solve "src/inputs/four.txt") 15205)

(defn add-cards [coll start matches]
  (reduce
   (fn [coll i] (update coll i inc))
   coll
   (range (inc start) (+ start matches 1))))

(comment
  (add-cards [1 1 1 1 1 1 1 1 1 1] 0 4))

(defn solve2 [lines]
  (->> (map #(split % #": ") lines)
       (map second)
       (map line->matches)
       (reduce
        (fn [m x]
          (-> (reduce (fn [m _] (update m :coll add-cards (m :count) x)) m (range (get-in m [:coll (m :count)])))
              (update :count inc)))
        {:count 0
         :coll (vec (repeat (count lines) 1))})
       :coll
       (reduce +)))

(= (solve2 (split-lines test-input)) 30)

(= (with-input solve2 "src/inputs/four.txt") 6189740)
