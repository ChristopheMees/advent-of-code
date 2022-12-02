(ns two
  (:require [clojure.java.io :refer [reader]]))

(defn round->score [[op mp]]
  (-> [[3 6 0]
       [0 3 6]
       [6 0 3]]
      (nth (dec op))
      (nth (dec mp))))

(defn xscore [mod-round]
  (comp (map (juxt first last))
        (map #(map {\A 1 \B 2 \C 3 \X 1 \Y 2 \Z 3} %))
        (map mod-round)
        (map (juxt second round->score))
        (map (partial apply +))))

(defn score [xform coll]
  (transduce xform + coll))

(defn calculate [xform]
  (with-open [rdr (reader "src/two/input")]
    (score xform (line-seq rdr))))

;Score based on x=rock y=paper z=scissors

(defn score1 [] (calculate (xscore identity)))

(comment
  (= (time (score1)) 8933))

;Score based on x=loss y=draw z=win (ldw)

(defn ldw->round [[op ldw]]
  (-> [[3 1 2]
       [1 2 3]
       [2 3 1]]
      (nth (dec ldw))
      (nth (dec op))))

(defn score2 [] (calculate (xscore (juxt first ldw->round))))

(comment 
  (= (time (score2)) 11998))
