(ns com.grzm.sorty.devcards.slides
  (:require
   [com.grzm.sorty.client.ui.slides :as slides]
   [devcards.core :refer-macros [defcard]]
   [fulcro.client.cards :refer [defcard-fulcro]]
   [fulcro.client.dom :as dom]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(def slide-data {:id      3
                 :text    "Here's some text"
                 :s-class "spam"})

(defui ^:once SlideWithRadioButtonsRoot
  Object
  (render [this]
    (let [{:keys [ui/react-key]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (slides/ui-slide slide-data)))))

(defcard-fulcro slide-with-radio-buttons
  SlideWithRadioButtonsRoot)

(defui ^:once SlideWithButtonsRoot
  Object
  (render [this]
    (let [{:keys [ui/react-key]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (slides/ui-slide-with-buttons slide-data)))))

(defcard-fulcro slide-with-buttons
  SlideWithButtonsRoot)
