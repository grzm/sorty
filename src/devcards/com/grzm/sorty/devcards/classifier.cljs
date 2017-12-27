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

(defui ^:once QueueListRoot
  static prim/IQuery
  (query
    [this]
    [:ui/react-key
     {:unclassified (prim/get-query classifier/QueueList)}])

  static prim/InitialAppState
  (initial-state [_ _]
    {:unclassified (prim/get-initial-state classifier/QueueList {:queue/id :unclassified})})

  Object
  (render
    [this]
    (let [{:keys [ui/react-key unclassified]} (prim/props this)]
      (dom/div
        #js {:key react-key}
        (classifier/ui-queue-list unclassified)))))

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
                                                    :s-class {:id 4 :name "spam"}
                                                    :text-item {:id   %
                                                                :text (str "Text item " %)})
                                                 (range 10))}))}})
