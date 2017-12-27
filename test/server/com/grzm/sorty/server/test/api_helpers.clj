(ns com.grzm.sorty.server.test.api-helpers
  (:require
    [clojure.test :refer [is]]
    [cognitect.transit :as ct]
    [com.grzm.sorty.server.fulcro-util :as fu]
    [fulcro.server :as fs]
    [io.pedestal.log :as log]
    [peridot.core :as p]))

(def api-endpoint "/api")
(def csrf-token-header-key "X-TEST-HELPER-CSRF")

;; fulcro doesn't append charset=UTF-8 to transit+json
(def transit+json "application/transit+json")

;; transit helpers

(defn edn->body [edn] (fu/write-transit edn))

(def body->edn fu/read-transit)

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
                 :headers {"x-csrf-token" (response-csrf-token response)}
                 :content-type transit+json)))

(defn test-api-request
  [state request-edn response-edn]
  (-> state (api-request request-edn)
      (doto ((fn [{:keys [response]}]
               (let [{:keys [body status headers]} response]
                 (is (= transit+json (get headers "Content-Type")))
                 (is (= 200 status))
                 (is (= response-edn (body->edn body)))))))))