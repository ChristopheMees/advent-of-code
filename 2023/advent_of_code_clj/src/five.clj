(ns five
  (:require [clojure.string :refer [split]]
            [util :refer [split-lines with-input]]))


(def test-input
  "seeds: 79 14 55 13

seed-to-soil map:
50 98 2
52 50 48

soil-to-fertilizer map:
0 15 37
37 52 2
39 0 15

fertilizer-to-water map:
49 53 8
0 11 42
42 0 7
57 7 4

water-to-light map:
88 18 7
18 25 70

light-to-temperature map:
45 77 23
81 45 19
68 64 13

temperature-to-humidity map:
0 69 1
1 0 69

humidity-to-location map:
60 56 37
56 93 4")

(defn longs [coll]
  (map #(Long/parseLong %) coll))

(defn nums->parse-fn [^Long drs ^Long srs ^Long len]
  (fn [^Long num]
    (when (and (>= num srs)
               (< num (+ srs len)))
      (+ (- drs srs) num))))

(defn lines->mapping [f]
  (let [reducer (fn [m ^String line]
                  (cond
                    (.startsWith line "seeds:")
                    (assoc m :seeds (-> (split line #"\s") rest longs f))

                    (.startsWith line "seed-")
                    (assoc m :agg :seed-to-soil)

                    (.startsWith line "so")
                    (assoc m :agg :soil-to-fertilizer)

                    (.startsWith line "f")
                    (assoc m :agg :fertilizer-to-water)

                    (.startsWith line "w")
                    (assoc m :agg :water-to-light)

                    (.startsWith line "l")
                    (assoc m :agg :light-to-temperature)

                    (.startsWith line "t")
                    (assoc m :agg :temperature-to-humidity)

                    (.startsWith line "h")
                    (assoc m :agg :humidity-to-location)

                    (= "" line) m

                    :else (update m (m :agg)
                                  conj (->> (split line #"\s")
                                            longs
                                            (apply nums->parse-fn)))))]
    (fn [lines] (reduce reducer {} lines))))

(defn next-num [fns num]
  (->> ((apply juxt fns) num)
       (some identity)
       (#(or % num))))

(defn solve [f lines]
  (->> ((lines->mapping f) lines)
       ((fn [m]
          (map
           #(reduce (fn [num mapk] (next-num (m mapk) num))
                    %
                    [:seed-to-soil
                     :soil-to-fertilizer
                     :fertilizer-to-water
                     :water-to-light
                     :light-to-temperature
                     :temperature-to-humidity
                     :humidity-to-location])
           (m :seeds))))
       (apply min)))

(= (solve identity (split-lines test-input)) 35)

(= (with-input (partial solve identity) "src/inputs/five.txt") 278755257)

(def lines->mapping2
  (let [reducer (fn [m ^String line]
                  (cond
                    (.startsWith line "seeds:")
                    (assoc m :seeds (->> (split line #"\s")
                                         rest
                                         longs
                                         (partition 2)
                                         (map (fn [[s l]] [s (+ s l)]))))

                    (.startsWith line "seed-")
                    (assoc m :agg :seed-to-soil)

                    (.startsWith line "so")
                    (assoc m :agg :soil-to-fertilizer)

                    (.startsWith line "f")
                    (assoc m :agg :fertilizer-to-water)

                    (.startsWith line "w")
                    (assoc m :agg :water-to-light)

                    (.startsWith line "l")
                    (assoc m :agg :light-to-temperature)

                    (.startsWith line "t")
                    (assoc m :agg :temperature-to-humidity)

                    (.startsWith line "h")
                    (assoc m :agg :humidity-to-location)

                    (= "" line) m

                    :else (update m (m :agg)
                                  conj (->> (split line #"\s")
                                            longs))))]
    (fn [lines] (reduce reducer {} lines))))

(defn overlap [[s1 e1] [s2 e2]]
  (cond (or (< e1 s2) (> s1 e2)) [nil 0]
        (and (>= s1 s2) (<= e1 e2)) [s1 (- e1 s1)]
        (and (< s1 s2) (> e1 e2)) [s2 (- e2 s2)]
        (< s1 s2) [s2 (- e1 s2)]
        :else [s1 (- e2 s1)]))

(comment
  (= (overlap [0 10] [20 30]) [nil 0])
  (= (overlap [20 30] [0 10]) [nil 0])
  (= (overlap [0 25] [20 30]) [20 5])
  (= (overlap [0 5] [0 10]) [0 5])
  (= (overlap [25 30] [20 30]) [25 5])
  (= (overlap [5 10] [0 10]) [5 5])
  (= (overlap [20 30] [0 50]) [20 10])
  (= (overlap [0 50] [20 30]) [20 10])
  (= (overlap [5 10] [0 5]) [5 0]))

(defn overlap? [seed]
  (fn [[_d s l :as _mapping]]
    (first (overlap seed [s (+ s l)]))))

(defn next-seeds [[start end] [d s l]]
  (let [diff (- d s)
        [ovlp-start ovlp-len] (overlap [start end] [s (+ s l)])
        tstart (+ ovlp-start diff)
        tend (+ tstart ovlp-len)]
    (reduce
     (fn [v seed]
       (if (empty? v)
         (conj v seed)
         (->> (filter #(pos? (second (overlap seed %))) v)
              (cons seed)
              ((juxt (partial reduce #(min %1 (first %2)) Long/MAX_VALUE)
                     (partial reduce #(max %1 (second %2)) 0)))
              vec
              (conj (filter #(not (pos? (second (overlap seed %)))) v)))))
     []
     (cond-> [[tstart tend]]
       (< start ovlp-start) (conj [start (dec ovlp-start)])
       (or (pos? (- ovlp-len (- tend tstart)))
           (and (<= start (+ s l)) (> end (+ s l))))
       (conj [(if (= start (+ s l)) (inc ovlp-start) (+ ovlp-start ovlp-len))
              end])))))

(overlap [0 25] [20 50])

(comment
  (= (next-seeds [0 25] [0 20 30]) [[0 19]])
  (= (next-seeds [0 5] [20 0 10]) [[20 25]])
  (= (next-seeds [25 30] [0 20 30]) [[5 10]])
  (= (next-seeds [5 10] [20 0 10]) [[25 30]])
  (= (next-seeds [5 10] [20 0 5]) [[6 10] [25 25]])
  (= (next-seeds [20 30] [20 0 50]) [[40 50]])
  (= (next-seeds [57 70] [49 53 8]) [[61 70] [53 57]])
  (= (next-seeds [0 50] [0 20 30]) [[0 30]]))

(defn next-mapped-seeds [mappings seed]
  (try
    (->> (filter (overlap? seed) mappings)
         (map (partial next-seeds seed))
         (#(cond-> % (empty? %) ((fn [coll] (conj coll seed))))))
    (catch Exception e {:seed seed :e e})))

(defn solve2 [lines]
  (-> (lines->mapping2 lines)
      ((fn [m]
         (->> (m :seeds)
              (map (partial next-mapped-seeds (m :seed-to-soil)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :soil-to-fertilizer)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :fertilizer-to-water)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :water-to-light)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :light-to-temperature)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :temperature-to-humidity)))
              flatten
              (partition 2)
              (map (partial next-mapped-seeds (m :humidity-to-location)))
              flatten
              sort
              #_(apply min))))))

(= (solve2 (split-lines test-input)) 46)

(= (with-input solve2 "src/inputs/five.txt") 26829166)
