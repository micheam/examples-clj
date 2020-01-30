(ns micheam.todo.handler
  (:require [micheam.todo.db :as db]
            [micheam.todo.specs :as specs]))

(def ^:dynamic *db-spec* 
  {:dbtype "pgsql"
   :dbname "todo"
   :user "postgres"})

(defn- init-db [] 
  (db/create-table! *db-spec*))

(def repo
  (db/->SQLTodoRepository *db-spec*))

(defn handle-list
  []
  (init-db)
  (println (specs/list-all repo)))

(defn handle-get
  [str-id]
  (init-db)
  (when-let [found (->>
                    (java.util.UUID/fromString str-id)
                    (specs/get-by-id repo))]
    (println found)))

(defn handle-new
  [title]
  (init-db)
  (let [todo {:todo/title title}]
   (println (specs/create! repo todo))))
