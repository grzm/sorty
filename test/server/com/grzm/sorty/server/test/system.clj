(ns com.grzm.sorty.server.test.system
  (:require
    [com.grzm.component.pedestal :as pedestal]
    [com.grzm.sorty.server.config :as config]
    [com.grzm.sorty.server.app :as app]
    [com.stuartsierra.component :as component]
    [com.grzm.sorty.server.api :as api]
    [com.grzm.sorty.server.persistor :as persistor]
    [com.grzm.sorty.server.redis :as redis]
    [com.grzm.sorty.server.redis.predis :as predis]
    [predis.core :as pc]))

(def http-port 8765)

(defn redis-init-fn [client]
  (pc/set client
          (redis/password-reset-key "brad-reset-token")
          {:user/email-address "brad@example.com" :user/id 2}))

(defn system
  "Provides a Component system map"
  ([]
   (system {:pedestal {:config-fn (config/pedestal-config-fn :dev http-port)}
            :redis {:init-fn redis-init-fn}}))
  ([{:keys [pedestal redis] :as _config}]
   (component/system-map
     :api (component/using (api/api-handler) [:app])
     :app (app/app)
     :pedestal (component/using
                 (pedestal/pedestal-servlet (:config-fn pedestal))
                 [:api :app])
     :persistor (component/using (persistor/persistor)
                                 [:redis])
     :redis (predis/redis (:init-fn redis)))))
