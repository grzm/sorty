(ns com.grzm.sorty.server.system
  (:require
    [com.grzm.sorty.server.config :as config]
    [com.stuartsierra.component :as component]
    [com.grzm.component.pedestal :as pedestal]
    [io.pedestal.log :as log]
    [reloaded.repl :as repl]
    [com.grzm.sorty.server.api :as api]
    [com.grzm.sorty.server.app :as application]))

(defn system
  [{:keys [pedestal] :as config}]
  (log/info :system "generating system map" :config config)
  (component/system-map
    :api (component/using (api/api-handler) [:app])
    :app (application/app)
    :pedestal (component/using
                (pedestal/pedestal (:config-fn pedestal))
                [:api :app])))

(defn start
  [config]
  (repl/set-init! #(system config))
  (repl/go)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. (fn []
                               (log/info :system "Shutting down system")
                               (repl/stop)
                               (log/info :system "Shutdown complete")))))
