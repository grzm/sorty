(ns com.grzm.sorty.server.test.fixture
  (:require
   [com.grzm.sorty.server.test.system :as system]))

(defn system-init-fn
  []
  (let [res (system/system)]
    (constantly res)))

(def system nil)
