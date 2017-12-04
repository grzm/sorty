(ns com.grzm.sorty.client.ui.slides
  (:require
   [fulcro.client.dom :as dom]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(defui ^:once Slide
  static prim/InitialAppState
  (initial-state [comp-class {:keys [text id s-class] :as params}]
    {:text text :id id :s-class s-class})
  static prim/IQuery
  (query [this] [:text :id :s-class])
  Object
  (render [this]
    (let [{:keys [text id s-class]} (prim/props this)
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
            (dom/div #js {:className "form-check"}
                     (radio-button {:accessKey ","} {:value "yes"} "yes")
                     (radio-button {:accessKey "."} {:value "no"} "no")
                     (radio-button {:accessKey "s"} {:value "skip"} "skippy!")
                     (dom/button #js {:className "btn btn-primary" :type "submit" :value "Submit"}
                                 "Submit"))))))))

(def ui-slide (prim/factory Slide))

(defui ^:once SlideWithButtons
  static prim/InitialAppState
  (initial-state [comp-class {:keys [text id s-class] :as params}]
    {:text text :id id :s-class s-class})
  static prim/IQuery
  (query [this] [:text :id :s-class])
  Object
  (render [this]
    (let [{:keys [text id s-class]} (prim/props this)
          default-attrs             {:className "btn btn-primary" :type "button"}
          classify-button           (fn [attrs text]
                                      (dom/button (clj->js (merge default-attrs attrs)) text))]
      (dom/div
        #js {:id id}
        (dom/p nil text)
        (dom/form
          nil
          (dom/fieldset
            nil
            (dom/legend nil "Is this " (dom/strong nil s-class) "?")
            (dom/div #js {:className "form-check"}
                     (classify-button {:value "yes"} "yes")
                     (classify-button {:value "no"} "no")
                     (classify-button {:value "skip"} "skip"))))))))

(def ui-slide-with-buttons (prim/factory SlideWithButtons))
