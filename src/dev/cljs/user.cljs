(ns cljs.user
  (:require
   [com.grzm.sorty.client.app :as app]
   [fulcro.client.primitives :as prim]))

(defn refresh
  []
  (swap! app/app app/mount))

;; for initial mount
(refresh)

(defn app-state
  "Return current app state value for the given app atom,
  or the main application app atom if no app atom is given."
  ([]
   (app-state app/app))
  ([app]
   @(prim/app-state
      (:reconciler @app))))

(defn q
  "Shortcut for fulcro.client.primitives/db->tree.

  If data is an atom wrapping a fulcro.client.Application,
  use the current app state"
  ([query data]
    (q query data {}))
  ([query data refs]
   (let [data' (if (and (instance? Atom data)
                        (instance? fulcro.client.Application @data))
                 (app-state data) data)]
     (prim/db->tree query data' refs))))

(defn no-op
  "Something just to ensure this gets loaded in devcard build.
  Called by figwheel on-jsload"
  [])