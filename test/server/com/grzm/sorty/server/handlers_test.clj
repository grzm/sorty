(ns com.grzm.sorty.server.handlers-test
  (:require
    [clojure.test :refer [are deftest is]]
    [com.grzm.sorty.server.test.api-helpers :refer [test-api-request]]
    [com.grzm.sorty.server.test.fixture :refer [system system-init-fn]]
    [com.grzm.component.pedestal.test :refer [with-system ring-handler]]
    [peridot.core :as p]))

(deftest hallo
  (with-system #'system system-init-fn
    (-> (p/session (ring-handler system))
        (p/request "/hallo")
        (doto ((fn [{:keys [response]}]
                 (is (= 200 (:status response)))))))))

(deftest api-initial-load
  (with-system #'system system-init-fn
    (let [load-query `[({:initial/unclassified 0})]]
      (-> (p/session (ring-handler system))
          (test-api-request
            load-query
            {:initial/unclassified
             {:item-list/id    :unclassified
              :item-list/items [{:s-class   {:id 4 :name "spam"}
                                 :text-item {:id 1 :text "Here's some text"}}
                                {:s-class   {:id 4 :name "spam"}
                                 :text-item {:id 2 :text "Here's some other text"}}
                                {:s-class   {:id 4 :name "spam"}
                                 :text-item {:id 3 :text "Hey, this is text, too"}}]}})))))