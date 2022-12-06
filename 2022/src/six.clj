(ns six)

(defn reduce-to-first-unique-idx [n]
  (fn [acc [idx c]]
    (if (= n (count (into #{} (conj acc c))))
      (reduced idx)
      (conj (subvec acc 1) c))))

(defn calculate-cont-unique-chars [n]
  (->> (slurp "src/six/input")
       (map-indexed (fn [idx c] [idx c]))
       (#(reduce (reduce-to-first-unique-idx n)
                 (into [] (map second) (take (dec n) %))
                 (drop (dec n) %)))
       inc))


(comment 
  (= (time (calculate-cont-unique-chars 4)) 1802)
  (= (time (calculate-cont-unique-chars 14)) 3551))
