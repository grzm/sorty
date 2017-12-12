(ns com.grzm.sorty.server.handlers
  (:require
    [com.grzm.component.pedestal :as cp]
    [com.grzm.sorty.server.api :as api]))

(defn hallo
  [request]
  {:status 200 :body "Hallo, world!"})

(def api (api/api-request-handler :api))
