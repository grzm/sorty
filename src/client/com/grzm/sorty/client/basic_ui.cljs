(ns com.grzm.sorty.client.basic-ui
  (:require
   [fulcro.client.core :as fc]
   [om.dom :as dom]
   [om.next :as om :refer [defui]]))

(defonce app (atom (fc/new-fulcro-client)))

(defui ^:once Root
  Object
  (render [this]
    (let [{:keys [ui/react-key]} (om/props this)]
      (dom/div #js {:key react-key} "Hello World!"))))
