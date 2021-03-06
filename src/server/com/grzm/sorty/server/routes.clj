(ns com.grzm.sorty.server.routes
  (:require
    [com.grzm.component.pedestal :as pedestal]
    [com.grzm.sorty.server.handlers :as handlers]
    [com.grzm.sorty.server.interceptor :as si]
    [io.pedestal.http :as http]
    [io.pedestal.http.body-params :as body-params]))

(def common-interceptors [(body-params/body-params)
                          http/html-body])

(defn with-common
  [h]
  (conj common-interceptors h))

(def api-interceptors
  [pedestal/strip-component
   si/fulcro-body-params
   (si/fulcro-response)
   (pedestal/using-component :app)
   (pedestal/using-component :api)
   `handlers/api])

(def routes
  #{["/" :get `handlers/index :route-name :index]
    ["/hallo" :get `handlers/hallo :route-name :hallo]
    ["/api" :get api-interceptors :route-name :api-get]
    ["/api" :post api-interceptors :route-name :api-post]})
