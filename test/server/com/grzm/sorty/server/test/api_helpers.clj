(ns server.com.grzm.sorty.server.test.api-helpers
  (:require
    [clojure.test :refer [is]]
    [cognitect.transit :as ct]
    [fulcro.server :as fs]
    [fulcro.transit :as transit]
    [io.pedestal.log :as log]
    [peridot.core :as p])
  (:import
    (java.io ByteArrayOutputStream ByteArrayInputStream)))

(def api-endpoint "/api")
(def csrf-token-header-key "X-TEST-HELPER-CSRF")

;; fulcro doesn't append charset=UTF-8 to transit+json
(def transit+json "application/transit+json")

;; transit helpers

(defn edn->body
  "Translates an EDN structure to a body String.

  This is effectively a copy of fulcro.server/write, which is marked private"
  [edn]
  (let [baos (ByteArrayOutputStream.)
        w (fs/writer baos)
        _ (ct/write w edn)
        body (.toString baos)]
    ;; why is this necessary? It's going out of scope, so will be garbage-collected.
    (.reset baos)
    body))

(defn body->edn
  "Translates a body String to an EDN structure.

  This is very similar to fulcro.server/read-transit, which is marked private."
  [body]
  (let [in (ByteArrayInputStream. (.getBytes body))
        reader (fs/reader in)]
    (try
      (ct/read reader)
      (catch Exception e
        (log/error :msg (str e)
                   :body (str body)
                   :stack-trace (with-out-str (.printStackTrace e)))))))

(defn response-csrf-token
  "Extracts CSRF token from server response"
  [response]
  (get-in response [:headers csrf-token-header-key]))

(defn api-request
  "Helper function to make Fulco Api requests"
  [{:keys [response] :as state} edn]
  (-> state
      (p/request api-endpoint
                 :request-method :post
                 :body (edn->body edn)
                 :headers {"x-csrf-token" (response-csrf-token response)})))

(defn test-api-request
  [state request-edn response-edn]
  (-> state (api-request request-edn)
      (doto ((fn [{:keys [response]}]
               (let [{:keys [body status headers]} response]
                 (is (= transit+json (get headers "Content-Type")))
                 (is (= 200 status))
                 (is (= response-edn (body->edn body)))))))))