(ns seven
  (:require [clojure.string :refer [split]]
            [util :refer [split-lines with-input]]))

(def test-input
  "32T3K 765
T55J5 684
KK677 28
KTJJT 220
QQQJA 483")

(let [hand "QQQJA"
      freqs (frequencies (vals (frequencies hand)))
      mx (apply max (keys freqs))]
  (cond
    (= mx 5) 100
    (= mx 4) 90
    (= mx 3) (if (contains? freqs 2) 80 70)
    (= mx 2) (if (= (freqs 2) 2) 60 50)
    (= mx 1) 40))

(defn hand->rank [hand]
  (let [freqs (frequencies (vals (frequencies hand)))
        mx (apply max (keys freqs))]
    (cond
      (= mx 5) 100
      (= mx 4) 90
      (= mx 3) (if (contains? freqs 2) 80 70)
      (= mx 2) (if (= (freqs 2) 2) 60 50)
      (= mx 1) 40)))

(def faces
  {\2 2
   \3 3
   \4 4
   \5 5
   \6 6
   \7 7
   \8 8
   \9 9
   \T 10
   \J 11
   \Q 12
   \K 13
   \A 14})

(defn solve [lines]
  (->> (map #(split % #"\s") lines)
       (map (fn [[hand bid]] {:hand hand
                              :rank (hand->rank hand)
                              :bid (Integer/parseInt bid)}))
       (sort (fn [{r1 :rank h1 :hand} {r2 :rank h2 :hand}]
               (cond
                 (= r1 r2) (->> (interleave h1 h2)
                                (partition 2)
                                (reduce (fn [_ [c1 c2]]
                                          (cond
                                            (= c1 c2) 0
                                            (> (faces c1) (faces c2)) (reduced 1)
                                            :else (reduced -1)))
                                        0))
                 (> r1 r2) 1
                 :else -1)))
       ((fn [coll] (reduce (fn [acc idx] (-> (nth coll idx)
                                             :bid
                                             (* (inc idx))
                                             (+ acc)))
                           0 (range (count coll)))))))

(= (solve (split-lines test-input)) 6440)

(= (with-input solve "src/inputs/seven.txt") 253933213)

(def faces2
  {\J 1
   \2 2
   \3 3
   \4 4
   \5 5
   \6 6
   \7 7
   \8 8
   \9 9
   \T 10
   \Q 12
   \K 13
   \A 14})

(defn hand->best-rank [hand]
  (cond-> (hand->rank hand)
    ((set hand) \J)
    ((fn [rank]
       (cond
         (= rank 100) 100
         (= rank 90) 100
         (= rank 80) 100
         (= rank 70) 90
         (= rank 60) (if (= 2 (count (filter #(= % \J) hand)))
                       90
                       80)
         (= rank 50) 70
         :else 50)))))

(defn solve2 [lines]
  (->> (map #(split % #"\s") lines)
       (map (fn [[hand bid]] {:hand hand
                              :rank (hand->best-rank hand)
                              :bid (Integer/parseInt bid)}))
       (sort (fn [{r1 :rank h1 :hand} {r2 :rank h2 :hand}]
               (cond
                 (= r1 r2) (->> (interleave h1 h2)
                                (partition 2)
                                (reduce (fn [_ [c1 c2]]
                                          (cond
                                            (= c1 c2) 0
                                            (> (faces2 c1) (faces2 c2)) (reduced 1)
                                            :else (reduced -1)))
                                        0))
                 (> r1 r2) 1
                 :else -1)))
       ((fn [coll] (reduce (fn [acc idx] (-> (nth coll idx)
                                             :bid
                                             (* (inc idx))
                                             (+ acc)))
                           0 (range (count coll)))))))

(= (solve2 (split-lines test-input)) 5905)

(= (with-input solve2 "src/inputs/seven.txt") 253473930)
