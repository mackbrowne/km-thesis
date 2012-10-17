(ns kmt.config)

(def *cameras*
  {:rescu {:index-url "http://www.toronto.ca/rescu/list.htm"
           :loc-pattern #"<a href=.(loc\d+).htm.>\d+-([^<]+)<"
           :loc-parser (fn [groups] {:name (groups 2) :id (groups 1)})
           :pic-url-builder 
              (fn [loc] 
                 (format "http://www.toronto.ca/trafficimages/%s.jpg" 
                    (loc :id)))
          }

   :mto {:index-url (str "http://www.mto.gov.on.ca/english/traveller/"
                         "trip/traffic_cameras_list.shtml")
         :loc-pattern #"pictures/(([^/]+/)*loc\d+)[^>]+>([^<]+)<"
         :loc-parser (fn [groups] {:name (groups 3) :id (groups 1)})
         :pic-url-builder
            (fn [loc]
              (str "http://www.mto.gov.on.ca/english/"
                  "traveller/compass/camera/pictures/"
                  (loc :id) ".jpg"))
        }
  })


(def *db* {:classname "org.sqlite.JDBC"
           :subprotocol "sqlite"
           :subname "traffic-images.sqlite"})

(def *db_postgres* {:classname "org.postgresql.Driver"
                    :subprotocol "postgresql"
                    :subname "//localhost:5432/traffic_images"})

(def *coord_file* "coordinates.csv")

