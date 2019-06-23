(ns kinograf.handlers
  (:require [buddy.auth :refer [authenticated?]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [com.walmartlabs.lacinia :as lacinia]
            [kinograf.authentication :refer [create-token]]
            [kinograf.database :as db]
            [kinograf.errors :refer [bad-request forbidden]]))

(defn create-user!
  [request]
  (let [data (:body request)
        user (db/create-user! (:username data) (:password data))]
    (if (not (nil? user))
      {:status 201
       :body {:user (dissoc user :password) ;; return everything but password
              :token (create-token (:id user))}}
      (bad-request "Could not create user."))))

(defn get-token
  [request]
  (if-not (authenticated? request)
    (forbidden)
    {:status 200
     :body {:token (create-token (:id (:identity request)))}}))

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
