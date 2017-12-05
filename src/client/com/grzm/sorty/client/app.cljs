(ns com.grzm.sorty.client.app
  (:require
   [fulcro.client :as fc]
   [fulcro.client.dom :as dom]
   [fulcro.client.primitives :as om :refer [defui]]))

(defonce app (atom (fc/new-fulcro-client)))

(defui ^:once Root
  Object
  (render [this]
    (let [{:keys [ui/react-key]} (om/props this)]
      (dom/div #js {:key react-key} "Hello World!"))))

(defn mount! []
  (reset! app (fc/mount @app Root "app")))
