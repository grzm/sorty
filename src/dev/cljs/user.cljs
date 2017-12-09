(ns cljs.user
  (:require
   [com.grzm.sorty.client.app :as app]
   [fulcro.client.primitives :as prim]))

(defn refresh
  []
  (swap! app/app app/mount))

;; for initial mount
(refresh)

(defn app-state-value
  "Return current app state value for the given app atom,
  or the main application app atom if no app atom is given."
  ([]
   (app-state-value app/app))
  ([app]
   @(prim/app-state
      (:reconciler @app))))
