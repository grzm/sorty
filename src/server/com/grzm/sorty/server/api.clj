(ns com.grzm.sorty.server.api
  (:require
    [clojure.set :as set]
    [com.grzm.component.pedestal :as cp]
    [com.grzm.sorty.server.parser :as parser]
    [com.stuartsierra.component :as component]
    [fulcro.server :as fs]
    [io.pedestal.log :as log]))

(defn assert-env-keys
  [api-handler env-keys]
  (let [comp-keys (set (keys api-handler))]
    (assert (every? comp-keys env-keys)
            (str "You asked to inject " env-keys
                 " but " (set/difference env-keys comp-keys)
                 " do not exist"))))

(defn parsing-env
  "Confirms the available components (named by key) are available
  to the API handler, and returns only those components."
  [api-handler env-keys]
  (assert-env-keys api-handler env-keys)
  (select-keys api-handler env-keys))

(defrecord APIHandler [parser env-keys]
  component/Lifecycle
  (start [this]
    (log/info :msg "Starting FulcroAPIHandler")
    (assoc this :env (parsing-env this env-keys)))
  (stop [this]
    (log/info :msg "Stopping FulcroAPIHandler")
    this))

(defn api-handler
  ([]
   (api-handler parser/parser #{:app}))
  ([parser env-keys]
   (->APIHandler parser env-keys)))

(defn handle-api-request
  [{:keys [parser env]} {:keys [transit-params] :as request}]
  (fs/handle-api-request parser (assoc env :request request) transit-params))

(defn api-request-handler
  [api-handler-key]
  (fn [request]
    (let [api-handler (cp/use-component request api-handler-key)]
      (handle-api-request api-handler request))))