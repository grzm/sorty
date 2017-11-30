(ns cljs.user
  (:require
   [com.grzm.sorty.client.basic-ui :refer [app Root]]
   [fulcro.client.core :as fc]))

(defn refresh
  []
  (swap! app fc/mount Root "app"))

;; for initial mount
(refresh)
