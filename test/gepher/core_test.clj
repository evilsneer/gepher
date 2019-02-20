(ns gepher.core-test
  (:require [clojure.test :refer :all]
            [gepher.core :refer :all]
            [clojure.string :as str]))

(def test-data-small ['({:label ":ROOT", :id 0, :deep 1}
                {:label "project.clj", :id 13, :deep 2 :to-label "audience"})
              '({:label ":ROOT", :id 0, :deep 1}
                {:label "LICENSE", :id 14, :deep 2})])

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
                '({:label "folder_to_gexf", :id 12, :deep 3 :wool "asd"}
                  {:label "core.clj", :id 23, :deep 4})])

(def return-data "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gexf version=\"1.2\" xmlns=\"http://www.gexf.net/1.2draft\"><meta ><creator>Gepher</creator><description>DESCRIPTION</description></meta><graph defaultedgetype=\"directed\" mode=\"static\"><attributes class=\"node\"><attribute id=\"0\" title=\"deep\" type=\"float\"></attribute></attributes><nodes><node id=\"14\" label=\"LICENSE\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"6\" label=\"maven\"><attvalue for=\"0\" value=\"5\"></attvalue></node><node id=\"7\" label=\"folder_to_gexf\"><attvalue for=\"0\" value=\"6\"></attvalue></node><node id=\"5\" label=\"META-INF\"><attvalue for=\"0\" value=\"4\"></attvalue></node><node id=\"17\" label=\"pom.properties\"><attvalue for=\"0\" value=\"8\"></attvalue></node><node id=\"15\" label=\"core_test.clj\"><attvalue for=\"0\" value=\"4\"></attvalue></node><node id=\"8\" label=\"folder_to_gexf\"><attvalue for=\"0\" value=\"7\"></attvalue></node><node id=\"9\" label=\"stale\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"0\" label=\":ROOT\"><attvalue for=\"0\" value=\"1\"></attvalue></node><node id=\"4\" label=\"classes\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"21\" label=\"README.md\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"12\" label=\"folder_to_gexf\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"23\" label=\"core.clj\"><attvalue for=\"0\" value=\"4\"></attvalue></node><node id=\"1\" label=\"test\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"16\" label=\"CHANGELOG.md\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"3\" label=\"target\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"2\" label=\"folder_to_gexf\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"20\" label=\"resources\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"22\" label=\"intro.md\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"10\" label=\"doc\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"13\" label=\"project.clj\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"18\" label=\"leiningen.core.classpath.extract-native-dependencies\"><attvalue for=\"0\" value=\"4\"></attvalue></node><node id=\"19\" label=\"repl-port\"><attvalue for=\"0\" value=\"3\"></attvalue></node><node id=\"11\" label=\"src\"><attvalue for=\"0\" value=\"2\"></attvalue></node></nodes><edges><edge id=\"0\" source=\"0\" target=\"13\"></edge><edge id=\"1\" source=\"0\" target=\"14\"></edge><edge id=\"2\" source=\"0\" target=\"1\"></edge><edge id=\"3\" source=\"1\" target=\"2\"></edge><edge id=\"4\" source=\"2\" target=\"15\"></edge><edge id=\"5\" source=\"0\" target=\"16\"></edge><edge id=\"6\" source=\"0\" target=\"3\"></edge><edge id=\"7\" source=\"3\" target=\"4\"></edge><edge id=\"8\" source=\"4\" target=\"5\"></edge><edge id=\"9\" source=\"5\" target=\"6\"></edge><edge id=\"10\" source=\"6\" target=\"7\"></edge><edge id=\"11\" source=\"7\" target=\"8\"></edge><edge id=\"12\" source=\"8\" target=\"17\"></edge><edge id=\"13\" source=\"3\" target=\"9\"></edge><edge id=\"14\" source=\"9\" target=\"18\"></edge><edge id=\"15\" source=\"3\" target=\"19\"></edge><edge id=\"16\" source=\"0\" target=\"20\"></edge><edge id=\"17\" source=\"0\" target=\"21\"></edge><edge id=\"18\" source=\"0\" target=\"10\"></edge><edge id=\"19\" source=\"10\" target=\"22\"></edge><edge id=\"20\" source=\"0\" target=\"11\"></edge><edge id=\"21\" source=\"11\" target=\"12\"></edge><edge id=\"22\" source=\"12\" target=\"23\"></edge></edges></graph></gexf>")

(def return-data-small  "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gexf version=\"1.2\" xmlns=\"http://www.gexf.net/1.2draft\"><meta ><creator>Gepher</creator><description>DESCRIPTION</description></meta><graph defaultedgetype=\"directed\" mode=\"static\"><attributes class=\"node\"><attribute id=\"0\" title=\"deep\" type=\"float\"></attribute></attributes><nodes><node id=\"14\" label=\"LICENSE\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"13\" label=\"project.clj\"><attvalue for=\"0\" value=\"2\"></attvalue></node><node id=\"0\" label=\":ROOT\"><attvalue for=\"0\" value=\"1\"></attvalue></node></nodes><edges><edge id=\"0\" label=\"audience\" source=\"0\" target=\"13\"></edge><edge id=\"1\" source=\"0\" target=\"14\"></edge></edges></graph></gexf>")

(defn remove-time [s]
  "Replace time element to nothing"
  (str/replace s #"lastmodifieddate=\"\d{4}-\d{2}-\d{2}\"" ""))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= (remove-time return-data-small) (remove-time (edges->gexf test-data-small))))))
