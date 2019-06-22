(ns kinograf.middleware
  (:require [ring.util.response :as response]
            [clojure.tools.logging :as log]))

(defn wrap-request-logger
  [handler name]
  (fn [request] (do (log/info name " LOGGER:\n" request) (handler request))))


(defn wrap-content-type
  "Wraps the response from request's handler with given content-type."
  [handler content-type]
  (fn [request] (response/content-type (handler request) content-type)))
