(ns kinograf.authentication
  (:require [buddy.auth.backends :as backends]
            [buddy.hashers :as hash]
            [buddy.sign.jwt :as jwt]
            [clj-time.core :as time]
            [environ.core :refer [env]]
            [kinograf.database :as db]))

(def secret-key (env :secret-key))

(defn expire-date
  []
  (time/plus (time/now) (time/minutes
                          (read-string (env :token-expiration-period)))))


;;; Token Auth

(defn create-token
  [user-id]
  (jwt/sign {:user user-id :exp (expire-date)} secret-key))


(defn validate-token
  [token]
  (try
    (jwt/unsign token secret-key)
    (catch clojure.lang.ExceptionInfo e nil)))


;;; Basic Auth

(defn basic-authentication
  [request auth-data]
  (let [username (:username auth-data)
        password (:password auth-data)
        user (db/get-user-by-name username)]
    (if (not (nil? user))
      (if (hash/check password (:password user))
        user
        nil)
      nil)))


;;; Backends


(def basic-backend (backends/http-basic {:realm "Kinograft"
                                         :authfn basic-authentication}))

(def token-backend (backends/jws {:secret secret-key}))