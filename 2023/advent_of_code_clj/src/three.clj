(ns three
  (:require [util :refer [split-lines with-input]]))

(def test-input
  "467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
..$..*....
.664.598..")

(defn left-nums [v digit-idx]
  (->> (subvec v 0 digit-idx)
       (map-indexed (fn [i x] [i x]))
       (reduce (fn [v [_ x :as pair]] (if x (conj v pair) [])) [])))

(defn right-nums [v digit-idx]
  (->> (subvec v digit-idx)
       (map-indexed (fn [i x] [(+ i digit-idx) x]))
       (reduce (fn [v [_ x :as pair]] (if x (conj v pair) (reduced v))) [])))

(defn pairs->str [ps]
  (apply str (map second ps)))

(defn adj-num-coords [v idx]
  (when v (let [[d1 d2 d3] [(v (dec idx)) (v idx) (v (inc idx))]]
            (cond
              (and d1 d2 d3) [(apply conj (left-nums v idx) (right-nums v idx))]
              d2 (cond
                   d1 [(left-nums v (inc idx))]
                   d3 [(right-nums v idx)]
                   :else [[[idx d2]]])
              (and d1 d3) [(left-nums v idx) (right-nums v (inc idx))]
              d1 [(left-nums v idx)]
              d3 [(right-nums v (inc idx))]))))

(comment
  (adj-num-coords [nil nil nil nil nil] 2)
  (adj-num-coords [nil nil \5 nil nil] 2)
  (adj-num-coords [nil nil \5 \2 nil] 2)
  (adj-num-coords [nil \1 \5 nil nil] 2)
  (adj-num-coords [nil \1 \5 \2 nil] 2)
  (adj-num-coords [nil \1 nil \2 nil] 2)
  (adj-num-coords [\7 \1 nil nil nil] 2)
  (adj-num-coords [nil nil nil \2 \8] 2)
  (adj-num-coords [\7 \1 nil \2 \8] 2))

(defonce debugm (atom {}))

(defn aggregate [input]
  (let [row-length (-> (split-lines input) first count inc)]
    (transduce
     (map-indexed vector)
     (completing
      (fn [m [idx chr]]
        (try
          (let [x (mod idx row-length)
                y (/ (- idx x) row-length)]
            (cond
              (or
               (= chr \.)
               (= chr \newline)) (cond-> m
                                   (= chr \newline) (#(assoc % (inc y) (vec (repeat (dec row-length) nil))))
                                   (m :aggregating) (#(-> (update % (first (:aggregating %)) conj (second (:aggregating %)))
                                                          (dissoc :aggregating))))
              (Character/isDigit chr) (cond (m :aggregating)
                                            (update-in m [:aggregating 1] str chr)
                                            (m [(dec x) y])
                                            (assoc m :aggregating [[(dec x) y] (str chr)])
                                            (m [(inc x) (dec y)])
                                            (#(let [pairs (left-nums (m y) x)]
                                                (->> (assoc m :aggregating [[(inc x) (dec y)] (str (pairs->str pairs) chr)])
                                                     ((fn [m] (reduce (fn [m [x _]] (assoc-in m [y x] nil)) m pairs))))))
                                            (m [x (dec y)])
                                            (assoc m :aggregating [[x (dec y)] (str chr)])
                                            (m [(dec x) (dec y)])
                                            (assoc m :aggregating [[(dec x) (dec y)] (str chr)])
                                            :else
                                            (update m y #(assoc % x chr)))
              :else ((fn [m]
                       (-> (assoc m [x y] (adj-num-coords (m (dec y)) x))
                           (#(update % [x y] conj (left-nums (% y) x)))
                           (#(reduce (fn [m coll]
                                       (->> (map first coll)
                                            (map (fn [x] [x (dec y)]))
                                            (reduce (fn [m [x y]] (assoc-in m [y x] nil)) m)))
                                     % (% [x y])))))
                     m)))
          (catch Exception e (clojure.pprint/pprint {:x (mod idx row-length)
                                                     :y (/ (- idx (mod idx row-length)) row-length)
                                                     :chr chr
                                                     :e e})
                 (reset! debugm m)
                 (throw (new IllegalStateException))))))
     {0 (vec (repeat (dec row-length) nil))} input)))

(defn solve [input]
  (->> (aggregate input)
       (filter #(-> (first %) vector?))
       (map second)
       (map #(->> (map
                   (fn [x]
                     (let [s (cond
                               (string? x) x
                               :else (->> (map second x) (apply str)))]
                       (when (not= s "") (Integer/parseInt s))))
                   %)))
       flatten
       (filter identity)
       (reduce +)))

(= (solve test-input) 4361)

(= (solve (slurp "src/inputs/three.txt")) 536576)

(defn aggregate2 [input]
  (let [row-length (-> (split-lines input) first count inc)]
    (transduce
     (map-indexed vector)
     (completing
      (fn [m [idx chr]]
        (try
          (let [x (mod idx row-length)
                y (/ (- idx x) row-length)]
            (cond
              (or
               (= chr \.)
               (= chr \newline)) (cond-> m
                                   (= chr \newline) (#(assoc % (inc y) (vec (repeat (dec row-length) nil))))
                                   (m :aggregating) (#(-> (update % (first (:aggregating %)) conj (second (:aggregating %)))
                                                          (dissoc :aggregating))))
              (Character/isDigit chr) (cond (m :aggregating)
                                            (update-in m [:aggregating 1] str chr)
                                            (m [(dec x) y])
                                            (assoc m :aggregating [[(dec x) y] (str chr)])
                                            (m [(inc x) (dec y)])
                                            (#(let [pairs (left-nums (m y) x)]
                                                (->> (assoc m :aggregating [[(inc x) (dec y)] (str (pairs->str pairs) chr)])
                                                     ((fn [m] (reduce (fn [m [x _]] (assoc-in m [y x] nil)) m pairs))))))
                                            (m [x (dec y)])
                                            (assoc m :aggregating [[x (dec y)] (str chr)])
                                            (m [(dec x) (dec y)])
                                            (assoc m :aggregating [[(dec x) (dec y)] (str chr)])
                                            :else
                                            (update m y #(assoc % x chr)))
              (= chr \*) ((fn [m]
                            (-> (assoc m [x y] (adj-num-coords (m (dec y)) x))
                                (#(update % [x y] conj (left-nums (% y) x)))
                                (#(reduce (fn [m coll]
                                            (->> (map first coll)
                                                 (map (fn [x] [x (dec y)]))
                                                 (reduce (fn [m [x y]] (assoc-in m [y x] nil)) m)))
                                          % (% [x y])))))
                          m)
              :else m))
          (catch Exception e (clojure.pprint/pprint {:x (mod idx row-length)
                                                     :y (/ (- idx (mod idx row-length)) row-length)
                                                     :chr chr
                                                     :e e})
                 (reset! debugm m)
                 (throw (new IllegalStateException))))))
     {0 (vec (repeat (dec row-length) nil))} input)))

(defn solve2 [input]
  (->> (aggregate2 input)
       (filter #(-> (first %) vector?))
       (map second)
       (map (partial filter seq))
       (filter #(= 2 (count %)))
       (map (partial map #(cond->> %
                            (vector? %) (reduce (fn [s [_ c]] (str s c)) "")
                            :then (Integer/parseInt))))
       (map (partial apply *))
       (reduce +)))

(= (solve2 test-input) 467835)

(= (solve2 (slurp "src/inputs/three.txt")) 75741499)
