(ns com.grzm.sorty.server
  (:gen-class)
  (:require
   [com.grzm.logback.MainDiscriminator :as main-discriminator]
   [com.grzm.sorty.server.config :as config]
   [com.grzm.sorty.server.system :as system]
   [io.pedestal.log :as log]))

(defn run-dev
  "Entry point for `lein run-dev`"
  [& args]
  (main-discriminator/set-value "dev-main")
  (log/info :main "starting dev")
  (system/start (config/config :dev))
  (log/info :main "started dev"))

(defn -main
  "Entry point for production"
  [& args]
  (main-discriminator/set-value)
  (log/info :main "starting")
  (system/start (config/config :prod))
  (log/info :main "started"))
