(ns user
  (:require
   [figwheel-sidecar.system :as fig.sys]
   [com.stuartsierra.component :as component]))

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
