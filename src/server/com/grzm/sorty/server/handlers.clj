(ns com.grzm.sorty.server.handlers
  (:require
   [com.grzm.component.pedestal :as pedestal]))

(defn hallo
  [request]
  {:status 200 :body "Hallo, world!"})
