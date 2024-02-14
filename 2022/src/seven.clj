(ns seven
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [starts-with?]]))

(defn line->op [line]
  (if (clojure.string/includes? line "ls") :ls
      [:cd (keyword (str (last (clojure.string/split line #" "))))]))

(defn line->file [line]
  (if (starts-with? line "d")
    (keyword (str (last (clojure.string/split line #" "))))
    (let [[size name] (clojure.string/split line #" ")] [:file name (parse-long size)])))

(defn folder-sizes [operations]
  (loop [path []
         tree {}
         operations operations]
    (if (empty? operations) tree
        (let [[op out] (first operations)]
          (if (= op :ls)
            (recur path
                   (reduce (fn [m [_ _ size]] (loop [m m path path]
                                                (if (empty? path) m
                                                    (recur (update m path (fn [x] (if x (+ x size) size))) (drop-last path)))))
                           tree (filter vector? out))
                   (rest operations))
            (recur (case (second op) :.. (into [] (drop-last path)) :/ [:/] (conj path (second op)))
                   tree
                   (rest operations)))))))

(defn calculate []
  (with-open [rdr (reader "src/seven/input")]
    (->> (line-seq rdr)
         (reduce (fn [s line] (if (clojure.string/starts-with? line "$")
                                (conj s [(line->op line) []])
                                (conj (rest s) (update (first s) 1 #(conj % (line->file line)))))) '())
         reverse
         folder-sizes
         (filter (fn [[_ size]] (<= size 100000)))
         (map second)
         (reduce +))))

(comment 
  (= (time (calculate)) 1611443)
  )

(defn calculate-folder-to-delete-size []
  (with-open [rdr (reader "src/seven/input")]
    (->> (line-seq rdr)
         (reduce (fn [s line] (if (clojure.string/starts-with? line "$")
                                (conj s [(line->op line) []])
                                (conj (rest s) (update (first s) 1 #(conj % (line->file line)))))) '())
         reverse
         folder-sizes 
         ((fn [folders] (let [space-req (->> (get folders [:/]) (- 70000000) (- 30000000))]
                          (drop-while #(< (second %) space-req)
                                      (sort-by second (map (fn [[path size]] [(last path) size]) folders))))))
         first
         second)))

(comment
  (= (time (calculate-folder-to-delete-size)) 2086088)
  )
