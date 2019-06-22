(ns kinograf-api.schema
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [clojure.edn :as edn]
            [com.walmartlabs.lacinia.util :as lacinia.util]
            [com.walmartlabs.lacinia.schema :as lacinia.schema]
            [kinograf-api.resolver :as resolver]))

(defn resolver-map
  []
  {:query/movie-by-id    resolver/movie-by-id
   :query/movie-by-title resolver/movie-by-title})

(defn load-schema
  []
  (-> (io/resource "edn/schema.edn")
      slurp
      edn/read-string
      (lacinia.util/attach-resolvers (resolver-map))
      lacinia.schema/compile))
