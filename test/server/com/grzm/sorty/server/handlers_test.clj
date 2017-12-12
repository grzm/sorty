(ns com.grzm.sorty.server.handlers-test
  (:require
   [clojure.test :as test :refer [are deftest is]]
   [com.grzm.sorty.server.test.fixture :as fix :refer [system system-init-fn]]
   [com.grzm.component.pedestal.test :refer [with-system ring-handler]]
   [peridot.core :as p]))

(deftest hallo
  (with-system #'system system-init-fn
    (-> (p/session (ring-handler system))
        (p/request "/hallo")
        (doto ((fn [{:keys [response]}]
                 (is (= 200 (:status response)))))))))
