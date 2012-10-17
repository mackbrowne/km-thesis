(ns kmt.tests.test-crawl
  (:use [kmt.crawl])
  (:require [clojure.contrib.pprint :as pprint]))


(defn -main []
  (let [r (make-cam-group-ref "rescu")]
    (pprint/pprint @r)
    (println "---------------------------")
    (pprint/pprint (crawl-next "rescu" r))
    (pprint/pprint (crawl-next "rescu" r))
    (pprint/pprint (crawl-next "rescu" r))
    (pprint/pprint (crawl-next "rescu" r))
  ))
