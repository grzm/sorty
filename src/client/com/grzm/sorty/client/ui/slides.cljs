(ns com.grzm.sorty.client.ui.slides
  (:require
   [om.dom :as dom]
   [om.next :as om :refer-macros [defui]]))

(defui Slide
  Object
  (render [this]
    (let [{:keys [text id s-class]} (om/props this)
          default-label-attrs       {:className "form-check-label"}
          default-input-attrs       {:className "btn"
                                     :name      "s-class"
                                     :type      "radio"}
          radio-button              (fn [label-attrs input-attrs text]
                                      (dom/label
                                        (clj->js (merge default-label-attrs
                                                        label-attrs))
                                        (dom/input (clj->js (merge default-input-attrs
                                                                   input-attrs))) text))]
      (dom/div
        #js {:id id}
        (dom/p nil text)
        (dom/form
          nil
          (dom/fieldset
            nil
            (dom/legend nil "Is this " (dom/strong nil s-class) "?")
            (dom/div
              #js {:className "form-check"}
              (radio-button {:accessKey ","} {:value "yes"} "yes")
              (radio-button {:accessKey "."} {:value "no"} "no")
              (radio-button {:accessKey "s"} {:value "skip"} "skip")
              (dom/button
                #js {:className "btn btn-primary" :type "submit" :value "Submit"} "Submit"))))))))

(def ui-slide (om/factory Slide {:keyfn :id}))

(defui SlideWithButtons
  Object
  (render [this]
    (let [{:keys [text id s-class]} (om/props this)]
      (dom/div
        #js {:id id}
        (dom/p nil text)
        (dom/form
          nil
          (dom/fieldset
            nil
            (dom/legend nil "Is this " (dom/strong nil s-class) "?")
            (dom/div
              #js {:className "form-check"}
              (dom/button #js {:className "btn btn-primary" :type "button" :value "yes"} "yes")
              (dom/button #js {:className "btn btn-primary" :type "button" :value "no"} "no")
              (dom/button #js {:className "btn btn-primary" :type "button" :value "skip"} "skip"))))))))

(def ui-slide-with-buttons (om/factory SlideWithButtons {:keyfn :id}))
