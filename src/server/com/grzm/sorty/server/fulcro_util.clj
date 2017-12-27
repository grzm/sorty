(ns com.grzm.sorty.server.fulcro-util
  (:require
    [cognitect.transit :as ct]
    [fulcro.server :as fs]
    [io.pedestal.interceptor.helpers :refer [on-response]])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))

(defn read-transit
  "Translates a body String to an EDN structure.

  This is very similar to fulcro.server/read-transit, which is marked private."
  [body]
  (let [in (ByteArrayInputStream. (.getBytes body))
        reader (fs/reader in)]
    (ct/read reader)))

(defn write-transit
  ([edn]
    (write-transit edn nil {}))
  ([edn _t opts]
   (let [baos (ByteArrayOutputStream.)
         w (fs/writer baos opts)
         _ (ct/write w edn)
         ret (.toString baos)]
     (.reset baos)
     ret)))
