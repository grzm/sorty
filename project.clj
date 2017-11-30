(defproject com.grzm/sorty.alpha "0.0.1-SNAPSHOT"
  :description "Simple web-based classification app"
  :url "https://github.com/grzm/sorty.alpha"
  :license {:name "MIT"
            :url  "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.0.0"
  :dependencies [[ch.qos.logback/logback-classic "1.1.7"
                  :exclusions [org.slf4j/slf4j-api]]
                 [com.grzm/logback-discriminator "0.1.1-SNAPSHOT"
                  :exclusions [ch.qos.logback/logback-classic]]
                 [com.stuartsierra/component "0.3.2"]
                 [grzm/component.pedestal "0.0.1-SNAPSHOT"]
                 [fulcrologic/fulcro "1.0.0-beta10"]
                 [io.pedestal/pedestal.jetty "0.5.3"]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [org.clojure/clojure "1.9.0-RC2"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.omcljs/om "1.0.0-beta1"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]
                 [reloaded.repl "0.2.4"]]
  :source-paths ["src/server" "src/client"]
  :resource-paths ["resources" "config"]
  :clean-targets ^{:protect false} ["resources/public/js" "target" "out"]
  ;; See: https://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions
  :java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  ;;  :java-agents [[org.mortbay.jetty.alpn/alpn-boot "8.1.3.v20150130"]] ;; JDK 1.8.0_31/40/45
  :main ^{:skip-aot true} com.grzm.sorty.server
  :plugins [[lein-cljfmt "0.5.7"]
            [lein-cljsbuild "1.1.7"]]
  :profiles {:dev {:source-paths ["src/client" "src/dev" "src/server"]
                   :aliases      {"run-dev" ["trampoline" "run" "-m" "com.grzm.sorty.server/run-dev"]
                                  "fw"      ["run" "-m" "clojure.main" "script/figwheel.clj"]}
                   :dependencies [[binaryage/devtools "0.9.4"]
                                  [com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.14"]
                                  [org.clojure/tools.namespace "0.3.0-alpha4"]
                                  [org.clojure/tools.nrepl "0.2.13"]
                                  [peridot "0.5.0"]]
                   :main user}

             :test    {:jvm-opts       ["-Dlogback.configurationFile=test/config/logback.xml"]
                       :source-paths   ["test/server"]
                       :repl-options   {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                       :resource-paths ["test/resources"]
                       :env            {:public-http-host   "test.sorty.com"
                                        :public-http-port   "443"
                                        :public-http-scheme "https"}}
             :server  {:main com.grzm.sorty.server}
             :uberjar {:aot [com.grzm.sorty.server]}}
  :cljfmt {:indents {merge      [[:inner 0]]
                     ;; component
                     system-map [[:inner 0]]
                     ;; om
                     render [[:inner 0]]
                     ;; figwheel
                     css-watcher [[:inner 0]]}}
  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/client"]
                :figwheel     {:on-jsload "cljs.user/refresh"}
                :compiler     {:main          cljs.user
                               :output-to     "resources/public/js/app.js"
                               :output-dir    "resources/public/js/app"
                               :preloads      [devtools.preload]
                               :asset-path    "js/app"
                               :optimizations :none}}]})
