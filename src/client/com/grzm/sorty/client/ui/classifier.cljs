(ns com.grzm.sorty.client.ui.classifier
  (:require
   [fulcro.client.dom :as dom]
   [fulcro.client.logging :as log]
   [fulcro.client.mutations :as m :refer [defmutation]]
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
    (let [{:keys [s-class text-item]}
          (prim/props this)

          classify-fn     (prim/get-computed this :classify-fn)
          default-attrs   {:className "btn btn-primary" :type "button"}
          classify-button (fn [attrs text]
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
                     (classify-button {:value   "yes"
                                       :onClick #(classify-fn text-item s-class :yes)} "yes")
                     (classify-button {:value   "no"
                                       :onClick #(classify-fn text-item s-class :no)} "no")
                     (classify-button {:value   "skip"
                                       :onClick #(classify-fn text-item s-class :skip)} "skip"))))))))

(def ui-classifiable-text-item (prim/factory ClassifiableTextItem
                                             {:keyfn #(get-in % [:text-item :id])}))

;; This relies on knowing the data model *from the root*,
;; which means this component needs to know where it is in composition
;; Yuck.

(defmutation classify-item
  "Classify item"
  [{:keys [item s-class value]}]
  (action [{:keys [state]}]
          (let [path     [:item-list :items]
                old-list (get-in @state path)
                new-list (vec (filter #(not= (:text-item %) item) old-list))]
            (swap! state assoc-in path new-list))))

(defn make-classify-fn [c]
  (fn [item s-class value]
    (prim/transact! c `[(classify-item
                          {:item ~item :s-class ~s-class :value ~value})])))

(defui ^:once ClassifiableTextItemList
  static prim/IQuery
  (query [this] [{:items (prim/get-query ClassifiableTextItem)}])
  Object
  (render [this]
    (let [{:keys [items]} (prim/props this)
          classify-fn     (make-classify-fn this)]
      (dom/div
        nil (dom/ol
              #js {:className "list-group"}
              (map (fn [item]
                     (ui-classifiable-text-item
                       (prim/computed item {:classify-fn classify-fn})))
                   items))))))

(def ui-classifiable-text-item-list (prim/factory ClassifiableTextItemList))
