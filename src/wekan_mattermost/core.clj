(ns wekan-mattermost.core
  (:require [org.httpkit.server :refer [run-server]]
            [org.httpkit.client :as http]
            [ring.util.response :refer [response]]
            [jsonista.core :as j])
  (:gen-class))

;; TODO
(def mapper (j/object-mapper {:encode-key-fn name :decode-key-fn keyword}))

;; main logic
(defn wekan->mattermost [in]
  (let [text (get in "text")]
    {:text text}))

(def client-http-options {:timeout 1000 :headers {"Content-Type" "application/json"}})

(defn http-client [out-body url]
  @(http/post url (assoc client-http-options
                         :body (j/write-value-as-string out-body))))

(defn app [{:keys [url]} req]
  (if (-> req :request-method (= :post))
    (let [out-resp (-> req :body j/read-value wekan->mattermost (http-client url))]
      ; (println {:req (pr-str req) :out-resp out-resp})
      (if (-> out-resp :status (= 200))
        (response "ok" #_())
        (response (str "Webhook failed with: " out-resp))))
    (response "Please send POST with wekan wenhook: https://github.com/wekan/wekan/wiki/Webhook-data")))

(defn start-http-server [{:keys [port url]}]
  (let [port (Integer. (or (System/getenv "PORT") port))
        url (or (System/getenv "URL") url (throw (Exception. "specify URL env")))]
    (run-server (partial app {:url url}) {:port port :join? false})))

(defn -main [& args]
  (start-http-server {}))