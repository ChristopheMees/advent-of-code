(ns two
  (:require [clojure.string :refer [split trim]]
            [util :refer [split-lines with-input]]))

(def test-input
  "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green")

(def max-cubes
  {"red" 12
   "green" 13
   "blue" 14})

(defn lines->dice [lines]
  (map (fn [line] (let [[game turns] (split line #":")]
                    [(Integer/parseInt (subs game 5))
                     (->> (split turns #";")
                          (map (fn [dices] (->> (split dices #",")
                                                (map trim)
                                                (map (fn [dice] (let [[n color] (split dice #"\s")]
                                                                  [(Integer/parseInt n) color])))))))]))
       lines))

(map (fn [[n color]] (> n (max-cubes color))))

(defn solve [lines]
  (->> (lines->dice lines)
       (map (juxt first #(map (partial map (fn [[n color]] (> n (max-cubes color)))) (second %))))
       (map flatten)
       (map (juxt first #(reduce (fn [x y] (or x y)) (rest %))))
       (filter #(not (second %)))
       (map first)
       (reduce +)))

(= (solve (split-lines test-input)) 8)

(= (with-input solve "src/inputs/two.txt") 2348)

(defn solve2 [lines]
  (->> (lines->dice lines)
       (map second)
       (map (partial map (partial reduce (fn [m [n color]] (assoc m color n)) {})))
       (map (partial reduce #(merge-with max %1 %2)))
       (map vals)
       (map (partial reduce *))
       (reduce +)))

(= (solve2 (split-lines test-input)) 2286)

(= (with-input solve2 "src/inputs/two.txt") 76008)
