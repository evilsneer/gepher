(ns gepher.vis
  (:require [gif-clj.core :as gif]
            [mikera.image.core :as img]
            [mikera.image.colours :as colors]
            [clojure.java.io :as io]))

(defn save-png-image [data epoch]
  (let [size (-> (count data)
               Math/sqrt
               Math/ceil
               int)
        bi (img/new-image size size)
        pixels (img/get-pixels bi)
        val->color {0 colors/white 1 colors/gray 2 colors/black}]
    (doseq [[i val] (map-indexed vector data)]
      (aset pixels i (val->color val)))
    (img/set-pixels bi pixels)
    ;(img/show bi :zoom 10.0 :title "Isn't it beautiful?")
    (let [filename (format "/Users/vladislavshishkovwork/Downloads/tmp/image%s.png" epoch)]
      (io/make-parents filename)
      (img/save bi filename))))
