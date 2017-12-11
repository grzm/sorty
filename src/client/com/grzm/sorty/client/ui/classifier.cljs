(ns com.grzm.sorty.client.ui.classifier
  (:require
   [fulcro.client.dom :as dom]
   [fulcro.client.logging :as log]
   [fulcro.client.mutations :as m :refer [defmutation]]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(defui ^:once ClassifiableTextItem
  static prim/Ident
  (ident
    [c {:keys [text-item] :as props}]
    [:classifiable-text-item/by-id (:id text-item)])

  static prim/IQuery
  (query [this] [:s-class :text-item])

  static prim/InitialAppState
  (initial-state
    [c {:keys [s-class text-item] :as params}]
    {:s-class   s-class
     :text-item text-item})

  Object
  (render [this]
          (let [{:keys [s-class text-item]}
                (prim/props this)
                classify-fn (prim/get-computed this :classify-fn)
                default-attrs {:className "btn btn-primary" :type "button"}
                classify-button (fn [attrs text]
                                  (dom/button (clj->js (merge default-attrs attrs)) text))]
            (dom/div
              #js {:id (:id text-item)}
              (dom/div nil (:text text-item))
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

(def ui-classifiable-text-item
  (prim/factory ClassifiableTextItem {:keyfn #(str "cti-" (get-in % [:text-item :id]))}))

(defmutation classify-item
  "Classify item"
  [{:keys [list-id item-id class-id value]}]
  (action [{:keys [state]}]
          (let [item-ident [:classifiable-text-item/by-id item-id]
                list-items-path [:item-list/by-id list-id :item-list/items]
                new-state (swap! state update-in list-items-path
                                 (fn [items]
                                   (vec (filter #(not= item-ident %) items))))]
            new-state)))

(defn make-classify-fn [c list-id]
  (fn [{item-id :id} {class-id :id} value]
    (prim/transact! c `[(classify-item
                          {:list-id ~list-id :item-id ~item-id :class-id ~class-id :value ~value})])))

(defui ^:once ClassifiableTextItemList
  static prim/Ident
  (ident [c {:keys [item-list/id]}]
         [:item-list/by-id id])

  static prim/IQuery
  (query [this] [:item-list/id {:item-list/items (prim/get-query ClassifiableTextItem)}])

  static prim/InitialAppState
  (initial-state
    [c {:keys [item-list/id item-list/items]}]
    {:item-list/id    id
     :item-list/items (if (= :unclassified id)
                        [(prim/get-initial-state ClassifiableTextItem
                                                 {:s-class   {:id 4 :name "spam"}
                                                  :text-item {:id 1 :text "Here's some text"}})
                         (prim/get-initial-state ClassifiableTextItem
                                                 {:s-class   {:id 4 :name "spam"}
                                                  :text-item {:id 2 :text "Here's some other text"}})
                         (prim/get-initial-state ClassifiableTextItem
                                                 {:s-class   {:id 4 :name "spam"}
                                                  :text-item {:id 3 :text "Hey, this is text, too"}})])})

  Object
  (render [this]
          (let [{:keys [item-list/id item-list/items]} (prim/props this)
                classify-fn (make-classify-fn this id)]
            (dom/div
              nil (dom/ol
                    #js {:className "list-group"}
                    (map (fn [item]
                           (ui-classifiable-text-item
                             (prim/computed item {:classify-fn classify-fn})))
                         items))))))

(def ui-classifiable-text-item-list (prim/factory ClassifiableTextItemList))
