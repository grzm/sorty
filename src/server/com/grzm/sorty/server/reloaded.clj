(ns com.grzm.sorty.server.reloaded
  (:require
   [com.grzm.logback.MainDiscriminator :as main-discriminator]
   [com.grzm.sorty.server.system :as system]
   [com.grzm.sorty.server.config :as config]
   [reloaded.repl :as repl]))

(defn init! []
  (main-discriminator/set-value "com.grzm.sorty.server.reloaded")
  (repl/set-init! #(system/system (config/config :dev))))
