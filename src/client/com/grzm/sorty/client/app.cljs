(ns com.grzm.sorty.client.app
  (:require
    [fulcro.client :as fc]
    [fulcro.client.data-fetch :as df]
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.primitives :as prim :refer [defui]]
    [com.grzm.sorty.client.ui.classifier :as classifier]))

(defonce
  ^{:doc "Client app atom for primary application"}
  app
  (atom (fc/new-fulcro-client
          :started-callback (fn [app]
                              (df/load app :initial/unclassified classifier/ClassifiableTextItemList
                                       {:refresh [:fulcro/force-root]})))))

(defui ^:once Root
  "Application root"
  static prim/IQuery
  (query
    [this]
    [:ui/react-key
     {:unclassified (prim/get-query classifier/ClassifiableTextItemList)}])

  static prim/InitialAppState
  (initial-state
    [c params]
    {:unclassified (prim/get-initial-state
                     classifier/ClassifiableTextItemList {:item-list/id :unclassified})})

  Object
  (render [this]
          (let [{:keys [ui/react-key unclassified]} (prim/props this)]
            (dom/div #js {:key react-key}
                     (classifier/ui-classifiable-text-item-list unclassified)))))

(defn mount
  "Helper function to mount app, isolating mount point from callers."
  [app]
  (fc/mount app Root "app"))

(defn mount!
  "Mount main application"
  []
  (reset! app (mount app)))
