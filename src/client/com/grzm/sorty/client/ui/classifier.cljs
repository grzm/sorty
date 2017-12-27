(ns com.grzm.sorty.client.ui.classifier
  (:require
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.primitives :as prim :refer-macros [defsc defui]]
    [goog.events :as events]
    [goog.ui.KeyboardShortcutHandler :as ksh])
  (:import [goog.ui KeyboardShortcutHandler]))

(defn install-shortcuts!
  "Installs a keyboard shortcut handler with the given keyboard shortcuts.

  A function to dispose of the handler is returned and can be used to remove
  the handler.

  Based on code from https://gist.github.com/rauhs/ec1a7b94a6481ae4cf1d"
  [key-bindings]
  (let [handler (KeyboardShortcutHandler. js/document)]
    (dorun (map (fn [[key ident f]]
                  (.registerShortcut handler (str ident) key)
                  (events/listen handler
                                 ksh/EventType.SHORTCUT_TRIGGERED
                                 #(f %)))
                key-bindings))
    #(.dispose handler)))

(defui ^:once TextItem
  static prim/Ident
  (ident [_ {:keys [item/id]}] [:item/by-id id])

  static prim/IQuery
  (query [_] [:item/id :item/text :s-class])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [item/id item/text s-class]}]
    {:item/id      id
     :item/text    text
     :s-class s-class})

  Object
  (render [this]
    (let [{:keys [item/id item/text member?]} (prim/props this)]
      (dom/li nil
              (dom/p nil text)
              (when member?
                (dom/p nil (str member?)))))))

(def ui-text-item (prim/factory TextItem {:key-fn :id}))

(defui ^:once ActiveTextItem
  static prim/Ident
  (ident [_ {:keys [item/id]}] [:item/by-id id])

  static prim/IQuery
  (query [_] [:item/id :item/text :member? :s-class])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [item/id item/text s-class]}]
    {:item/id   id
     :item/text text
     :s-class   s-class})

  Object
  (componentDidMount [this]
    (let [{:keys [item/id s-class]} (prim/props this)
          classify-fn (prim/get-computed this :classify-fn)]
      (prim/set-state! this {:shortcut-handler
                             (install-shortcuts!
                               [[events/KeyCodes.A :not-member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :not-member))
                                     (classify-fn id s-class :no)))]
                                [events/KeyCodes.D :member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :member))
                                     (classify-fn id s-class :yes)))]
                                [events/KeyCodes.S :skip
                                 (fn [e]
                                   (when (= (.-identifier e) (str :skip))
                                     (classify-fn id s-class :skip)))]])})))

  (componentWillUnmount [this]
    ;; why do I need this when-let?
    (when-let [dispose-of-handler
               (prim/get-state this :shortcut-handler)]
      (dispose-of-handler)))

  (render [this]
    (let [{:keys [item/id item/text member? s-class]} (prim/props this)
          classify-fn (prim/get-computed this :classify-fn)
          default-attrs {:className "btn btn-primary" :type "button"}
          classify-button (fn [attrs text]
                            (dom/button (clj->js (merge default-attrs attrs)) text))]
      (dom/li
        nil
        (dom/form
          nil
          (dom/fieldset
            nil
            (dom/legend nil "Is this " (dom/strong nil (:name s-class)) "?")
            (dom/div nil
                     (dom/p nil text)
                     (when member?
                       (dom/p nil (str member?))))
            (dom/div #js {:className "form-check"}
                     (classify-button {:value   "yes"
                                       :onClick #(classify-fn id s-class :yes)} "yes")
                     (classify-button {:value   "no"
                                       :onClick #(classify-fn id s-class :no)} "no")
                     (classify-button {:value   "skip"
                                       :onClick #(classify-fn id s-class :skip)} "skip"))))))))

(def ui-active-text-item (prim/factory ActiveTextItem {:key-fn :item/id}))

(defmutation classify-item
  "Classify item"
  [{:keys [list-id item-id class-id value]}]
  (action [{:keys [state]}]
          (let [item-ident [:item/by-id item-id]
                list-items-path [:queue/by-id list-id :queue/items]
                item-count (count (get-in @state list-items-path))
                active-index-ident [:queue/by-id list-id
                                    :queue/active-index]
                active-index (get-in @state active-index-ident)]
            (swap! state (fn [s]
                           (cond-> s
                                   (< active-index (dec item-count))
                                   (update-in active-index-ident inc)

                                   true
                                   (assoc-in (conj item-ident :member?) value))))))
  (remote [env] true))

(defn make-classify-fn [c list-id]
  (fn [item-id {class-id :id} value]
    (prim/transact! c `[(classify-item
                          {:list-id  ~list-id,
                           :item-id  ~item-id,
                           :class-id ~class-id,
                           :value    ~value})])))

(defmutation move-index
  [{:keys [list-id direction]}]
  (action
    [{:keys [state]}]
    (let [active-index-ident [:queue/by-id list-id :queue/active-index]
          active-index (get-in @state active-index-ident)
          item-count (count (get-in @state [:queue/by-id list-id
                                            :queue/items]))]
      (case direction
        :prev (when (< 0 active-index)
                (swap! state update-in active-index-ident dec))
        :next (when (< active-index (dec item-count))
                (swap! state update-in active-index-ident inc))))))

(defn mv-index
  [c list-id direction]
  (let [cmd `[(move-index {:list-id ~list-id, :direction ~direction})]]
    (prim/transact! c cmd)))

(defui ^:once QueueList
  static prim/Ident
  (ident [_ {:keys [queue/id]}] [:queue/by-id id])

  static prim/IQuery
  (query [_this] [:queue/active-index
                  :queue/id {:queue/items (prim/get-query ActiveTextItem)}])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [queue/id]}]
    {:queue/id           id
     :queue/items        []
     :queue/active-index 0})

  Object
  (componentDidMount [this]
    (let [{:keys [queue/id]} (prim/props this)]
      (prim/set-state! this {:shortcut-handler
                             (install-shortcuts!
                               [[events/KeyCodes.UP :prev-item
                                 (fn [e]
                                   (when (= (.-identifier e) (str :prev-item))
                                     (mv-index this id :prev)))]
                                [events/KeyCodes.DOWN :next-item
                                 (fn [e]
                                   (when (= (.-identifier e) (str :next-item))
                                     (mv-index this id :next)))]])})))
  (componentWillUnmount [this]
    ((prim/get-state this :shortcut-handler)))

  (render [this]
    (let [{:keys [queue/active-index queue/id queue/items]} (prim/props this)
          classify-fn (make-classify-fn this id)]
      (dom/div
        nil
        (if (seq items)
          (apply dom/ol #js {:className "list-group"}
                 (map-indexed (fn [i item]
                                (if (= active-index i)
                                  (ui-active-text-item
                                    (prim/computed item {:classify-fn classify-fn}))
                                  (ui-text-item item)))
                              items))
          (dom/p nil "no more items"))))))

(def ui-queue-list (prim/factory QueueList))
