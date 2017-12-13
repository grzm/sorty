(ns com.grzm.sorty.server.interceptor
  (:require
    [cognitect.transit :as ct]
    [com.grzm.sorty.server.fulcro-util :as fu]
    [fulcro.server :as fs]
    [io.pedestal.http.body-params :as body-params]
    [io.pedestal.interceptor.helpers :refer [on-response]]
    [ring.util.response :as response])
  (:import (com.cognitect.transit ReadHandler)
           (fulcro.tempid TempId)
           (java.io ByteArrayOutputStream)))

(def fulcro-body-params
  "Parse body as as Transit with a Fulcro Transit reader."
  (body-params/body-params
    (body-params/default-parser-map
      :transit-options [{:handlers {"fulcro/tempid" (reify ReadHandler
                                     (fromRep [_ id] (TempId. id)))}}])))

(def transit-encodings #{:json :json-verbose :msgpack})

(defn fulcro-response
  "Transit-encode the body of the response with a Fulcro Transit writer."
  ([]
   (fulcro-response {:encoding :json}))
  ([{:keys [encoding opts] :or {encoding :json}}]
   (assert (transit-encodings encoding)
           (format "The encoding must be one of %s" (pr-str transit-encodings)))
   (on-response
     ::fulcro-transit-response
     (fn [{:keys [body headers] :as response}]
       (if (coll? body)
         (let [transit-response (update-in response [:body] fu/write-transit encoding opts)]
           (if (contains? headers "Content-Type")
             transit-response
             (response/content-type transit-response
                                    (format "application/transit+%s;charset=utf-8"))))
         response)))))
