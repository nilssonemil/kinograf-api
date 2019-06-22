(ns kinograf.main
  (:require [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [kinograf.routes :refer [handlers]]))

(def -main (wrap-defaults handlers api-defaults))
