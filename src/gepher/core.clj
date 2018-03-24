(ns gepher.core
  (:require [hiccup.core :as hiccup]
            [clojure.tools.cli :refer [parse-opts]]
            [clj-time.local :as localtime]
            [clj-time.format :as format]
            [clojure.data.json :as json])
  (:gen-class :main true))

(def cli-options
  ;; An option with a required argument.
  [["-f" "--file FILE" "Path to convert file"]
   ["-h" "--help"]])

;;; necessaries keys
(def LABEL :label)
(def ID :id)
(def MAIN-ARGS [LABEL ID])

(defn- format-time [t]
  "Format time for this project"
  (format/unparse (format/formatter "YYYY-MM-dd") t))

(defn- now []
  "Return now time in YYYY-MM-DD format"
  (format-time (localtime/local-now)))

(defn- node->id [node]
  (:id node))

(defn- in? [i c]
  (boolean (some (partial = i) c)))

(def boolean? (partial instance? Boolean))

(defn- update-map [f m]
  (reduce-kv (fn [m k v]
    (assoc m k (f v))) {} m))

(defn items->gexf-attr-type [c]
  (cond
    (every? boolean? c) "boolean"
    (some string? c) "string"
    :else "float"))

(defn- edges->attributes [e]
  (->> e
    flatten
    (mapcat identity)
    (filter (fn [[k v]] (not (in? k [:id :label]))))
    (map (fn [[k v]] {k [v]}))
    (apply merge-with into)
    (update-map items->gexf-attr-type)))

(def edges->attributes-mem (memoize edges->attributes))

(defn- edges->attribute-attrid-map [e]
  (->> e
    edges->attributes-mem
    keys
    (map-indexed vector)
    (map reverse)
    flatten
    (apply hash-map)
    ))

(defn- edges->nodes-attributes-vector [e]
  (->> e
    edges->attributes-mem
    seq
    (map-indexed vector)
    (map (fn [[i [n t]]] [:attribute {:id i :title n :type t}]))
    (conj [:attributes {:class "node"}])))

(defn- node->attvalues [n att-id-map]
  (->> att-id-map
    (filter (fn [[k v]] (some? (k n))))
    (map (fn [[k v]] [:attvalue {:for v :value (k n)}]))
    flatten
    (apply vector)))

(defn- node->node-representation [att-id-map n]
  (conj [:node {:id (:id n) :label (:label n)}] (node->attvalues n att-id-map)))

(defn- edge->edge-representation [e]
  (let [[i [fe se]] e]
    [:edge {:id i :source fe :target se}]))

(defn edges->gexf
  "Convert `edges`= [({:id 1 :label 42} {:id 2 :label 34})] to gexf file"
  ([edges] (edges->gexf edges "directed"))
  ([edges type]
    (let [attr->attrid (edges->attribute-attrid-map edges)]
     (let [graph-body [:graph {:mode "static" :defaultedgetype type}
                              (edges->nodes-attributes-vector edges)
                              [:nodes (->> edges
                                           flatten
                                           set
                                       (map (partial node->node-representation attr->attrid)))]
                              [:edges (->> edges
                                        (map #(map node->id %))
                                        (map-indexed vector)
                                        (map edge->edge-representation))]]]
          (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
               (hiccup/html  [:gexf {:xmlns "http://www.gexf.net/1.2draft" :version "1.2"}
                              [:meta {:lastmodifieddate (now)}
                                [:creator "Gepher"]
                                [:description "DESCRIPTION"]]
                                graph-body]))))))

(defn- str-keys->keywords-keys [col]
  (zipmap (map keyword (keys col)) (vals col)))

(defn -main [& args]
  "Start point."
  (let [opt (:options (parse-opts args cli-options))]
    (if-let [file (get opt :file)]
      (let [content (->> file
                         slurp
                         json/read-str
                         (map #(map str-keys->keywords-keys %)))]
        (println (edges->gexf content)))
      (println "Specify file with -f arg"))))
        ; (println @(st/save storage
        ;                    (get-filename!)
        ;                    (gepher/edges->gexf
        ;                      (get-full-edges folder hide)))))))

;(edges->gexf test-data)
