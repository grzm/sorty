(ns com.grzm.sorty.devcards.classifier
  (:require
   [com.grzm.sorty.client.ui.classifier :as classifier]
   [devcards.core :refer-macros [defcard]]
   [fulcro.client :as fc]
   [fulcro.client.cards :refer [defcard-fulcro]]
   [fulcro.client.dom :as dom]
   [fulcro.client.logging :as log]
   [fulcro.client.mutations :as m :refer [defmutation]]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(defui ^:once ClassifiableTextItemListRoot
  static prim/IQuery
  (query
    [this]
    [:ui/react-key
     {:unclassified (prim/get-query classifier/ClassifiableTextItemList)}])

  static prim/InitialAppState
  (initial-state [c params]
                 {:unclassified (prim/get-initial-state classifier/ClassifiableTextItemList {:item-list/id :unclassified})})

  Object
  (render
    [this]
    (let [{:keys [ui/react-key unclassified]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (classifier/ui-classifiable-text-item-list unclassified)))))

(defcard-fulcro text-item-list
  ClassifiableTextItemListRoot
  nil
  {:inspect-data true
   :fulcro       {:started-callback
                  (fn [app]
                    (fc/merge-state! app classifier/ClassifiableTextItemList
                                     {:item-list/id    :unclassified
                                      :item-list/items (mapv #(hash-map
                                                                :s-class {:id 4 :name "spam"}
                                                                :text-item {:id    %
                                                                            :text (str "Text item " %)})
                                                             (range 10))}))}})
