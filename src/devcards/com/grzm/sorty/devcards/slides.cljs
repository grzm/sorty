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
  static prim/IQuery
  (query [this] [:ui/react-key
                 {:slide-data (prim/get-query slides/Slide)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key slide-data]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (slides/ui-slide slide-data)))))

(defcard-fulcro slide-with-radio-buttons
  SlideWithRadioButtonsRoot
  {:slide-data slide-data}
  {:inspect-data true})




(defui ^:once SlideWithButtonsRoot
  static prim/IQuery
  (query [this] [:ui/react-key
                 {:slide-data (prim/get-query slides/SlideWithButtons)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key slide-data]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (slides/ui-slide-with-buttons slide-data)))))

(defcard-fulcro slide-with-buttons
  SlideWithButtonsRoot
  {:slide-data slide-data}
  {:inspect-data true})
