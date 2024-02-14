(ns eight
  (:require [clojure.java.io :refer [reader]]
            [util :refer [split-lines with-input]]))

(def test-input
  "RL

AAA = (BBB, CCC)
BBB = (DDD, EEE)
CCC = (ZZZ, GGG)
DDD = (DDD, DDD)
EEE = (EEE, EEE)
GGG = (GGG, GGG)
ZZZ = (ZZZ, ZZZ)")

(defn solve [lines]
  (-> (reduce (fn [m line]
                (assoc
                 m
                 (apply str (take 3 line))
                 [(subs line 7 10) (subs line 12 15)]))
              {:instructions (map #(if (= % \L) 0 1) (first lines))}
              (rest (rest lines)))
      ((fn [{ins :instructions :as m}]
         (reduce (fn [{loc :loc cnt :count :as m} instruction]
                   (let [nloc (get-in m [loc instruction])]
                     (if (= nloc "ZZZ")
                       (reduced cnt)
                       (-> (assoc m :loc nloc)
                           (update :count inc)))))
                 (assoc m :loc "AAA" :count 1)
                 (cycle (seq ins)))))))

(= (solve (split-lines test-input)) 2)

(= (with-input solve "src/inputs/eight.txt") 24253)

(def test-input2
  "LR

11A = (11B, XXX)
11B = (XXX, 11Z)
11Z = (11B, XXX)
22A = (22B, XXX)
22B = (22C, 22C)
22C = (22Z, 22Z)
22Z = (22B, 22B)
XXX = (XXX, XXX)")

(defn solve2 [lines]
  (-> (reduce (fn [m line]
                (let [k (apply str (take 3 line))]
                  (cond-> (assoc m k [(subs line 7 10) (subs line 12 15)])
                    (= (last k) \A) (update :locations conj k))))
              {:instructions (map #(if (= % \L) 0 1) (first lines))}
              (rest (rest lines)))
      #_((fn [{ins :instructions :as m}]
           (reduce (fn [{locs :locations cnt :count :as m} instruction]
                     (let [nlocs (map #(get-in m [% instruction]) locs)
                           lset (-> (map last nlocs) set)]
                       (if (and (lset \Z) (= (count lset) 1))
                         (reduced cnt)
                         (-> (assoc m :locations nlocs)
                             (update :count inc)))))
                   (assoc m :count 1)
                   (cycle (seq ins)))))))

(= (solve2 (split-lines test-input2)) 6)

((with-input solve2 "src/inputs/eight.txt") :locations)

(= (with-input solve2 "src/inputs/eight.txt") 24253)

(defn solve3 [start min-count lines]
  (-> (reduce (fn [m line]
                (assoc
                 m
                 (apply str (take 3 line))
                 [(subs line 7 10) (subs line 12 15)]))
              {:instructions (map #(if (= % \L) 0 1) (first lines))}
              (rest (rest lines)))
      ((fn [{ins :instructions :as m}]
         (reduce (fn [{loc :loc cnt :count :as m} instruction]
                   (let [nloc (get-in m [loc instruction])]
                     (if (and (>= cnt min-count) (.endsWith nloc "Z"))
                       (reduced nloc)
                       (-> (assoc m :loc nloc)
                           (update :count inc)))))
                 (assoc m :loc start :count 1)
                 (cycle (seq ins)))))))

(defn find-next-match [{ins :instructions :as m} start min-count]
  (reduce (fn [{loc :loc cnt :count :as m} instruction]
            (let [nloc (get-in m [loc instruction])]
              (if (and (>= cnt min-count) (.endsWith nloc "Z"))
                (reduced [nloc cnt])
                (-> (assoc m :loc nloc)
                    (update :count inc)))))
          (assoc m :loc start :count 1)
          (cycle (seq ins))))

(defonce keeper (atom {}))

(def mapped-input
  (with-open [rdr (reader "src/inputs/eight.txt")]
    (let [lines (line-seq rdr)]
      (reduce (fn [m line]
                (let [k (apply str (take 3 line))]
                  (cond-> (assoc m k [(subs line 7 10) (subs line 12 15)])
                    (= (last k) \A) (update :locations conj k))))
              {:instructions (map #(if (= % \L) 0 1) (first lines))}
              (rest (rest lines))))))

(->> (map
      (fn [start]
        {start (loop [mc 0
                      matches []
                      counts []]
                 (let [[mlocation mcount] (find-next-match mapped-input start mc)]
                   (if (> (count (filter #(= % mlocation) matches)) 0)
                     {:matches (conj matches mlocation) :counts (conj counts mcount)}
                     (recur (inc mcount) (conj matches mlocation) (conj counts mcount)))))})
      (mapped-input :locations))
     (map #(->> (vals %) first :counts reverse (apply -)))
     #_(reduce (fn [agg n] (if (zero? (mod agg n)) (/ agg n) (* agg n))) (bigint 1)))

(reset! keeper {})


(with-input (partial solve3 "DPA" 13201) "src/inputs/eight.txt") ;; 2668444

(with-input (partial solve3 "DGA" 211523) "src/inputs/eight.txt") ;; 216435
(with-input (partial solve3 "MGA" 216435) "src/inputs/eight.txt") ;; 217970
(with-input (partial solve3 "AAA" 217970) "src/inputs/eight.txt") ;; 218277

(let [counts [13201 20569 16271 14429 21797]]
  (loop [n (bigint 24253)]
    (if (zero? (reduce + (map #(mod n %) counts)))
      n
      (recur (+ n 24253)))))

(defn gcd [a b]
  (if (zero? b)
    a
    (recur b (mod a b))))

(defn lcm
  ([x y] (/ (* x y) (gcd x y)))
  ([x y & zs] (reduce lcm (lcm x y) zs)))

(lcm 13201 20569 16271 14429 21797 24253) ;; solution 12357789728873

(lcm 6 12)

(gcd 6 12)

(gcd 2 4)
(gcd 2 5)
(gcd 2 6)

2
3
4
5
6

(count [:a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z])

[:a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z :a :z
 :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z :a :b :z
 :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z :a :b :c :z
 :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z :a :b :c :d :z
 :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z :a :b :c :d :e :z]
