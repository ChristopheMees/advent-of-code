(ns one
  (:require [util :refer [split-lines with-input]]))

(defn find-first [pred coll]
  (first (filter pred coll)))

(defn find-last [pred coll]
  (last (filter pred coll)))

(defn number?? [x]
  (try (Integer/parseInt (str x))
       (catch java.lang.NumberFormatException _e false)))

(defn line->calibration-value [line]
  (Integer/parseInt (str (find-first number?? line) (find-last number?? line))))

(defn solve [input]
  (transduce (map line->calibration-value) + input))

(def test-input (split-lines
                 "1abc2
pqr3stu8vwx
a1b2c3d4e5f
treb7uchet"))

(= (solve test-input) 142)

(= (with-input solve "src/inputs/one.txt") 54573)

(def number-word
  {"one" 1
   "two" 2
   "three" 3
   "four" 4
   "five" 5
   "six" 6
   "seven" 7
   "eight" 8
   "nine" 9})

(def word-regex #"one|two|three|four|five|six|seven|eight|nine")

(defn number-reducer [concat-fn]
  (fn [s c]
    (if (number?? c)
      (reduced c)
      (if-let [word (re-find word-regex (concat-fn s c))]
        (reduced (number-word word))
        (concat-fn s c)))))

(defn first-num [concat-fn line]
  (reduce (number-reducer concat-fn) "" line))

(defn first+last-num [line]
  (Integer/parseInt
   (str
    (first-num #(str %1 %2) line)
    (first-num #(str %2 %1) (reverse line)))))

(defn solve2 [input]
  (transduce (map first+last-num) + input))

(def test-input2 (split-lines
                  "two1nine
eightwothree
abcone2threexyz
xtwone3four
4nineeightseven2
zoneight234
7pqrstsixteen"))

(= (solve2 test-input2) 281)

(time (= (with-input solve2 "src/inputs/one.txt") 54591))
