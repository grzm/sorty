(ns com.grzm.sorty.client.ui.classifier
  (:require
   [fulcro.client.dom :as dom]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(defui ^:once TextItem
  static prim/IQuery
  (query [this] [:id :text])
  Object
  (render [this]
    (let [{:keys [id text]} (prim/props this)]
      (dom/p nil text))))

(def ui-text-item (prim/factory TextItem))

(defui ^:once ClassifiableTextItem
  static prim/IQuery
  (query [this] [:s-class {:text-item (prim/get-query TextItem)}])

  Object
  (render [this]
    (let [{:keys [s-class text-item]} (prim/props this)
          default-attrs               {:className "btn btn-primary" :type "button"}
          classify-button             (fn [attrs text]
                                        (dom/button (clj->js (merge default-attrs attrs)) text))]
      (dom/div
        #js {:id (:id text-item)}
        (ui-text-item text-item)
        (dom/form
          nil
          (dom/fieldset
            nil
            (dom/legend nil "Is this " (dom/strong nil (:name s-class)) "?")
            (dom/div #js {:className "form-check"}
                     (classify-button {:value "yes"} "yes")
                     (classify-button {:value "no"} "no")
                     (classify-button {:value "skip"} "skip"))))))))

(def ui-classifiable-text-item (prim/factory ClassifiableTextItem))

(defui ^:once ClassifiableTextItemList
  static prim/IQuery
  (query [this] [{:items (prim/get-query ClassifiableTextItem)}])
  Object
  (render [this]
    (let [{:keys [items]} (prim/props this)]
      (dom/div
        nil (dom/ol
              #js {:className "list-group"}
              (map ui-classifiable-text-item items))))))

(def ui-classifiable-text-item-list (prim/factory ClassifiableTextItemList))
