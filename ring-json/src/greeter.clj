(ns greeter
  (:require [ring.adapter.jetty :as s]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.response :refer :all])
  (:gen-class))

(defonce server (atom nil))

(defn build-message [user]
  (str "Hello, " user))

(defn handler [req]
  (let [user (get-in req [:params :user])]
    (response {:message (build-message user)})))

(def app
  (-> handler
      wrap-keyword-params
      wrap-json-params
      wrap-json-response
      wrap-params))

(defn start []
  (when-not @server
    (reset! server (s/run-jetty app {:port 3000 :join? false}))))

(defn stop []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart []
  (when @server
    (stop)
    (start)))

(defn -main [& args]
  (start))

