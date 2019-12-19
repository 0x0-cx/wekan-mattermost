(ns wekan-mattermost.core
  (:require [org.httpkit.server :refer [run-server]]
            [org.httpkit.client :as http]
            [ring.util.response :refer [response]]
            [jsonista.core :as j]
            [clojure.string :as string])
  (:gen-class))

;; TODO
(def mapper (j/object-mapper {:encode-key-fn name :decode-key-fn keyword}))

(defn add-refer-to-card-name
  [text card reference]
  (string/replace-first text card (str "[" card "](" reference ")")))

(defn clear-message
  [text reference]
  (string/replace text (str "\n" reference) ""))

(defn wekan->mattermost
  "creates message"
  [in]
  (let [text (get in "text")
        card (get in "card")
        reference (subs text (-> (string/last-index-of text "\n") inc))]
    (if (nil? reference)
      {:text text}
      {:text (-> (add-refer-to-card-name text card reference) (clear-message reference))})))

(def client-http-options {:timeout 1000 :headers {"Content-Type" "application/json"}})

(defn http-client [out-body url]
  @(http/post url (assoc client-http-options
                         :body (j/write-value-as-string out-body))))

(defn app [{:keys [url]} req]
  (if (-> req :request-method (= :post))
    (let [out-resp (-> req :body j/read-value wekan->mattermost (http-client url))]
    ; (println {:req (pr-str req) :out-resp out-resp}) ; debug full request
      (if (-> out-resp :status (= 200))
        (response "POST to mattermost successful")
        (response (str "Webhook failed with: " out-resp))))
    (response "Please send POST with wekan wenhook: https://github.com/wekan/wekan/wiki/Webhook-data")))

(defn start-http-server [{:keys [port url]}]
  (let [port (Integer. (or (System/getenv "PORT") port))
        url (or (System/getenv "URL_mattermost") url (throw (Exception. "Specify mattermost webhook URL env")))]
    (run-server (partial app {:url url}) {:port port :join? false})))

(defn -main [& args]
  (start-http-server {}))