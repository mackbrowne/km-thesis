(ns getter
  (:require [clojure.contrib.duck-streams :as duck]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.contrib.sql :as sql])
  ;;(:use [clojure.contrib.pprint])
  (:import (java.net URL)
           (java.io ByteArrayOutputStream)
           (java.sql Timestamp)
           (java.util Date)))

(def *rescu-index-url* "http://www.toronto.ca/rescu/list.htm")
(def *rescu-loc-pattern* #"<a href=.(loc\d+).htm.>\d+-([^<]+)<")
(def *rescu-pic-url* "http://www.toronto.ca/trafficimages/")
(def *rescu-loc-group* 2)
(def *rescu-id-group* 1)

(def *mto-index-url* "http://www.mto.gov.on.ca/english/traveller/trip/traffic_cameras_list.shtml")
(def *mto-loc-pattern* #"pictures/(([^/]+/)*loc\d+)[^>]+>([^<]+)<")
(def *mto-pic-url* "http://www.mto.gov.on.ca/english/traveller/compass/camera/pictures/")
(def *mto-loc-group* 3)
(def *mto-id-group* 1)

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "test.db" })

(defn wget 
  ([url] (wget url :text))
  ([url option]
    (let [input-stream  (-> url URL. .openConnection .getInputStream)]
      (cond 
        (= option :text) (-> input-stream duck/reader duck/slurp*)
        (= option :binary) 
          (let [byte-array-stream (ByteArrayOutputStream.)]
            (io/copy input-stream byte-array-stream)
            (.toByteArray byte-array-stream))
        :else nil))))
      

(defn get-locations [html-text loc-pattern loc-group id-group]
  (for [groups (re-seq loc-pattern 
                 (string/join (string/split-lines html-text)))]
    {:locname (groups loc-group) :locid (groups id-group)}))

(defn html-as-one-line [html]
  (.replaceAll (string/join (string/split-lines html)) "\\s+" " "))

(defn clean-string [s]
  (.replaceAll s "[^a-zA-Z0-9]" "_"))

(defn make-pic-url [loc pic-url]
  (str pic-url (loc :locid) ".jpg"))

(defn save-pic [loc timestamp jpegbytes]
  (let [filename (format "%s_%s.jpg" (clean-string (loc :locname)) timestamp)]
    (println (loc :locid) filename (count jpegbytes) "bytes")
    (with-open [output (io/output-stream filename)]
      (io/copy jpegbytes output))))

(defn insert-pix [loc jpg]
  (sql/with-connection db (sql/insert-values :pics [:locid :timestamp :jpg]
           [loc (java.sql.Timestamp. (.getTime (java.util.Date.))) jpg])))

(defn create-table []
  (sql/with-connection db (sql/create-table :pics 
        [:locid "varchar(20)"]
        [:timestamp "TIMESTAMP"]
        [:jpg "BLOB"]
        ["PRIMARY KEY" "(locid, timestamp)"])))

(defn main-0 []
  (create-table))


(defn genericRun [index-url pic-url loc-pattern loc-group id-group]
  (let [html (html-as-one-line (wget index-url))
        locs (get-locations html loc-pattern loc-group id-group)]
    (doseq [loc (take 3 locs)]
      (save-pic loc "000" (wget (make-pic-url loc pic-url) :binary)))))
  ;;    (insert-pix (loc :locid) (wget (make-pic-url loc pic-url) :binary)))))
  
(defn runMTO []
  (genericRun *mto-index-url* *mto-pic-url* *mto-loc-pattern* *mto-loc-group* *mto-id-group*))

(defn runRESCU []
  (genericRun *rescu-index-url* *rescu-pic-url* *rescu-loc-pattern* *rescu-loc-group* *rescu-id-group*))

(defn main []
  (do
    (runMTO)
    (runRESCU)))

(defn main-2 [from to]
  (do (println "main-2 version 1.0")
  (sql/with-connection db
    (sql/with-query-results results 
      ["select * from pics where timestamp >= ? and timestamp <= ?" from to]
      (doseq [row results]
        (save-pic {:locid (:locid row) :locname "unknown"} (:timestamp row) (:jpg row)))))))

