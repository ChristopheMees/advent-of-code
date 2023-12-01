(ns util
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split-lines] :rename {split-lines sl}]))

(defn split-lines [^CharSequence s]
  (sl s))

(defn with-input [solver file]
  (with-open [rdr (reader file)]
    (solver (line-seq rdr))))
