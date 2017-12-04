(ns com.grzm.sorty.client.ui.slides
  (:require
   [om.dom :as dom]
   [om.next :as om :refer-macros [defui]]))

(defui Slide
  Object
  (render [this]
    (let [{:keys [text id]} (om/props this)]
      (dom/div #js {:id id}
               (dom/p nil text)
               (dom/form)))))

(def ui-slide (om/factory Slide {:keyfn :id}))
