(ns com.grzm.sorty.devcards.queue-list
  (:require
    [fulcro.client :as fc]
    [fulcro.client.cards :refer [defcard-fulcro]]
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.primitives :as prim :refer-macros [defui]]
    [goog.events :as events]
    [goog.ui.KeyboardShortcutHandler :as ksh])
  (:import [goog.ui KeyboardShortcutHandler]))

(defn install-shortcuts!
  [keybindings]
  (let [handler (KeyboardShortcutHandler. js/document)]
    (dorun (map (fn [[key ident f]]
                  (.registerShortcut handler (str ident) key)
                  (events/listen handler
                                 KeyboardShortcutHandler.EventType.SHORTCUT_TRIGGERED
                                 #(f %)))
                keybindings))
    #(.dispose handler)))

(defui ^:once ListItem
  static prim/Ident
  (ident [_ {:keys [item/id]}] [:item/by-id id])

  static prim/IQuery
  (query [_] [:item/id :item/text])

  static prim/InitialAppState
  (initial-state [_ {:keys [item/id item/text]}] {:item/id id, :item/text text})

  Object
  (render [this]
    (let [{:keys [item/text]} (prim/props this)]
      (dom/li nil text))))

(def ui-list-item (prim/factory ListItem {:key-fn :item/id}))

(defui ^:once ActiveListItem
  static prim/Ident
  (ident [_ {:keys [item/id]}] [:list-item/by-id id])

  static prim/IQuery
  (query [_] [:item/id :item/text])

  static prim/InitialAppState
  (initial-state [_ {:keys [item/id item/text]}] {:item/id id, :item/text text})

  Object
  (render [this]
    (let [{:keys [item/text]} (prim/props this)]
      (dom/li nil (dom/strong nil text)))))

(def ui-active-list-item (prim/factory ActiveListItem {:key-fn :item/id}))

(defmutation move-index
  [{:keys [list-id direction]}]
  (action
    [{:keys [state]}]
    (let [active-index-ident [:queue-list/by-id list-id :queue-list/active-index]]
      (when-let [dir-fn ({:prev inc :next dec} direction)]
        (swap! state update-in active-index-ident dir-fn)))))

(defmutation classify-active
  [{:keys [list-id classification-id]}]
  (action
    [{:keys [state]}]
    (let [active-index-ident [:queue-list/by-id list-id
                              :queue-list/active-index]
          active-index (get-in @state active-index-ident)
          active-item-ident (get-in @state [:queue-list/by-id list-id
                                            :queue-list/items active-index])]
      (reset! state
              (-> @state
                  (assoc-in (conj active-item-ident :classification)
                            classification-id)
                  (update-in active-index-ident inc))))))

(defn mv-index
  [c list-id direction]
  (let [cmd `[(move-index {:list-id   ~list-id
                           :direction ~direction})]]
    (prim/transact! c cmd)))

(defn classify-active-item
  [c list-id classification-id]
  (let [cmd `[(classify-active {:list-id ~list-id
                                :classification-id ~classification-id})]]
    (prim/transact! c cmd)))

(defui ^:once QueueList
  static prim/Ident
  (ident [_ {:keys [queue-list/id]}] [:queue-list/by-id id])

  static prim/IQuery
  (query [_] [:queue-list/active-index
              :queue-list/id
              {:queue-list/items (prim/get-query ListItem)}])

  static prim/InitialAppState
  (initial-state [_ {:keys [queue-list/id]}]
    {:queue-list/id id, :queue-list/items [], :queue-list/active-index 0})

  Object
  (componentDidMount [this]
    (let [{:keys [queue-list/id]} (prim/props this)]
      (prim/set-state! this {:keyboard-shortcut-handler
                             (install-shortcuts!
                               [[events/KeyCodes.UP :next-item
                                 (fn [e]
                                   (when (= (.-identifier e) (str :next-item))
                                     (mv-index this id :next)))]
                                [events/KeyCodes.DOWN :prev-item
                                 (fn [e]
                                   (when (= (.-identifier e) (str :prev-item))
                                     (mv-index this id :prev)))]
                                [events/KeyCodes.A :not-member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :not-member))
                                     (classify-active-item this id :not-member)))]
                                [events/KeyCodes.D :member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :member))
                                     (classify-active-item this id :member)))]
                                [events/KeyCodes.S :skip
                                 (fn [e]
                                   (when (= (.-identifier e) (str :skip))
                                     (classify-active-item this id :skip)))]])})))
  (componentWillUnmount [this]
    ((prim/get-state this :keyboard-shortcut-handler)))

  (render [this]
    (let [{:keys [queue-list/active-index queue-list/items]} (prim/props this)]
      (apply dom/ol nil
             (map-indexed (fn [i item]
                            (if (= active-index i)
                              (ui-active-list-item item)
                              (ui-list-item item)))
                          items)))))

(def ui-queue-list (prim/factory QueueList))

(defui ^:once QueueListRoot
  static prim/IQuery
  (query [_] [:ui/react-key {:q (prim/get-query QueueList)}])

  static prim/InitialAppState
  (initial-state [_ _] {:q (prim/get-initial-state QueueList {:queue-list/id :q})})

  Object
  (render [this]
    (let [{:keys [ui/react-key q]} (prim/props this)]
      (dom/div #js {:key react-key} (ui-queue-list q)))))

(defcard-fulcro queue-list
  QueueListRoot
  nil
  {:inspect-data true
   :fulcro       {:started-callback
                  (fn [app]
                    (fc/merge-state!
                      app QueueList
                      {:queue-list/active-index 0
                       :queue-list/id           :q
                       :queue-list/items        (mapv #(hash-map
                                                         :item/id %
                                                         :item/text (str "item " %))
                                                      (range 10))}))}})