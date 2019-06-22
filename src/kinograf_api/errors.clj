(ns kinograf-api.errors)

;; These functions provide response objects in the form of maps that can be
;; directly used as a response if converted to JSON.

(defn bad-request
  "Gives map convertable to json for sending HTTP 400 responses. Optionally a
   specific message can be provided."
  ([] (bad-request "Bad request."))
  ([message] {:status 400
              :body {:error message}}))

(defn unauthorized
  []
  "Gives map convertable to json for sending HTTP 401 responses."
  {:status 401
   :body {:error "Unauthorized."}})

(defn forbidden
  []
  "Gives map convertable to json for sending HTTP 403 responses."
  {:status 403
   :body {:error "Forbidden."}})

(defn page-not-found
  "Gives map convertable to json for sending HTTP 404 responses."
  []
  {:status 404
   :body {:error "Resource not found."}})
