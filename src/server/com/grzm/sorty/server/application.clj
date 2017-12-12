(ns com.grzm.sorty.server.application
  (:require [com.stuartsierra.component :as component]))

(defrecord Application []
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn application []
  (->Application))