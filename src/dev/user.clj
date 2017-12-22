(ns user
  (:require
   [clojure.tools.namespace.repl :as repl]
   [com.grzm.sorty.server.config :as config]
   [com.grzm.sorty.server.system :as system]
   [com.stuartsierra.component :as component]
   [figwheel-sidecar.system :as fig.sys]
   [io.pedestal.http.route :as route]
   [com.grzm.sorty.server.routes :as routes]))

(def figwheel (atom nil))

(defn start-figwheel
  ([]
   (let [figwheel-config (fig.sys/fetch-config)
         props           (System/getProperties)
         all-builds      (->> figwheel-config :data :all-builds (mapv :id))]
     (start-figwheel (keys (select-keys props all-builds)))))
  ([build-ids]
   (let [figwheel-config   (fig.sys/fetch-config)
         default-build-ids (-> figwheel-config :data :build-ids)
         build-ids         (if (seq build-ids)
                             build-ids
                             default-build-ids)
         preferred-config  (assoc-in figwheel-config [:data :build-ids] build-ids)]
     (reset! figwheel (component/system-map
                        :figwheel-system (fig.sys/figwheel-system preferred-config)
                        :css-watcher (fig.sys/css-watcher
                                       {:watch-paths ["resources/public/css"]})))
     (swap! figwheel component/start)
     (fig.sys/cljs-repl (:figwheel-system @figwheel)))))

;;; server

(def system nil)

(defn init
  "constructs the current development system"
  []
  (alter-var-root #'system
                  (constantly (system/system (config/config :dev)))))

(defn start
  "Starts the current (initialized) development system"
  []
  (alter-var-root #'system component/start))

(defn stop
  "Shuts down and destroys the current development system"
  []
  (alter-var-root #'system (fn [sys]
                             (when sys
                               (component/stop sys)
                               nil))))

(defn go
  "Initializes and starts the current development system"
  []
  (if system
    "system not nil. Use (reset) ?"
    (do (init)
        (start))))

(defn reset
  "Destroys, initializes, and starts the current development system"
  []
  (stop)
  (repl/refresh :after 'user/go))


(comment
  (require '[io.pedestal.test :refer [response-for]])
  (require '[com.grzm.sorty.server.routes :as routes])
  (require '[io.pedestal.http.route :as route])

  (route/expand-routes routes/routes)
  (def service  (get-in system [:pedestal :server :io.pedestal.http/service-fn]))
  (response-for service :get "/hallo")

  )
