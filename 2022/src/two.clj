(ns two
  (:require [util :refer [split-rn]]))

(def input (slurp "src/two/input"))

(def points {\A 1 \B 2 \C 3 \X 1 \Y 2 \Z 3})

(defn round->score [[op mp]]
  (case op
    1 ([3 6 0] (dec mp))
    2 ([0 3 6] (dec mp))
    3 ([6 0 3] (dec mp))))

(defn xscore [mod-round]
  (comp (map (juxt first last))
        (map #(map points %))
        (map mod-round)
        (map (juxt second round->score))
        (map (partial apply +))))

(defn score [xform]
  (transduce xform + (split-rn input)))

;Score based on x=rock y=paper z=scissors

(def score1 (score (xscore identity)))

(comment
  (= score1 8933))

;Score based on x=loss y=draw z=win (ldw)

(defn ldw->round [[op ldw]]
  (case ldw
    1 ([3 1 2] (dec op))
    2 op
    3 ([2 3 1] (dec op))))

(def score2 (score (xscore (juxt first ldw->round))))

(comment 
  (= score2 11998))
