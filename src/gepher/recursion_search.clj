(ns gepher.recursion-search
  (:require [clojure.pprint :as pp]
            [gepher.vis :as vis]))

(defn find_childs [edges]
  (apply
    merge-with
    into
    (->> edges
      ;(map #(map :id %))
      (map (fn [[a b]] [a [b]]))
      (map (partial apply assoc {})))))


(defn cy-do [dict-node-childs colors node]
  (reset! colors (assoc @colors node 1))
  (let [neighbors (get dict-node-childs node)]
    (doseq [neighbor neighbors]
      ;(println (frequencies (vals @colors)))
      (case (get @colors neighbor)
        0 (cy-do dict-node-childs colors neighbor)
        1 (println "AAAA")
        0))
    (reset! colors (assoc @colors node 2))))

(defn plotlog [colors index node]
  (vis/save-png-image (vals (into (sorted-map) @colors)) index)
  (printf "do node %s%n" node)
  (println (frequencies (vals @colors))))

(defn is-cyclic? [edges]
  (let [nodes (flatten edges)
        colors (atom (zipmap nodes (repeat (count nodes) 0)))
        dict-node-childs (find_childs edges)]
    (doseq [[index node] (map-indexed vector nodes)]
      #_(when (or (< index 1000) (= (mod index 1000) 0))
        (plotlog colors index node))
      (if (= 0 (get @colors node))
        (cy-do dict-node-childs colors node)))))