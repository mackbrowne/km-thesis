(ns kmt.util
  (:require [clojure.contrib.duck-streams :as duck]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.contrib.sql :as sql]
            [kmt.config :as cfg])
  ;;(:use [clojure.contrib.pprint])
  (:import (java.net URL)
           (java.io ByteArrayOutputStream)
           (java.sql Timestamp)
           (java.util Date)))

(defn does-it-work? []
  (do (println (-> cfg/*cameras* :mto :index-url))
      true))

;; ------------------------------------------------
;; Web access
;; ------------------------------------------------

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


(defn html-as-one-line [html]
  (.replaceAll (string/join (string/split-lines html)) "\\s+" " "))

(defn clean-string [s]
  (.replaceAll s "[^a-zA-Z0-9]" "_"))

(defn get-loc-list [cam-group-name]
  (let [cam-cfg (cfg/*cameras* (keyword cam-group-name))
        loc-pattern (cam-cfg :loc-pattern)
        loc-parser (cam-cfg :loc-parser)]
    (map loc-parser 
      (re-seq loc-pattern (html-as-one-line (wget (cam-cfg :index-url)))))))

(defn get-image [cam-group-name loc]
  (let [cam-cfg (cfg/*cameras* (keyword cam-group-name))
        url ((cam-cfg :pic-url-builder) loc)]
    (wget url :binary)))

(defn now-timestamp []
  (.getTime (Date.)))

;; ----------------------------------------------
;; Logging to stdout
;; ----------------------------------------------
(def *logger* (agent nil))
(defn print-log [& s]
  (send *logger* (fn [x] (apply println s))))

