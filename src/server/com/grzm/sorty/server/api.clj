(ns com.grzm.sorty.server.api
  (:require
    [com.stuartsierra.component :as component]
    [io.pedestal.log :as log]))

(defn api
  [request]
  {:status 200 :body "Hallo, world!"})

(defrecord APIHandler []
  component/Lifecycle
  (start [this]
    (log/info :msg "Creating APIHandler")
    this)
  (stop [this]
    (log/info :msg "Destroying APIHandler")
    this))

(defn api-handler []
  (->APIHandler))
