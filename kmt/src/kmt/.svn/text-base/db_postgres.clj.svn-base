(ns kmt.db-postgres
  (:use [kmt.config])
  (:require [clojure.contrib.sql :as sql]))

(defn create-tables []
  (do (sql/with-connection *db_postgres*
        (sql/create-table :frames
           [:groupname "varchar (50)"]
           [:locid "varchar(50)"]
           [:timestamp "TIMESTAMP"]
           [:jpg "BLOB"]
           ["PRIMARY KEY" "(groupname, locid, timestamp)"]))
      (sql/with-connection *db_postgres*
         (sql/create-table :locations
           [:groupname "varchar(50)"]
           [:locid "varchar(50)"]
           [:locname "varchar(80)"]
           ["PRIMARY KEY" "(groupname, locid)"]))))


(def *db-agent* (agent {:count 0}))

(defn save-location [loc]
  (sql/with-connection *db_postgres* ;; Right now its just inserting without checking for duplicates
    (sql/insert-records :locations
      {:groupname (loc :cam-group)
       :locid (loc :id)
       :locname (loc :name)})))

(defn save-image [agent-count loc]
  (do (sql/with-connection *db_postgres*
        (sql/insert-records :frames
          {:groupname (loc :cam-group)
           :locid (loc :id) 
           :timestamp (java.sql.Timestamp. (loc :last-visted)) 
           :jpg (loc :cached-image) }))
   (assoc agent-count :count (inc (:count agent-count)))))

(defn fetch-image-from-db [groupname locid timestamp]
  (sql/with-connection *db_postgres*
    (sql/with-query-results results [
      "SELECT jpg FROM frames where groupname=? and locid=? and timestamp=?"
      groupname locid timestamp]
      ((first results) :jpg))))