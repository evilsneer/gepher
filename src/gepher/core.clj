(ns gepher.core
  (:require [hiccup.core :as hiccup]
            [clojure.tools.cli :refer [parse-opts]]
            [clj-time.local :as localtime]
            [clj-time.format :as format]
            [clojure.data.json :as json]
            [gepher.recursion-search :as rs])
  (:gen-class :main true))

(def cli-options
  ;; An option with a required argument.
  [["-f" "--file FILE" "Path to convert file"]
   ["-c" "--check FILE" "Check file for loops"]
   ["-h" "--help"]])

;;; Necessaries keys
(def LABEL :label)
(def ID :id)
(def MAIN-ARGS [LABEL ID])

;;; Time format functions for gexf metadata
(defn- format-time [t]
  "Format time for this project. `t` is localtime obj."
  (format/unparse (format/formatter "YYYY-MM-dd") t))

(defn- now []
  "Return now time in YYYY-MM-DD format."
  (format-time (localtime/local-now)))


;;; Necessaries
(defn- str-keys->keywords-keys [d]
  "Convert str to keyword in keys of `d` dict."
  (zipmap (map keyword (keys d)) (vals d)))

(defn- in? [i c]
  "True if `i` item in `c` collection."
  (boolean (some (partial = i) c)))

;; Why it is not in clojure.core? I dont know
(def boolean? (partial instance? Boolean))

(defn- update-map [f m]
  "Apply `f` function to `m` hash-map "
  ; (reduce-kv (fn [m k v]
  ; (assoc m k (f v))) {} m)) ;; hmm lets try something simpler
  (zipmap (keys m) (map f (vals m))))


;;; Conversions
(defn- node->id [node]
  (:id node))

(defn- node->to-edge-label [node]
  (:to-label node))


;; Attributes
(defn coll->gexf-attr-type [c]
  "Convert `c` to one of types for gexf = [boolean, string, float] based on types of elements in `c`"
  (cond
    (every? boolean? c) "boolean"
    (some string? c) "string"
    :else "float"))

(defn- edges->attributes [e]
  "Convert edges to attributes"
  (->> e
    flatten
    (mapcat identity)
    (filter (fn [[k _]] (not (in? k [:id :label :to-label]))))
    (map (fn [[k v]] {k [v]}))
    (apply merge-with into)
    (update-map coll->gexf-attr-type)))

(def edges->attributes-mem (memoize edges->attributes))

(defn- edges->attribute-attrid-map [e]
  "Convert edges to {:attrkey attrid}."
  (->> e
    edges->attributes-mem
    keys
    (map-indexed vector)
    (map reverse)
    flatten
    (apply hash-map)))

(defn- edges->nodes-attributes-representation [e]
  "Convert edges to nodes attributes representation in hiccup/html gexf format."
  (->> e
    edges->attributes-mem
    seq
    (map-indexed vector)
    (map (fn [[i [n t]]] [:attribute {:id i :title n :type t}]))
    (conj [:attributes {:class "node"}])))

(defn- node->attvalues [n att-id-map]
  "Convert node to attributes representation in hiccup/html gexf format."
  (->> att-id-map
    (filter (fn [[k _]] (some? (k n))))
    (map (fn [[k v]] [:attvalue {:for v :value (k n)}]))
    (apply vector)))


;;; Nodes representation
(defn- node->node-representation [att-id-map n]
  "Convert node to representation in hiccup/html gexf format."
  (into []
    (concat
      [:node {:id (:id n) :label (:label n)}]
      (node->attvalues n att-id-map))))

(defn edges->node-representations [edges att-id-map]
  "Convert edges to node representations in hiccup/html gexf format."
  (->> edges
    flatten
    set
    (map (partial node->node-representation att-id-map))))


;; Edges
(defn- edge->edge-representation [e]
  "Convert one edge to representation in hiccup/html gexf format."
  (let [[i [label fe se]] e]
    [:edge {:id i :source fe :target se :label label}]))

(defn edges->edge-representations [edges]
  "Convert edges to edges representation in hiccup/html gexf format."
  (->> edges
    (map (fn [nodes] (cons (node->to-edge-label (last nodes)) (map node->id nodes))))
    (map-indexed vector)
    (map edge->edge-representation)))

(defn edges->gexf
  "Convert `edges`= [({:id 1 :label 42} {:id 2 :label 34})] to gexf file"
  ([edges] (edges->gexf edges "directed"))
  ([edges type]
   (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
     (hiccup/html [:gexf {:xmlns "http://www.gexf.net/1.2draft" :version "1.2"}
                   [:meta {:lastmodifieddate (now)}
                    [:creator "Gepher"]
                    [:description "DESCRIPTION"]]
                   [:graph {:mode "static" :defaultedgetype type}
                    (edges->nodes-attributes-representation edges)
                    [:nodes (edges->node-representations edges (edges->attribute-attrid-map edges))]
                    [:edges (edges->edge-representations edges)]]]))))

(defn -main [& args]
  "Start point."
  (let [opt (:options (parse-opts args cli-options))]
    (if-let [file (get opt :file)]
      (let [content (->> file
                      slurp
                      json/read-str
                      (map #(map str-keys->keywords-keys %)))]
        (println (edges->gexf content)))
      (println "Specify file with -f arg for convert"))

    (if-let [file (get opt :check)]
      ;(println file)
      (let [edges (->> file
                    slurp
                    json/read-str
                    (map (fn [x] (map (fn [x1] (or (get x1 "id") 0)) x))))]
        (println (rs/is-cyclic? edges))
        (println (rs/is-cyclic-2? edges)))
      (println "Specify file with -c arg for check"))))