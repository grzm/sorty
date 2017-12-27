(ns com.grzm.sorty.server.config
  (:require
   [com.grzm.sorty.server.routes :as routes]
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]))

(def http-port 8765)

(def default-service-map
  {::http/routes            routes/routes
   ::http/resource-path     "/public"
   ::http/type              :jetty
   ::http/container-options {:h2c? true
                             :h2?  false
                             :ssl? false}})

(defn pedestal-config-fn
  [env http-port]
  (let [service-map (assoc default-service-map
                           :env env
                           ::http/port http-port)]
    (fn []
      (condp = env
        :prod service-map
        :dev  (-> service-map
                  (merge
                    {::http/join?           false
                     ::http/routes          #(route/expand-routes (deref #'routes/routes))
                     ::http/allowed-origins {:creds           true
                                             :allowed-origins (constantly true)}
                     ::http/secure-headers {:content-security-policy-settings {:object-src "none"}}})
                  http/default-interceptors
                  http/dev-interceptors)))))

(defn config
  [env]
  {:pedestal {:config-fn (pedestal-config-fn env http-port)}})
