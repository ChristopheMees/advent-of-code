(ns ten
  (:require [clojure.zip :refer [up down left replace right root vector-zip]]
            [util :refer [split-lines with-input]]))

(def test-input
  "-L|F7
7S-7|
L|7||
-L-J|
L|-JF")

(defn parse-grid [lines]
  (mapv (partial mapv #(if (= \. %) nil %)) lines))

(defn applytimes [n f x]
  ((apply comp (repeat n f)) x))

(defn solve [lines]
  (let [grid (parse-grid lines)]
    (->> (vector-zip grid)
         ((fn [z]
            (loop [cur (down z)]
              (if (empty? (filter #(= \S %) (first cur)))
                (recur (right cur))
                cur))))
         ((fn [z]
            (loop [cur (down z)]
              (if (= \S (first cur))
                cur
                (recur (right cur))))))
         ((fn [z]
            (loop [[node zip :as cur] z
                   num 0
                   dir nil]
              #_(println {:node node :num num :dir dir})
              (cond
                (= node \S) (cond
                              (#{\-} (first (:r zip)))
                              (recur (right (replace cur num)) (inc num) :r)

                              (#{\7} (first (:r zip)))
                              (recur (right (replace cur num)) (inc num) :r))

                (and (= dir :r) (= \- node))
                (recur (right (replace cur num)) (inc num) :r)

                (and (= dir :l) (= \- node))
                (recur (left (replace cur num)) (inc num) :l)

                (and (= dir :l) (= \L node))
                (recur (->> (replace cur num) up left down (applytimes (count (:l zip)) right)) (inc num) :u)

                (and (= dir :d) (= \L node))
                (recur (right (replace cur num)) (inc num) :r)

                (and (= dir :r) (#{\7} node))
                (recur (->> (replace cur num) up right down (applytimes (count (:l zip)) right)) (inc num) :d)

                (and (= dir :u) (#{\7} node))
                (recur (left (replace cur num)) (inc num) :l)

                (and (= dir :d) (#{\|} node))
                (recur (->> (replace cur num) up right down (applytimes (count (:l zip)) right)) (inc num) :d)

                (and (= dir :u) (#{\|} node))
                (recur (->> (replace cur num) up left down (applytimes (count (:l zip)) right)) (inc num) :u)

                (and (= dir :d) (#{\J} node))
                (recur (left (replace cur num)) (inc num) :l)

                (and (= dir :r) (#{\J} node))
                (recur (->> (replace cur num) up left down (applytimes (count (:l zip)) right)) (inc num) :u)

                (and (= dir :u) (#{\F} node))
                (recur (right (replace cur num)) (inc num) :r)

                (and (= dir :l) (#{\F} node))
                (recur (->> (replace cur num) up right down (applytimes (count (:l zip)) right)) (inc num) :d)

                (number? node) (/ num 2)

                :else {:else (first cur)})))))))

(= (solve (split-lines test-input)) 4)

(= (with-input solve "src/inputs/ten.txt") 6838)

(def test-input2
  ".F----7F7F7F7F-7....
.|F--7||||||||FJ....
.||.FJ||||||||L7....
FJL7L7LJLJ||LJ.L-7..
L--J.L7...LJS7F-7L7.
....F-J..F7FJ|L7L7L7
....L7.F7||L7|.L7L7|
.....|FJLJ|FJ|F7|.LJ
....FJL-7.||.||||...
....L---J.LJ.LJLJ...")

(let [lines (split-lines test-input2)
      grid (parse-grid lines)]
  (->> (vector-zip grid)
       ((fn [z]
          (loop [cur (down z)]
            (if (empty? (filter #(= \S %) (first cur)))
              (recur (right cur))
              cur))))
       ((fn [z]
          (loop [cur (down z)]
            (if (= \S (first cur))
              cur
              (recur (right cur))))))
       ((fn [z]
          (loop [[node zip :as cur] z
                 dir nil]
            #_(println {:node node :num num :dir dir})
            (cond
              (= node \S) (cond
                            (#{\-} (first (:r zip)))
                            (recur (right (replace cur ">")) :r)

                            (#{\7} (first (:r zip)))
                            (recur (right (replace cur ">")) :r))

              (and (= dir :r) (= \- node))
              (recur (right (replace cur ">")) :r)

              (and (= dir :l) (= \- node))
              (recur (left (replace cur "<")) :l)

              (and (= dir :l) (= \L node))
              (recur (->> (replace cur "|") up left down (applytimes (count (:l zip)) right)) :u)

              (and (= dir :d) (= \L node))
              (recur (right (replace cur ">")) :r)

              (and (= dir :r) (#{\7} node))
              (recur (->> (replace cur "|") up right down (applytimes (count (:l zip)) right)) :d)

              (and (= dir :u) (#{\7} node))
              (recur (left (replace cur "<")) :l)

              (and (= dir :d) (#{\|} node))
              (recur (->> (replace cur "|") up right down (applytimes (count (:l zip)) right)) :d)

              (and (= dir :u) (#{\|} node))
              (recur (->> (replace cur "|") up left down (applytimes (count (:l zip)) right)) :u)

              (and (= dir :d) (#{\J} node))
              (recur (left (replace cur "<")) :l)

              (and (= dir :r) (#{\J} node))
              (recur (->> (replace cur "|") up left down (applytimes (count (:l zip)) right)) :u)

              (and (= dir :u) (#{\F} node))
              (recur (right (replace cur ">")) :r)

              (and (= dir :l) (#{\F} node))
              (recur (->> (replace cur "|") up right down (applytimes (count (:l zip)) right)) :d)

              (#{"<" ">" "|"} node) (root cur)

              :else {:else (first cur)}))))))
