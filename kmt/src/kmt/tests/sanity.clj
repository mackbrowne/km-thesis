(ns kmt.tests.sanity
  (:use [kmt.util])
  (:require [clojure.contrib.pprint :as pprint]))

(defn -main []
  (do
    (println "------- Environment ----------")
    (println (does-it-work?))
    (println "------- mto list ----------")
    (pprint/pprint (get-loc-list "mto"))
    (println "------- rescu list ----------")
    (pprint/pprint (get-loc-list "rescu"))))

