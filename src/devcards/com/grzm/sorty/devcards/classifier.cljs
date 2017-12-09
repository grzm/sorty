(ns com.grzm.sorty.devcards.classifier
  (:require
   [com.grzm.sorty.client.ui.classifier :as classifier]
   [devcards.core :refer-macros [defcard]]
   [fulcro.client.cards :refer [defcard-fulcro]]
   [fulcro.client.dom :as dom]
   [fulcro.client.logging :as log]
   [fulcro.client.mutations :as m :refer [defmutation]]
   [fulcro.client.primitives :as prim :refer-macros [defui]]))

(defui ^:once ClassifiableTextItemRoot
  static prim/IQuery
  (query [this]
    [:ui/react-key {:item (prim/get-query classifier/ClassifiableTextItem)}])

  Object
  (render [this]
    (let [{:keys [ui/react-key item]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (classifier/ui-classifiable-text-item item)))))

(defcard-fulcro classifiable-text-item
  ClassifiableTextItemRoot
  {:item {:s-class   {:id 4 :name "spam"}
          :text-item {:id 1 :text "Here's some text"}}}
  {:inspect-data true})

(defui ^:once ClassifiableTextItemListRoot
  static prim/IQuery
  (query [this]
    [:ui/react-key {:unclassified (prim/get-query classifier/ClassifiableTextItemList)}])

  Object
  (render [this]
    (let [{:keys [ui/react-key unclassified]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (classifier/ui-classifiable-text-item-list unclassified)))))

(defcard-fulcro classification-text-item-list
  ClassifiableTextItemListRoot
  {:unclassified
   {:item-list/id    :unclassified
    :item-list/items [{:s-class   {:id 4 :name "spam"}
                       :text-item {:id 1 :text "Here's some text"}}
                      {:s-class   {:id 4 :name "spam"}
                       :text-item {:id 2 :text "Here's some other text"}}
                      {:s-class   {:id 4 :name "spam"}
                       :text-item {:id 3 :text "Hey, this is text, too"}}]}
   :classified
   {:item-list/id    :classified
    :item-list/items []}}
  {:inspect-data true})
