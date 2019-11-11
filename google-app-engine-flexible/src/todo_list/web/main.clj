(ns todo-list.web.main
  (:require [todo-list.web.core :refer [start]])
  (:gen-class))

(defn -main [& args]
  (start))
