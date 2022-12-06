(ns five
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [starts-with?]]))

(defn split-crate-line [line]
  (loop [s line
       coll []]
  (if (empty? s) coll
      (recur (drop 4 s) (conj coll (take 3 s))))))

(defn col->crate [[start chr]]
  (if (= start \space) nil chr))

(defn column [grid c]
  (map #(nth % c) grid))

(defn input->grid [input]
  (->> (map #(->> % split-crate-line (mapv col->crate)) input)
       reverse
       (into [])
       ((fn [grid] (map (fn [idx] (column grid idx)) (range (count (first grid))))))
       (map (partial filter identity))
       (mapv reverse)))

(defn ins->vals [ins]
  (->> (re-seq #"\d+" ins)
       (map parse-long)
       ((fn [[amount from to]] [amount (dec from) (dec to)])))) ;;dec to indexes

(defn move [grid amount from to]
  (let [crates (take amount (grid from))]
    (reduce (fn [g c] (update g to (partial cons c)))
            (update grid from (partial drop amount))
            crates)))

(defn calculate [move-fn]
  (with-open [rdr (reader "src/five/input")]
    (->> (line-seq rdr)
         (split-with #(not (starts-with? % " 1")))
         ((fn [[input instructions]] (reduce (fn [g ins] (apply move-fn g ins))
                                             (input->grid input)
                                             (map ins->vals (drop 2 instructions)))))
         (map first)
         (apply str))))

(comment
  (= (calculate move) "TLFGBZHCN"))

(defn move-9001 [grid amount from to]
  (let [crates (->> (grid from) (take amount) reverse)]
    (reduce (fn [g c] (update g to (partial cons c)))
            (update grid from (partial drop amount))
            crates)))

(comment
  (= (calculate move-9001) "QRQFHFWCL"))
