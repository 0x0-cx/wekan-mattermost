(ns wekan-mattermost.core
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :refer [response]]
            [jsonista.core :as j]))

;; TODO
(def mapper (j/object-mapper {:encode-key-fn name :decode-key-fn keyword}))

(defn app [req]
  (println (pr-str req))
  (if (-> req :request-method (= :post))
    (response (j/write-value-as-string (-> req :body j/read-value)))
    (response "Please send POST with wekan wenhook: https://github.com/wekan/wekan/wiki/Webhook-data")))

(defn start-http-server [{:keys [port]}]
  (let [port (Integer. (or (System/getenv "port") port))]
    (run-jetty #'app {:port port :join? false})))

(defn -main [& args]
  (start-http-server {:port 3000}))