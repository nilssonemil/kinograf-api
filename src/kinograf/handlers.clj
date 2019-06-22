(ns kinograf.handlers
  (:require [com.walmartlabs.lacinia :as lacinia]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.tools.logging :as log]))

(defn extract-query
  [request]
  (case (:request-method request)
    :get (get-in request [:body :query])
    :post (get-in request [:body :query])
    :else ""))

(defn extract-variables
  [request]
  (let [vars (get-in request [:body :variables])]
    (if (nil? vars)
      {}
      vars)))

(defn graphql-handler
  [schema]
  (let [context {:cache (atom {})}]
    (fn [request]
      (let [vars (extract-variables request)
            query (extract-query request)
            result (lacinia/execute schema query vars context)
            status (if (-> result :errors seq) 400, 200)]
        {:status status
         :body (json/write-str result)}))))