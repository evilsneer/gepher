# gepher

A Clojure library designed to convert list of nodes or json to gexf

## Usage

### Clojure usage
<pre>
(:require [gepher.core :as gepher])

(def td ['({:label ":ROOT", :id 0, :deep 1}
           {:label "project.clj", :id 13, :deep 2})
         '({:label ":ROOT", :id 0, :deep 1}
           {:label "LICENSE", :id 14, :deep 2})])

(gepher/edges->gexf td)
</pre>

### Cli usage 
there is --file argument for cpecify json file to convert

## License

Copyright © 2018 Vladislav Shishkov

Distributed under the MIT License.
