(ns todo-list.db.todo-tag
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]

            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]

            [todo-list.spec :as todo-list]
            [todo-list.db.util :refer [get-update-count]]))

(defn create!
  [conn todo tag]
  (let [todo-tag {:todo-tag/todo todo, :todo-tag/tag tag}]
    (sql/insert! conn :todo_tag todo-tag)))

(defn list-all
  [conn] (sql/query conn ["SELECT * FROM todo_tag"]))

(defn list-all-tag-by-todo
  [conn todo-id]
  (sql/query
   conn
   ["SELECT tag.id, tag.name 
      FROM todo_tag
      LEFT OUTER JOIN tag
        ON todo_tag.tag = tag.id
      WHERE todo_tag.todo = ?" todo-id]))

(defn get-one
  [conn id]
  (->> (str id)
       (sql/get-by-id conn :todo_tag)))

(defn delete!
  [conn id]
  (-> (sql/delete! conn :todo_tag ["id = ?" id])
      (get-update-count)
      (= 1)))

(defn delete-by-todo!
  [conn todo-id]
  (-> (sql/delete! conn :todo_tag ["todo = ?" todo-id])
      (get-update-count)))
