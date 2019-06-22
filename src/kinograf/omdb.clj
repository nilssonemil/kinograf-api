(ns kinograf.omdb
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log]
            [clojure.walk :as walk]
            [clj-http.client :as client]
            [environ.core :refer [env]]))

(def url (str "http://www.omdbapi.com/?apikey=" (env :omdb-api-key) "&"))

(defn convert-ratings-to-schema
  [omdb-ratings]
  (map
    (fn [omdb-rating]
      {:source (:Source omdb-rating)
       :value (:Value omdb-rating)})
    omdb-ratings))

(defn convert-movie-to-schema
  [omdb-movie]
  {:actors (:Actors omdb-movie)
   :awards (:Awards omdb-movie)
   :box_office (:BoxOffice omdb-movie)
   :country (:Country omdb-movie)
   :dvd (:DVD omdb-movie)
   :director (:Director omdb-movie)
   :genre (:Genre omdb-movie)
   :language (:Language omdb-movie)
   :metascore (:Metascore omdb-movie)
   :plot (:Plot omdb-movie)
   :poster (:Poster omdb-movie)
   :production (:Production omdb-movie)
   :rated (:Rated omdb-movie)
   :ratings (convert-ratings-to-schema (:Ratings omdb-movie))
   :released (:Released omdb-movie)
   :runtime (:Runtime omdb-movie)
   :title (:Title omdb-movie)
   :type (:Type omdb-movie)
   :website (:Website omdb-movie)
   :writer (:Writer omdb-movie)
   :year (:Year omdb-movie)
   :imdb_id (:imdbID omdb-movie)
   :imdb_rating (:imdbRating omdb-movie)
   :imdb_votes (:imdbVotes omdb-movie)})

(defn clojurify
  "Converts JSON to a keyworded map."
  [body]
  (walk/keywordize-keys (json/read-str body)))

(defn omdb-get
  [query]
  (let [res (client/get (str url query))]
    (if (= (:status res) 200)
      (let [body (clojurify (:body res))]
        (if (= (:Response body) "True")
          body
          nil))
      nil)))

(defn search-movie
  [title]
  (let [res (omdb-get (str "s=" title))]
    (if (not (nil? res))
      (map (fn [omdb-movie]
             (convert-movie-to-schema omdb-movie))
           (:Search res))
      nil)))

(defn get-movie
  [movie-id]
  (convert-movie-to-schema (omdb-get (str "i=" movie-id "&plot=full"))))
