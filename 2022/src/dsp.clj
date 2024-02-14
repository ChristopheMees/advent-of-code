(ns dsp)

(def machines
  {:assembler 3/4
   :lab 1
   :miner 1
   :refinery 1
   :smelter 1})

(defn machine-speed [machine]
  (machines machine))

(def recipes
  {:blue {:machine :lab :time 3 :input [[:circuit 1] [:coil 1]] :output [[:blue 1]] :prolif? true}
   :circuit {:machine :assembler :time 1 :input [[:iron 2] [:copper 1]] :output [[:circuit 2]] :prolif? true}
   :coal {:machine :miner :time 2 :output [[:coal 6]] :prolif? false}
   :coil {:machine :assembler :time 1 :input [[:magnet 2] [:copper 1]] :output [[:coil 2]] :prolif? true}
   :copper {:machine :smelter :time 1 :input [[:copper-ore 1]] :output [[:copper 1]] :prolif? true}
   :copper-ore {:machine :miner :time 1 :output [[:copper-ore 6]] :prolif? false}
   :iron {:machine :smelter :time 1 :input [[:iron-ore 1]] :output [[:iron 1]] :prolif? true}
   :iron-ore {:machine :miner :time 1 :output [[:iron-ore 6]] :prolif? false}
   :magnet {:machine :smelter :time 3/2 :input [[:iron-ore 1]] :output [[:magnet 1]] :prolif? true}
   :oil {:machine :miner :time 1 :output [[:oil 1]] :prolif? false}
   :plasma-refining {:machine :refinery :time 4 :input [[:oil 2]] :output [[:refined-oil 2] [:hydrogen 1]] :prolif? true}
   :reforming-refine {:machine :refinery :time 4 :input [[:coal 1] [:plasma-refining 1]] :output [[:refined-oil 3]] :prolif? false}})

(defn proliferation [proliferator]
  (if proliferator ([1125/1000 120/100 125/100] (dec proliferator)) 1))

(defn products-per-second [{:keys [machine time prolif? proliferator]} products-per-cycle]
  (/ (* products-per-cycle (machine-speed machine) (if prolif? (proliferation proliferator) 1)) time))

(comment
  (= (products-per-second {:machine :smelter :time 1} 1) 1)
  (= (products-per-second {:machine :smelter :time 1 :prolif? true :proliferator 1} 1) 9/8)
  (= (products-per-second {:machine :smelter :time 3/2} 1) 2/3)
  (= (products-per-second {:machine :assembler :time 1} 1) 3/4)
  (= (products-per-second {:machine :miner :time 1 :proliferator 1} 6) 6)
  )

(defn product->recipe [product]
  (recipes product))

(comment
  (= (product->recipe :iron) (recipes :iron))
  )

(defn supply-chain [{:keys [product proliferator]} required-products-per-second]
  (let [{:keys [machine time input] [[product products-per-cycle]] :output :as recipe} (product->recipe product)
        req-machines (/ required-products-per-second (products-per-second (assoc recipe :proliferator proliferator) products-per-cycle))
        sp (if (= machine :miner) [product (* 360 req-machines)] [req-machines machine product])]
    (if (not input) sp
        (conj sp (mapv (fn [[component components-per-cycle]]
                         (supply-chain {:product component :proliferator proliferator} (/ (* req-machines components-per-cycle) time)))
                       input)))))

(comment
  (= (supply-chain {:product :iron-ore} 6) [:iron-ore 360])
  (= (supply-chain {:product :iron} 6) [6 :smelter :iron [[:iron-ore 360]]])
  (= (supply-chain {:product :iron :proliferator 1} 9) [8 :smelter :iron [[:iron-ore 480]]])
  (= (supply-chain {:product :magnet} 6) [9 :smelter :magnet [[:iron-ore 360]]])
  (= (supply-chain {:product :circuit} 6)
     [4 :assembler :circuit [[8 :smelter :iron [[:iron-ore 480]]] [4 :smelter :copper [[:copper-ore 240]]]]])
  (= (supply-chain {:product :circuit :proliferator 1} 6)
     [4 :assembler :circuit [[8 :smelter :iron [[:iron-ore 480]]] [4 :smelter :copper [[:copper-ore 240]]]]])
  (supply-chain {:product :reforming-refine} 97/10)
  (supply-chain {:product :reforming-refine} 6)
  (supply-chain {:product :plasma-refining} 6)
  (supply-chain {:product :magnet :proliferator 1} 18)
  )
(/ 21.4 4.5)

22 = 4x