(ns kmt.db
  (:use [kmt.config]
        [kmt.util])
  (:require [clojure.contrib.sql :as sql]
            [clojure.contrib.string :as string]
            [clojure.string :as cstring]
            [clojure.contrib.duck-streams :as duck]
            [clojure.contrib.str-utils :as utils]))

(defn create-tables []
  (do (sql/with-connection *db*
        (sql/create-table :frames
           [:groupname "varchar (50)"]
           [:locid "varchar(50)"]
           [:timestamp "TIMESTAMP"]
           [:jpg "BLOB"]
           ["PRIMARY KEY" "(groupname, locid, timestamp)"]))
      (sql/with-connection *db*
         (sql/create-table :locations
           [:groupname "varchar(50)"]
           [:locid "varchar(50)"]
           [:locname "varchar(80)"]
           [:longitude "varchar(30)"]
           [:latitude "varchar(30)"]
           ["PRIMARY KEY" "(groupname, locid)"]))))


(def *db-agent* (agent {:count 0}))

(defn save-location [loc]
  (sql/with-connection *db* ;; Right now its just inserting without checking for duplicates
    (sql/insert-records :locations
      {:groupname (loc :cam-group)
       :locid (loc :id)
       :locname (loc :name)})))

(defn save-image [agent-count loc]
  (do (sql/with-connection *db*
        (sql/insert-records :frames
          {:groupname (loc :cam-group)
           :locid (loc :id) 
           :timestamp (java.sql.Timestamp. (loc :last-visted)) 
           :jpg (loc :cached-image) }))
   (assoc agent-count :count (inc (:count agent-count)))))

(defn update-coordinates []
  (sql/with-connection *db*
    (sql/transaction
       (doseq [line (duck/read-lines *coord_file*)]
          (let [[locid longitude latitude desc] (utils/re-split #"," line)]
             (sql/update-values :locations 
                             ["locid=?" (cstring/trim locid)] 
                             {:longitude longitude :latitude latitude}))))))
      

(defn fetch-image-from-db [groupname locid timestamp]
  (sql/with-connection *db*
    (sql/with-query-results results [
      "SELECT jpg FROM frames where groupname=? and locid=? and timestamp=?"
      groupname locid timestamp]
      ((first results) :jpg))))
