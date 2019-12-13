(ns wekan-mattermost.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [jsonista.core :as j]))

(defn app [req]
  (println (pr-str {:req req}))
  {:status 200 :headers {} :body (j/write-value-as-string {:text "hello clojure"})})

(defn start-http-server [{:keys [port]}]
  (let [port (Integer. (or (System/getenv "port") port))]
    (run-jetty #'app {:port port :join? false})))

(defn -main [& args]
  (start-http-server {:port 3000}))