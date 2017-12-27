(ns com.grzm.sorty.devcards.classifier
  (:require
    [com.grzm.sorty.client.ui.classifier :as classifier]
    [devcards.core :refer-macros [defcard]]
    [fulcro.client :as fc]
    [fulcro.client.cards :refer [defcard-fulcro]]
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.mutations :as m :refer [defmutation]]
    [fulcro.client.primitives :as prim :refer-macros [defsc defui]]))

(defsc QueueListRoot
  [this {:keys [ui/react-key unclassified]}]
  {:query         [:ui/react-key
                   {:unclassified (prim/get-query classifier/QueueList)}]
   :initial-state (fn [_]
                    {:unclassified (prim/get-initial-state
                                     classifier/QueueList {:queue/id :unclassified})})}
  (dom/div
    #js {:key react-key}
    (classifier/ui-queue-list unclassified)))

(defcard-fulcro queue-list
  QueueListRoot
  nil
  {:inspect-data true
   :fulcro       {:started-callback
                  (fn [app]
                    (fc/merge-state!
                      app classifier/QueueList
                      {:queue/active-index 0
                       :queue/id           :unclassified
                       :queue/items        (mapv #(hash-map
                                                    :id %
                                                    :text (str "Text item " %)
                                                    :s-class {:id 4 :name "spam"})
                                                 (range 10))}))}})
