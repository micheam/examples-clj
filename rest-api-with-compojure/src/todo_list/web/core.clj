(ns todo-list.web.core
  (:require [ring.adapter.jetty :as s]
            [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.util.http-response :refer :all]

            ; original namespaces
            [todo-list.web.route :refer [todo-list-routes]]))

(defonce server (atom nil))

(def app
  (-> todo-list-routes
      wrap-keyword-params
      wrap-json-params
      wrap-json-response
      wrap-params))

(defn start []
  (when-not @server
    (reset! server (s/run-jetty #'app {:port 3000 :join? false}))))

(defn stop []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart []
  (when @server
    (stop)
    (start)))
