(ns greeter
  (:gen-class)
  (:require [clojure.string :as str]
            [ring.adapter.jetty :refer :all]
            [ring.middleware.defaults :refer :all]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]))

(defonce server (atom nil))

(defroutes app
  (GET "/" [] "<h1>Hello, World.</h1>")
  (GET "/:parson" [parson] (str "<h1>" "Hello, " parson "." "</h1>"))
  (route/not-found "<h1>Page not found</h1>"))

(def site
  (wrap-defaults app site-defaults))

(defn start []
  (when-not @server
    (reset! server (run-jetty site {:port 3000 :join? false}))))

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

