(ns kinograf-api.main
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [kinograf-api.routes :refer [handlers]]))

(def -main (wrap-defaults handlers api-defaults))
