(ns kinograf.routes
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body
                                          wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [kinograf.errors :refer [bad-request page-not-found]]
            [kinograf.authentication :as auth]
            [kinograf.handlers :refer [create-user!
                                       get-token
                                       graphql-handler]]
            [kinograf.middleware :refer [wrap-content-type
                                         wrap-request-logger]]
            [kinograf.schema :refer [load-schema]]))

;; GraphQL schema with attached resolvers.
(def schema (load-schema))


;;; Routes

(defroutes api-routes
  (context "/" []
    (GET "/graphql" request (graphql-handler schema))
    (POST "/graphql" request (graphql-handler schema))))

(defroutes auth-routes
  (context "/api" []
    (POST "/token" request (get-token request))
    (POST "/users" request (create-user! request))))

(defroutes root-routes
  (route/not-found (page-not-found)))


;;; Wrapped Routes

(def basic-auth-routes
  (-> auth-routes
      (wrap-authentication auth/basic-backend)))

(def token-auth-routes
  (-> api-routes
      (wrap-authentication auth/token-backend)))

;; Apply middleware to accept/send JSON data for all routes.
(def handlers
  (-> (routes basic-auth-routes token-auth-routes root-routes)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (wrap-params)
      (wrap-json-body {:keywords? true})
      (wrap-content-type "application/json")
      (wrap-json-response)))
