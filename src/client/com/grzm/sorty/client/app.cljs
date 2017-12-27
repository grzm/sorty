(ns com.grzm.sorty.client.app
  (:require
    [fulcro.client :as fc]
    [fulcro.client.data-fetch :as df]
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.primitives :as prim :refer [defui defsc]]
    [com.grzm.sorty.client.ui.classifier :as classifier]))

(defonce
  ^{:doc "Client app atom for primary application"}
  app
  (atom (fc/new-fulcro-client
          :started-callback (fn [app]
                              (df/load app :initial/unclassified classifier/QueueList
                                       {:refresh [:fulcro/force-root]})))))

(defsc Root
  "Application root"
  [this {:keys [ui/react-key unclassified]}]
  {:query         [:ui/react-key {:unclassified (prim/get-query classifier/QueueList)}]
   :initial-state (fn [_]
                    {:unclassified (prim/get-initial-state
                                     classifier/QueueList {:queue/id :unclassified})})}
  (dom/div #js {:key react-key}
           (classifier/ui-queue-list unclassified)))

(defn mount
  "Helper function to mount app, isolating mount point from callers."
  [app]
  (fc/mount app Root "app"))

(defn mount!
  "Mount main application"
  []
  (reset! app (mount app)))
