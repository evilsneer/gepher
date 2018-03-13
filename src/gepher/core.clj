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

(def test-data ['({:label ":ROOT", :id 0, :deep 1}
                  {:label "project.clj", :id 13, :deep 2})
                '({:label ":ROOT", :id 0, :deep 1}
                  {:label "LICENSE", :id 14, :deep 2})
                '({:label ":ROOT", :id 0, :deep 1} {:label "test", :id 1, :deep 2})
                '({:label "test", :id 1, :deep 2}
                  {:label "folder_to_gexf", :id 2, :deep 3})
                '({:label "folder_to_gexf", :id 2, :deep 3}
                  {:label "core_test.clj", :id 15, :deep 4})
                '({:label ":ROOT", :id 0, :deep 1}
                  {:label "CHANGELOG.md", :id 16, :deep 2})
                '({:label ":ROOT", :id 0, :deep 1} {:label "target", :id 3, :deep 2})
                '({:label "target", :id 3, :deep 2}
                  {:label "classes", :id 4, :deep 3})
                '({:label "classes", :id 4, :deep 3}
                  {:label "META-INF", :id 5, :deep 4})
                '({:label "META-INF", :id 5, :deep 4}
                  {:label "maven", :id 6, :deep 5})
                '({:label "maven", :id 6, :deep 5}
                  {:label "folder_to_gexf", :id 7, :deep 6})
                '({:label "folder_to_gexf", :id 7, :deep 6}
                  {:label "folder_to_gexf", :id 8, :deep 7})
                '({:label "folder_to_gexf", :id 8, :deep 7}
                  {:label "pom.properties", :id 17, :deep 8})
                '({:label "target", :id 3, :deep 2} {:label "stale", :id 9, :deep 3})
                '({:label "stale", :id 9, :deep 3}
                  {:label "leiningen.core.classpath.extract-native-dependencies", :id 18, :deep 4})
                '({:label "target", :id 3, :deep 2}
                  {:label "repl-port", :id 19, :deep 3})
                '({:label ":ROOT", :id 0, :deep 1}
                  {:label "resources", :id 20, :deep 2})
                '({:label ":ROOT", :id 0, :deep 1}
                  {:label "README.md", :id 21, :deep 2})
                '({:label ":ROOT", :id 0, :deep 1} {:label "doc", :id 10, :deep 2})
                '({:label "doc", :id 10, :deep 2}
                  {:label "intro.md", :id 22, :deep 3})
                '({:label ":ROOT", :id 0, :deep 1} {:label "src", :id 11, :deep 2})
                '({:label "src", :id 11, :deep 2}
                  {:label "folder_to_gexf", :id 12, :deep 3})
                '({:label "folder_to_gexf", :id 12, :deep 3}
                  {:label "core.clj", :id 23, :deep 4})])

(defn- node->id [node]
  (:id node))

(defn edges->gexf
  "Convert `edges`= [({:id 1 :label 42} {:id 2 :label 34})] to gexf file"
  [edges]
  (let [graph-body [:graph {:mode "static" :defaultedgetype "directed"}
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
                             graph-body]]))))

;(edges->gexf test-data)
