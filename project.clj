(defproject com.grzm/sorty.alpha "0.0.1-SNAPSHOT"
  :description "Simple web-based classification app"
  :url "https://github.com/grzm/sorty.alpha"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :dependencies [[com.taoensso/carmine "2.16.0"]
                 [ch.qos.logback/logback-classic "1.1.7"
                  :exclusions [org.slf4j/slf4j-api]]
                 [com.grzm/logback-discriminator "0.1.1-SNAPSHOT"
                  :exclusions [ch.qos.logback/logback-classic]]
                 [com.grzm/component.pedestal "0.0.2-SNAPSHOT"]
                 [com.cognitect/transit-clj "0.8.300"]
                 [com.grzm/tespresso.alpha "0.1.1-SNAPSHOT"]
                 [com.mchange/c3p0 "0.9.5.2"]
                 [com.stuartsierra/component "0.3.2"]
                 [environ "1.1.0"]
                 [fulcrologic/fulcro "2.0.0-beta6-SNAPSHOT"]
                 [io.pedestal/pedestal.jetty "0.5.3"]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/java.jdbc "0.7.4"]
                 [org.postgresql/postgresql "42.1.4"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]
                 [reloaded.repl "0.2.4"]]
  :source-paths ["src/server" "src/client"]
  :resource-paths ["resources" "config"]
  :clean-targets ^{:protect false} ["resources/public/js" "target" "out"]
  ;; See: https://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions
  :java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  :main ^{:skip-aot true} com.grzm.sorty.server
  :plugins [[lein-cljfmt "0.5.7"]
            [lein-cljsbuild "1.1.7"]
            [lein-environ "1.1.0"]]
  :profiles {:dev     {:source-paths ^:replace ["src/client" "src/dev" "src/server"]
                       :test-paths ^:replace []
                       :aliases      {"run-dev" ["trampoline" "run" "-m" "com.grzm.sorty.server/run-dev"]
                                      "fw"      ["run" "-m" "clojure.main" "script/figwheel.clj"]}
                       :dependencies [[amperity/predis "0.2.1"]
                                      [binaryage/devtools "0.9.4"]
                                      [clj-time "0.14.2"]
                                      [com.cemerick/piggieback "0.2.2"]
                                      [devcards "0.2.4" :exclusions [cljsjs/react-dom cljsjs/react]]
                                      [enlive "1.1.6"]
                                      [figwheel-sidecar "0.5.14"]
                                      [org.clojure/test.check "0.10.0-alpha2"]
                                      [org.clojure/tools.namespace "0.3.0-alpha4"]
                                      [org.clojure/tools.nrepl "0.2.13"]
                                      [peridot "0.5.0"]
                                      [pjstadig/humane-test-output "0.8.3"]
                                      [sablono "0.8.1"]]
                       :main         user
                       :plugins [[com.jakemccrary/lein-test-refresh "0.22.0"]]
                       :injections [(require 'pjstadig.humane-test-output)
                                    (pjstadig.humane-test-output/activate!)]}

             :test    {:jvm-opts       ["-Dlogback.configurationFile=test/config/logback.xml"]
                       :test-paths ^:replace  ["test/server"]
                       :repl-options   {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                       :resource-paths ["test/server/resources"]
                       :env            {:public-http-host   "test.sorty.com"
                                        :public-http-port   "443"
                                        :public-http-scheme "https"}}
             :server  {:main com.grzm.sorty.server}
             :uberjar {:aot [com.grzm.sorty.server]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/client"]
                :figwheel     {:on-jsload "cljs.user/refresh"}
                :compiler     {:main                 cljs.user
                               :output-to            "resources/public/js/app.js"
                               :output-dir           "resources/public/js/app"
                               :asset-path           "js/app"
                               :preloads             [devtools.preload]
                               :optimizations        :none
                               :source-map-timestamp true}}
               {:id           "devcards"
                :source-paths ["src/client" "src/dev" "src/devcards"]
                :figwheel     {:devcards true
                               :on-jsload "cljs.user/no-op"}
                :compiler     {:main                 com.grzm.sorty.devcards
                               :output-to            "resources/public/js/devcards.js"
                               :output-dir           "resources/public/js/devcards"
                               :asset-path           "js/devcards"
                               :preloads             [devtools.preload]
                               :optimizations        :none
                               :source-map-timestamp true}}]}
  :cljfmt {:indents {merge       [[:inner 0]]
                     ;; component
                     system-map  [[:inner 0]]
                     ;; om
                     render      [[:inner 0]]
                     ;; figwheel
                     css-watcher [[:inner 0]]}})
