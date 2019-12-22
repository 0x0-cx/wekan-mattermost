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
  (if (nil? reference)
    (if (nil? (string/index-of text "http"))
      {:text text}
      (let [url-pos (string/index-of text "http")
            url (subs text url-pos)]
        (-> text (string/replace url "") string/trim-newline)))
    (string/replace text (str "\n" reference) "")))

(defn wekan->mattermost
  "creates message"
  [{:keys [text card cardId boardId]}]
  (let [regexp (re-pattern (str "https?://[\\w]+/b/" boardId "/[\\w]+/" cardId))
        reference (re-find regexp text)]
    ; TODO change to more simple structure if it real
    (if (nil? card)
      ; if we don't receive card then we clear reference without adding it to text
      (if (nil? (string/index-of text "http"))
        {:text text}
        {:text (clear-message text nil)})
      (if (nil? reference)
        (if (nil? (string/index-of text "http"))
          {:text text}
          {:text (clear-message text nil)})
        {:text (-> text (add-refer-to-card-name card reference) (clear-message reference))})))) 



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