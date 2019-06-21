(ns kinograf-api.omdb
  (:require [clojure.data.json :as json]
            [clojure.walk :as walk]
            [clj-http.client :as client]
            [environ.core :refer [env]]))

(def url (str "http://www.omdbapi.com/?apikey=" (env :omdb-api-key) "&"))

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
      (:Search res)
      nil)))

(defn get-movie
  [movie-id]
  (omdb-get (str "i=" movie-id "&plot=full")))
