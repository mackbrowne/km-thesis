(ns kmt.crawl
  (:use [kmt.util]
        [kmt.db]))

(defn- loc->str [loc]
  (format "%s/%s [%s]" 
    (loc :cam-group) (loc :name) (if (loc :locked) "x" " ")))

;; A group reference is a hashmap
;; map(loc :id -> 
;;        {keys :id :name :cam-group :last-vistited :cached-image :locked}
;;
(defn make-cam-group-ref [cam-group]
  (ref (apply hash-map (flatten (doall (for [loc (get-loc-list cam-group)]
      [(loc :id) (assoc loc 
                  :cam-group cam-group
                  :last-visted 0
                  :cached-image nil
                  :locked false)]))))))

(defn lock-loc [cam-group locid]
  (let [newloc (assoc (cam-group locid) 
                :locked true
                :last-visted (now-timestamp))]
    (assoc cam-group locid newloc)))

;;
;; Sort the cam-group-ref by :last-visted
;; and pick the oldest unvisited location
;;
(defn pick-and-lock [cam-group-ref]
  (dosync
    (let [x (sort-by #(% :last-visted) 
              (filter #(not (% :locked))
                (vals @cam-group-ref)))
          loc (first x)]
      (if (nil? loc)
        nil
        ((alter cam-group-ref lock-loc (loc :id)) (loc :id))))))
      
(defn is-new-image? [loc jpg]
  (if (nil? jpg)
    false
    (if (nil? (loc :cached-image))
      true
      (if (not= (count (loc :cached-image)) (count jpg))
        true
        (some false? (map = (loc :cached-image) jpg))))))

;;
;; update the cached-image
;; unlock the loc
;; save to database
(defn update-and-unlock [r loc jpg]
  (dosync
    (let [newloc (assoc loc :cached-image jpg :locked false)]
      (print-log "updating" (loc->str newloc))
      (alter r assoc (loc :id) newloc)
      (send *db-agent* save-image newloc)
      loc)))

;;
;; Just unlock the location
;;
(defn unlock [r loc]
  (when (not (nil? loc))
    (dosync
      (let [x (assoc loc :locked false)]
        (print-log "unlocking" (loc->str x))
        (alter r assoc (loc :id) x)
        loc))))

;;
;; Crawl the next image
;; - get the next location (pick-and-lock)
;; - performs wget
;; - perform compare
;; - if not identical: 
;;   update loc cached-image
;;   send to database

(defn crawl-next [cam-group-name cam-group-ref]
  (let [loc (pick-and-lock cam-group-ref)
        jpg (if (nil? loc) nil (get-image cam-group-name loc))]
    (print-log "locked" (loc->str loc))
    (if (is-new-image? loc jpg)
        (update-and-unlock cam-group-ref loc jpg)
        (unlock cam-group-ref loc))))

;; Start multiple threads to crawl a given camera group
;; Make sure we don't get black listed.

(defn start-crawl-cameras 
      [& {:keys [group-name interval thread-count]}]
  (let [group-ref (make-cam-group-ref group-name)]
    (doseq [[locid loc] @group-ref]
      (try
        (save-location loc)
        (catch Exception e nil)))
    (update-coordinates)
    (dotimes [n thread-count]
      (.start (Thread. 
        (fn []
          (while true
            ; if crawl-next returns nil
            ; we sleep for 1 second, otherwise
            ; we sleep for the specified number of seconds
            (try
              (if (nil? (crawl-next group-name group-ref))
                (Thread/sleep (* 1000))
                (Thread/sleep (* 1000 interval)))
              (catch Exception e
                (print-log "Exception: " e)
                (Thread/sleep 1000))))))))))

