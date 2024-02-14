(ns nine)

(defn move [dir [hx hy]]
  (case dir
    \U [hx (inc hy)]
    \R [(inc hx) hy]
    \D [hx (dec hy)]
    \L [(dec hx) hy]))

(defn follow [[hx hy] [tx ty :as tail]]
  (cond
    (and (zero? hx) (= tx 2)) [1 hy]
    (and (zero? hx) (= tx -2)) [-1 hy]
    (and (zero? hy) (= ty 2)) [hx 1]
    (and (zero? hy) (= ty -2)) [hx -1]
    (and (pos? hx) (> (- hx tx) 1)) [(inc tx) hy]
    (and (neg? hx) (< (- hx tx) -1)) [(dec tx) hy]
    (and (pos? hy) (> (- hy ty) 1)) [hx (inc ty)]
    (and (neg? hy) (< (- hy ty) -1)) [hx (dec ty)]))

(def input
  )


