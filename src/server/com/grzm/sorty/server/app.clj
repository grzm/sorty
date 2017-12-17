(ns com.grzm.sorty.server.app
  (:require
    [clojure.spec.alpha :as s]
    [com.stuartsierra.component :as component]
    [io.pedestal.log :as log]))


;; Component

(defrecord App []
  component/Lifecycle
  (start [this] this)
  (stop [this] this))

(defn app []
  (->App))

(def app? (partial instance? App))

;; ReadAPI

(defprotocol ReadAPI
  "Defines read queries for the application."
  (-unclassified-items [read-api params]
    "Returns unclassified items"))

(defn unclassified-items
  [{:keys [read-api]} params]
  (-unclassified-items read-api params))

(def read-api? (partial satisfies? ReadAPI))

;; WriteAPI

(defprotocol WriteAPI
  "Defines write queries for the application."

  (-new-classification [write-api params]
    "Creates a new classification"))

(def write-api? (partial satisfies? WriteAPI))

(defn new-classification
  [{:keys [write-api]} params]
  "Creates a new classification"
  (-new-classification write-api params))

(s/def ::classifier-id (s/and int? pos?))
(s/def ::class-id (s/and int? pos?))
(s/def ::classification-id (s/and int? neg?))

(s/def ::yes #(= :yes %))
(s/def ::no #(= :no %))
(s/def ::skip #(= :skip %))
(s/def ::classification-result (s/or :yes ::yes, :no ::no, :skip ::skip))

(s/def ::new-classification--params
  (s/keys :req-un [::classifier-id
                   ::class-id
                   ::classification-result]))

(s/def ::new-classification--args
  (s/cat :app app? :params ::new-classification-params))
(s/def ::new-classification
  (s/keys :req-un [::classification-id]))
(s/fdef new-classification
        :args ::new-classification--args
        :ret ::new-classification)


;; Command and query multimethods

(defn dispatch
  "Dispatch function for -command and -query multimethods"
  [_app key _params] key)

(defmulti -command dispatch)
(defmethod -command :default
  [_app cmd params]
  (log/error :msg "Unrecognized command" :command cmd :prams params))

(defmulti -query dispatch)
(defmethod -query :default
  [_app q params]
  (log/error :msg "Unrecognized query" :query q :params params))



(defn dispatch-args [args] (second args))

;; Commands

(defn command
  "Wrapper for -command multimethod, of of which one can hang a spec"
  [app key params]
  (-command app key params))

(defmulti command-args dispatch-args)
(s/def ::command-args (s/multi-spec command-args dispatch-args))
(s/fdef command :args ::command-args)

(defmethod command-args 'classify/new
  [_]
  (s/cat :app app? :key symbol? :params ::new-classification--params))


;; Queries

(defn query
  "Wrapper for -query multimethod, off of which one can hang a spec"
  [app key params]
  (-query app key params))

(defmulti query-args dispatch-args)
(s/def ::query-args (s/multi-spec query-args dispatch-args))
(s/fdef query :args ::query-args)

;; Commands

(defmethod -command 'classify/new
  [app _ params]
  (new-classification app params))

;; Queries

(s/def ::unclassified-items--params
  (s/keys :req-un [::class-id
                   ::classifier-id]))

(defmethod query-args :classify/unclassified-items [_]
  (s/cat :app app? :key keyword? :params ::unclassified-items--params))

(defmethod -query :classify/unclassified-items
  [app _ params]
  (unclassified-items app params))
