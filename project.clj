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
                 [io.pedestal/pedestal.jetty "0.5.3"]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [org.slf4j/jcl-over-slf4j "1.7.22"]
                 [org.slf4j/jul-to-slf4j "1.7.22"]
                 [org.slf4j/log4j-over-slf4j "1.7.22"]
                 [peridot "0.5.0"] ;; scope test
                 [reloaded.repl "0.2.4"]]
  :resource-paths ["resources" "config"]
  ;; See: https://www.eclipse.org/jetty/documentation/current/alpn-chapter.html#alpn-versions
  :java-agents [[org.mortbay.jetty.alpn/jetty-alpn-agent "2.0.5"]]
  ;;  :java-agents [[org.mortbay.jetty.alpn/alpn-boot "8.1.3.v20150130"]] ;; JDK 1.8.0_31/40/45
  :main ^{:skip-aot true} com.grzm.sorty.server
  :profiles {:dev     {:source-paths ["src/server"]
                       :aliases      {"run-dev" ["trampoline" "run" "-m" "com.grzm.sorty.server/run-dev"]}}
             :test    {:jvm-opts       ["-Dlogback.configurationFile=test/config/logback.xml"]
                       :source-paths   ["test/server"]
                       :resource-paths ["test/resources"]
                       :env            {:public-http-host   "test.sorty.com"
                                        :public-http-port   "443"
                                        :public-http-scheme "https"}}
             :server  {:main com.grzm.sorty.server}
             :uberjar {:aot [com.grzm.sorty.server]}}
  :plugins [[lein-cljfmt "0.5.7"]]
  :cljfmt {:indents {merge [[:inner 0]]
                     system-map [[:inner 0]]}})
