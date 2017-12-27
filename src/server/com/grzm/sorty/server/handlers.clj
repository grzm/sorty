(ns com.grzm.sorty.server.handlers
  (:require
    [com.grzm.component.pedestal :as cp]
    [com.grzm.sorty.server.api :as api]
    [ring.util.response :as response]))

(defn hallo
  [_request]
  {:status 200 :body "Hallo, world!"})

(defn index
  [_request]
  (-> (response/resource-response "index.html" {:root "public"})
      (response/content-type "text/html")))

(def api (api/api-request-handler :api))
