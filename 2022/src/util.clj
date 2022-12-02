(ns util
  (:require [clojure.string :refer [split]]))

(defn split-rn
  ([s] (split-rn 1 s))
  ([n s] (->> "\r\n" (repeat n) (apply str) re-pattern (split s))))
