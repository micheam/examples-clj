(ns todo-list.web.core
  (:require [clojure.tools.logging :as log]
            [ring.adapter.jetty :as s]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer :all]

            ; original namespaces
            [todo-list.web.route :refer [todo-list-routes]]))

(defonce server (atom nil))

(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (do
          (log/errorf e "un handle exception: %s" (ex-info e))
          {:status 500 :body "Internal Server Error"})))))

(def app
  (-> todo-list-routes
      wrap-exception-handling
      wrap-keyword-params
      wrap-json-params
      wrap-json-response
      wrap-params))

(defn start []
  (when-not @server
    (reset! server (s/run-jetty #'app {:port 8080 :join? false}))))

(defn stop []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart []
  (when @server
    (stop)
    (start)))
