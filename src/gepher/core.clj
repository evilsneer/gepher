(ns gepher.core
  (:require [hiccup.core :as hiccup]
            [clj-time.local :as localtime]
            [clj-time.format :as format]))

;;; necessaries keys
(def LABEL :label)
(def ID :id)
(def MAIN-ARGS [LABEL ID])

(defn- now []
  "Return now time in YYYY-MM-DD format"
  (format/unparse (format/formatter "YYYY-MM-dd") (localtime/local-now)))

(defn- node->id [node]
  (:id node))

(defn edges->gexf
  "Convert `edges`= [({:id 1 :label 42} {:id 2 :label 34})] to gexf file"
  ([edges] (edges->gexf edges "directed"))
  ([edges type]
   (let [graph-body [:graph {:mode "static" :defaultedgetype type}
                            [:attributes {:class "node"}
                                         [:attribute {:id 0 :title "deep" :type "float"}]]
                            [:nodes (->> edges
                                         flatten
                                     (map #(vector :node {:id (:id %) :label (:label %)} [:attvalue {:for 0 :value (:deep %)}])))]
                            [:edges (->> edges
                                      (map #(map node->id %))
                                      (map-indexed vector)
                                      (map #(let [index (first %)
                                                  edge (second %)]
                                                 (vector :edge {:id index :source (first edge) :target (second edge)}))))]]]
        (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
             (hiccup/html  [:gexf {:xmlns "http://www.gexf.net/1.2draft" :version "1.2"}
                            [:meta {:lastmodifieddate (now)}
                              [:creator "Gepher"]
                              [:description "DESCRIPTION"]
                              graph-body]])))))

;(edges->gexf test-data)
