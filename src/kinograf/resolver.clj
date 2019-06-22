(ns kinograf.resolver
  (:require [clojure.tools.logging :as log]
            [kinograf.omdb :as omdb]))

(defn movie-by-id
  [context args value]
  (omdb/get-movie (:id args)))

(defn movie-by-title
  [context args value]
  (let [movies (omdb/search-movie (:title args))]
    (map
      (fn [movie]
        (omdb/get-movie (:imdb_id movie)))
      movies)))
