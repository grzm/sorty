(ns com.grzm.sorty.server.test.fixture
  (:require
   [com.grzm.sorty.server.test.system :as system]))

(defn system-init-fn
  ([]
    (system-init-fn system/system))
  ([system-fn]
   (let [res (system-fn)]
     (constantly res))))

(def system nil)
