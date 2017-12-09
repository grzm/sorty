(ns cljs.user
  (:require
   [com.grzm.sorty.client.app :as app]))

(defn refresh
  []
  (swap! app/app app/mount))

;; for initial mount
(refresh)
