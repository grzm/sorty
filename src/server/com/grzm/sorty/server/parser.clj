(ns com.grzm.sorty.server.parser
  (:refer-clojure :exclude [read])
  (:require
    [com.grzm.sorty.server.app :as app]
    [fulcro.server :as fs]
    [io.pedestal.log :as log]))

(defmulti read fs/dispatch)
(defmethod read :default
  [_env key params]
  (log/error :msg "Unrecognized read" :key key :params params))

(defmulti mutate fs/dispatch)
(defmethod mutate :default
  [_env key params]
  (log/error :msg "Unrecognized mutation" :key key :params params))

(def parser (fs/parser {:read read :mutate mutate}))

(defmethod read :initial/unclassified
  [{:keys [_app] :as _env} _ _]
  {:value
   {:queue/active-index 0
    :queue/id           :unclassified
    :queue/items        (mapv #(hash-map
                                 :item/id %
                                 :item/text (str "Text item " %)
                                 :s-class {:id 4 :name "spam"})
                              (range 10))}})

(defmethod mutate 'com.grzm.sorty.client.ui.classifier/classify-item
  [{:keys [app] :as _env} key params]
  (log/warn ::mutate "classifying" :params params))
