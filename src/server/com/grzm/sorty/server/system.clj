(ns com.grzm.sorty.server.system
  (:require
   [com.grzm.sorty.server.config :as config]
   [com.stuartsierra.component :as component]
   [grzm.component.pedestal :as pedestal]
   [io.pedestal.log :as log]
   [reloaded.repl :as repl]))

(defn system
  [{:keys [pedestal] :as config}]
  (log/info :system "generating system map" :config config)
  (component/system-map
    :pedestal (pedestal/pedestal (:config-fn pedestal))))

(defn start
  [config]
  (repl/set-init! #(system config))
  (repl/go)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. (fn []
                               (log/info :system "Shutting down system")
                               (repl/stop)
                               (log/info :system "Shutdown complete")))))
