(ns com.grzm.sorty.server.test.stub-persistor
  (:require
    [com.grzm.sorty.server.app :as app]))

(defrecord Persistor []
  app/ReadAPI
  (-unclassified-items [_this _params])
  app/WriteAPI
  (-new-classification [_this _params]))

(defn persistor [] (->Persistor))