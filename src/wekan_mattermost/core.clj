(ns wekan-mattermost.core
  (:require [org.httpkit.server :refer [run-server]]
            [org.httpkit.client :as http]
            [ring.util.response :refer [response]]
            [jsonista.core :as j]
            [clojure.string :as string])
  (:gen-class))

;; TODO
(def mapper (j/object-mapper {:encode-key-fn true :decode-key-fn true}))

(defn add-refer-to-card-name
  [text card reference]
  (string/replace-first text card (str "[" card "](" reference ")")))

(defn clear-message
  [text reference]  
  (string/replace text (str "\n" reference) ""))

(defn clear-user-in-text
  [text user]
  (-> text (string/replace user "") string/trim))

(defn wekan->mattermost
  "creates message"
  [{:keys [text card cardId boardId user]}]
  (let [regexp (re-pattern (str "https?://[\\w\\W]+/b/" boardId "/[\\w\\W]+/" cardId))
        regexp-reference (re-find regexp text)]
    (if regexp-reference
      {:text (-> text (clear-user-in-text user) (add-refer-to-card-name card regexp-reference) (clear-message regexp-reference)) :username user}
      {:text (clear-user-in-text text user) :username user}))) 

(def client-http-options {:timeout 1000 :headers {"Content-Type" "application/json"}})

(defn http-client [out-body url]
  @(http/post url (assoc client-http-options
                         :body (j/write-value-as-string out-body))))

(defn app [{:keys [url]} req]
  (if (-> req :request-method (= :post))
    (let [out-resp (-> req :body (j/read-value mapper) wekan->mattermost (http-client url))]
    ; (println {:req (pr-str req) :out-resp out-resp}) ; debug full request
      (if (-> out-resp :status (= 200))
        (response "POST to mattermost successful")
        (response (str "Webhook failed with: " out-resp))))
    (response "Please send POST with wekan wenhook: https://github.com/wekan/wekan/wiki/Webhook-data \n")))

(defn start-http-server [{:keys [port url]}]
  (let [port (Integer. (or (System/getenv "PORT") port))
        url (or (System/getenv "OUT_URL") url (throw (Exception. "Specify out webhook URL in your environment")))]
    (run-server (partial app {:url url}) {:port port :join? false})))

(defn -main [& args]
  (start-http-server {}))