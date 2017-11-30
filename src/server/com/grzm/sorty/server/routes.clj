(ns com.grzm.sorty.server.routes
  (:require
   [com.grzm.sorty.server.handlers :as handlers]
   [grzm.component.pedestal :as pedestal]
   [io.pedestal.http :as http]
   [io.pedestal.http.body-params :as body-params]))

(def common-interceptors [(body-params/body-params)
                          http/html-body])

(defn with-common
  [h]
  (conj common-interceptors h))

(def routes
  #{["/hallo" :get `handlers/hallo :route-name :hallo]})
