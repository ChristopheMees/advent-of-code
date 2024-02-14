(ns nine
  (:require [clojure.string :refer [split]]
            [util :refer [split-lines with-input]]))

(def test-input
  "0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45")


(defn line->num [line]
  (->> (split line #"\s")
       (map #(Integer/parseInt (str %)))
       ((fn [coll]
          (loop [rows (conj [] coll)]
            (let [nrow (loop [nums (last rows)
                              diffs []]
                         (if (= (count nums) 1)
                           diffs
                           (recur (rest nums) (conj diffs (- (second nums) (first nums))))))]
              (if (= (filter zero? nrow) nrow)
                (conj rows nrow)
                (recur (conj rows nrow)))))))
       (map last)
       (reduce +)))

(defn solve [lines]
  (reduce
   +
   (map line->num lines)))

(= (solve (split-lines test-input)) 114)

(= (with-input solve "src/inputs/nine.txt") 1884768153)

(defn line->num2 [line]
  (->> (split line #"\s")
       (map #(Integer/parseInt (str %)))
       ((fn [coll]
          (loop [rows (conj [] coll)]
            (let [nrow (loop [nums (last rows)
                              diffs []]
                         (if (= (count nums) 1)
                           diffs
                           (recur (rest nums) (conj diffs (- (second nums) (first nums))))))]
              (if (= (filter zero? nrow) nrow)
                (conj rows nrow)
                (recur (conj rows nrow)))))))
       (map first)
       reverse
       (#(reduce (fn [sum n] (- n sum)) (first (rest %)) (rest (rest %))))))

(defn solve2 [lines]
  (reduce
   +
   (map line->num2 lines)))

(= (solve2 (split-lines test-input)) 2)

(= (with-input solve2 "src/inputs/nine.txt") 1031)
