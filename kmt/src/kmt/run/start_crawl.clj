(ns kmt.run.start-crawl
  (:require [clojure.contrib.pprint :as pprint]
            [kmt.crawl :as crawl]
            [kmt.db :as db]))


(defn -main []
  (do
    (try 
      (db/create-tables)
      (catch Exception e (println "Create-tables: " e)))
    (comment crawl/start-crawl-cameras :group-name "rescu" 
                               :interval 5 ;seconds
                               :thread-count 1)
    (crawl/start-crawl-cameras :group-name "mto"
                               :interval 10
                               :thread-count 10)
  ))
