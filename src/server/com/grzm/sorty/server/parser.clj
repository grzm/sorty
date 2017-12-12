(ns com.grzm.sorty.server.parser
  (:refer-clojure :exclude [read])
  (:require
   [fulcro.server :as fs]))

(defmulti read fs/dispatch)
(defmulti mutate fs/dispatch)

(def parser (fs/parser {:read read :mutate mutate}))