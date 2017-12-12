(ns com.grzm.sorty.server.handlers-test
  (:require
   [clojure.test :refer [are deftest is]]
   [server.com.grzm.sorty.server.test.api-helpers :refer [test-api-request]]
   [com.grzm.sorty.server.test.fixture :refer [system system-init-fn]]
   [com.grzm.component.pedestal.test :refer [with-system ring-handler]]
   [peridot.core :as p]))

(deftest hallo
  (with-system #'system system-init-fn
    (-> (p/session (ring-handler system))
        (p/request "/hallo")
        (doto ((fn [{:keys [response]}]
                 (is (= 200 (:status response)))))))))

(deftest api-get
  (with-system #'system system-init-fn
    (-> (p/session (ring-handler system))
        (p/request "/api")
        (doto ((fn [{:keys [response]}]
                 (is (= 200 (:status response)))))))))

(deftest api-post
  (with-system #'system system-init-fn
    (-> (p/session (ring-handler system))
        (p/request "/api" :request-method :post)
        (doto ((fn [{:keys [response]}]
                 (is (= 200 (:status response)))))))))

(deftest api-initial-load
  (with-system #'system system-init-fn
    (let [load-query `[({:initial/unclassified 0})]]
      (-> (p/session (ring-handler system))
          (test-api-request load-query
                            {})))))