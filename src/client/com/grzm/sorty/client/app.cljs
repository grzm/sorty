(ns com.grzm.sorty.client.app
  (:require
   [fulcro.client :as fc]
   [fulcro.client.dom :as dom]
   [fulcro.client.primitives :as om :refer [defui]]))

(defonce
  ^{:doc "Client app atom for primary application"}
         app
  (atom (fc/new-fulcro-client)))

(defui ^:once Root
  "Application root"
  Object
  (render [this]
    (let [{:keys [ui/react-key]} (om/props this)]
      (dom/div #js {:key react-key} "Hello World!"))))

(defn mount
  "Helper function to mount app, isolating mount point from callers."
  [app]
  (fc/mount app Root "app"))

(defn mount!
  "Mount main application"
  []
  (reset! app (mount app)))
