(ns kinograf.database
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.stacktrace :refer [print-stack-trace]]
            [clojure.tools.logging :as log]
            [buddy.hashers :as hash]
            [environ.core :refer [env]]))

(def db-spec {:dbtype   (env :db-type)
              :dbname   (env :db-name)
              :user     (env :db-user)
              :password (env :db-pass)})

(defn create-user!
  [username password]
  (first
    (try
      (jdbc/insert! db-spec :kinograf_user
        {:name username
         :password (hash/derive password)})
      (catch Exception e
        (print-stack-trace e)))))

(defn get-user-by-id
  [id]
  (first
    (try
      (jdbc/query db-spec
        ["SELECT * FROM kinograf_user WHERE id = ?" id])
      (catch Exception e
        (print-stack-trace e)))))

(defn get-user-by-name
  [name]
  (first
    (try
      (jdbc/query db-spec
        ["SELECT * FROM  kinograf_user WHERE name = ?" name])
      (catch Exception e
        (print-stack-trace e)))))
