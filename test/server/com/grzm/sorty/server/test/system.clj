(ns com.grzm.sorty.server.test.system
  (:require
    [com.grzm.component.pedestal :as pedestal]
    [com.grzm.sorty.server.config :as config]
    [com.grzm.sorty.server.application :as application]
    [com.stuartsierra.component :as component]
    [com.grzm.sorty.server.api :as api]))

(def http-port 8765)

(defn system
  "Provides a Component system map"
  ([]
   (system {:pedestal {:config-fn (config/pedestal-config-fn :dev http-port)}}))
  ([{:keys [pedestal] :as _config}]
   (component/system-map
     :api (component/using (api/api-handler) [:app])
     :app (application/application)
     :pedestal (component/using
                 (pedestal/pedestal-servlet (:config-fn pedestal))
                 [:api :app]))))
