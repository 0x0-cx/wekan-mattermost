(ns user
  (:require [nrepl.server :as nrepl]
            [clojure.tools.namespace.repl :refer [refresh]]
            [wekan-mattermost.core :as core]))

(def http-server (atom nil))

(defn reset []
  (when @http-server (@http-server))

  (refresh)

  ;; TODO: use integrant system & make http & nrepl a part of it
  (reset! http-server (core/start-http-server {:port 3000 :url (System/getenv "URL")}))
  (defonce nrepl-server (nrepl/start-server :port 7888)))