(ns com.grzm.sorty.client.ui.classifier
  (:require
    [fulcro.client.dom :as dom]
    [fulcro.client.logging :as log]
    [fulcro.client.mutations :refer [defmutation]]
    [fulcro.client.primitives :as prim :refer-macros [defui]]
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
  (ident
    [_ {:keys [text-item]}]
    [:classifiable-text-item/by-id (:id text-item)])

  static prim/IQuery
  (query [_] [:s-class :text-item])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [s-class text-item]}]
    {:s-class   s-class
     :text-item text-item})

  Object
  (render [this]
    (let [{:keys [text-item]} (prim/props this)]
      (dom/li nil (:text text-item)))))

(def ui-text-item (prim/factory TextItem {:key-fn #(str "ti-" (get-in % [:text-item :id]))}))

(defui ^:once ClassifiableTextItem
  static prim/Ident
  (ident
    [_ {:keys [text-item]}]
    [:classifiable-text-item/by-id (:id text-item)])

  static prim/IQuery
  (query [_] [:s-class :text-item])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [s-class text-item]}]
    {:s-class   s-class
     :text-item text-item})

  Object
  (componentDidMount [this]
    (let [{:keys [s-class text-item]} (prim/props this)
          classify-fn (prim/get-computed this :classify-fn)]
      (prim/set-state! this {:shortcut-handler
                             (install-shortcuts!
                               [[events/KeyCodes.A :not-member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :not-member))
                                     (classify-fn text-item s-class)))]
                                [events/KeyCodes.D :member
                                 (fn [e]
                                   (when (= (.-identifier e) (str :member))
                                     (classify-fn text-item s-class)))]
                                [events/KeyCodes.S :skip
                                 (fn [e]
                                   (when (= (.-identifier e) (str :skip))
                                     (classify-fn text-item s-class)))]])})))

  (componentWillUnmount [this]
    ;; why do I need this when-let?
    (when-let [dispose-of-handler
               (prim/get-state this :shortcut-handler)]
      (dispose-of-handler)))

  (render [this]
    (let [{:keys [s-class text-item]}
          (prim/props this)
          classify-fn (prim/get-computed this :classify-fn)
          default-attrs {:className "btn btn-primary" :type "button"}
          classify-button (fn [attrs text]
                            (dom/button (clj->js (merge default-attrs attrs)) text))]
      (dom/li
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
                item-count (count (get-in @state list-items-path))
                active-index-ident [:item-list/by-id list-id
                                    :item-list/active-index]
                active-index (get-in @state active-index-ident)]
            (if (< active-index (dec item-count))
              (swap! state update-in active-index-ident inc))))
  (remote [env] true))

(defn make-classify-fn [c list-id]
  (fn [{item-id :id} {class-id :id} value]
    (prim/transact! c `[(classify-item
                          {:list-id ~list-id :item-id ~item-id :class-id ~class-id :value ~value})])))

(defmutation move-index
  [{:keys [list-id direction]}]
  (action
    [{:keys [state]}]
    (let [active-index-ident [:item-list/by-id list-id :item-list/active-index]
          active-index (get-in @state active-index-ident)
          item-count (count (get-in @state [:item-list/by-id list-id
                                            :item-list/items]))]
      (case direction
        :prev (when (< 0 active-index)
                (swap! state update-in active-index-ident dec))
        :next (when (< active-index (dec item-count))
                (swap! state update-in active-index-ident inc))))))

(defn mv-index
  [c list-id direction]
  (let [cmd `[(move-index {:list-id ~list-id, :direction ~direction})]]
    (prim/transact! c cmd)))

(defui ^:once ClassifiableTextItemList
  static prim/Ident
  (ident [c {:keys [item-list/id]}]
    [:item-list/by-id id])

  static prim/IQuery
  (query [_this] [:item-list/active-index
                  :item-list/id {:item-list/items (prim/get-query ClassifiableTextItem)}])

  static prim/InitialAppState
  (initial-state
    [_ {:keys [item-list/id]}]
    {:item-list/id    id
     :item-list/items []
     :item-list/active-index 0})

  Object
  (componentDidMount [this]
    (let [{:keys [item-list/id]} (prim/props this)]
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
    (let [{:keys [item-list/active-index item-list/id item-list/items]} (prim/props this)
          classify-fn (make-classify-fn this id)]
      (dom/div
        nil
        (if (seq items)
          (apply dom/ol #js {:className "list-group"}
                 (map-indexed (fn [i item]
                                (if (= active-index i)
                                  (ui-classifiable-text-item
                                    (prim/computed item {:classify-fn classify-fn}))
                                  (ui-text-item item)))
                              items))
          (dom/p nil "no more items"))))))

(def ui-classifiable-text-item-list (prim/factory ClassifiableTextItemList))
