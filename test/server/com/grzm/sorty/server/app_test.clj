(ns com.grzm.sorty.server.app-test
  (:require
    [clojure.spec.alpha :as s]
    [clojure.spec.gen.alpha :as gen]
    [clojure.spec.test.alpha :as stest]
    [clojure.test :refer [deftest is]]
    [com.grzm.component.pedestal.test :refer [with-system]]
    [com.grzm.sorty.server.test.fixture :as fix :refer [system-init-fn]]
    [com.grzm.sorty.server.app :as app]
    [com.grzm.sorty.server.test.stub-persistor :as persistor]
    [com.grzm.tespresso.alpha]
    [com.stuartsierra.component :as component]))

(alias 'stc 'clojure.spec.test.check)

(defn component-gen-fn [sys comp-key]
  (let [get-fn (if (seq? comp-key) get-in get)]
    (fn []
      (gen/fmap
        (fn [component]
          (when (nil? component)
            (throw (ex-info "Component not found in system"
                            {:key comp-key :system (vec (keys sys))})))
          component)
        (gen/return (get-fn sys comp-key))))))

(defn system []
  (component/system-map
    :app (component/using (app/app)
                          {:read-api  :persistor
                           :write-api :persistor})
    :persistor (persistor/persistor)))

(deftest classify-item
  (with-system #'fix/system #(system-init-fn system)
    (let [app (:app fix/system)
          new-classification {:classifier-id 4, :class-id 5, :classification-result :skip}
          expected {:classification-id 7}
          fns-to-instrument #{`app/command
                              `app/new-classification}
          gen-overrides {}]
      (stest/instrument fns-to-instrument)
      (is (com.grzm.tespresso.spec-test/check? (stest/check ['app/new-classification])
                                                 {:gen gen-overrides
                                                  ::stc/opts {:num-tests 100}}))
      (is (= expected (app/command app 'classify/new new-classification))))))

(deftest fetch-unclassified-items
  (with-system #'fix/system #(system-init-fn system)
    (let [app (:app fix/system)
          fns-to-instrument #{`app/query
                              `app/unclassified-items}
          gen-overrides {}
          expected {}
          params {:classifier-id 3
                  :class-id 3}]
      (stest/instrument fns-to-instrument)
      (is (com.grzm.tespresso.spec-test/check? (stest/check ['app/unclassified-items])
                                               {:gen gen-overrides
                                                ::stc/opts {:num-tests 100}}))
      (is (= expected (app/query app :classify/unclassified-items params))))))