(ns kinograf.routes
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [kinograf.errors :refer [bad-request page-not-found]]
            [kinograf.handlers :refer [graphql-handler]]
            [kinograf.middleware :refer [wrap-request-logger wrap-content-type]]
            [kinograf.schema :refer [load-schema]]))

;; GraphQL schema with attached resolvers.
(def schema (load-schema))

;;; Routes

;; The routes are split-up to api/auth to clarify their purpose and enable
;; different handling in backends, see the wrapping of the routes.

(defroutes api-routes
  (context "/" []
    (GET "/graphql" request (graphql-handler schema))
    (POST "/graphql" request (graphql-handler schema))))

(defroutes root-routes
  (route/not-found (page-not-found)))

;; Apply middleware to accept/send JSON data for all routes.
(def handlers
  (-> (routes api-routes root-routes)
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :delete])
      (wrap-params)
      (wrap-json-body {:keywords? true})
      (wrap-content-type "application/json")
      (wrap-json-response)))
