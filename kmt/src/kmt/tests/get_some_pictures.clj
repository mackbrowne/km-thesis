(ns kmt.tests.get-some-pictures
  (:use [kmt.util])
  (:require [clojure.contrib.pprint :as pprint]
            [clojure.java.io :as io]))

(defn -main []
  (do
    (println "------- rescu list ----------")
    (let [loc (rand-nth (get-loc-list "rescu"))]
      (println "Saving snapshot of " (loc :name))
      (with-open [output (io/output-stream (str (loc :name) ".jpg"))]
        (io/copy (get-image "rescu" loc) output)))))

