(ns com.grzm.sorty.server.test.system
  (:require
   [com.grzm.sorty.server.config :as config]
   [com.stuartsierra.component :as component]
   [grzm.component.pedestal :as pedestal]))

(def http-port 8765)

(defn system
  "Provides a Component system map"
  ([]
   (system {:pedestal {:config-fn (config/pedestal-config-fn :dev http-port)}}))
  ([{:keys [pedestal] :as config}]
   (component/system-map
     :pedestal (pedestal/pedestal-servlet (:config-fn pedestal)))))
