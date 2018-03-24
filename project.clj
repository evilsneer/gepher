(defproject gepher "0.1.0-SNAPSHOT"
  :description "convert edges to gexf"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [hiccup "1.0.5"]
                 [proto-repl "0.3.1"]
                 [clj-time "0.14.2"]
                 [org.clojure/data.json "0.2.6"]]
  :main ^:skip-aot gepher.core)
