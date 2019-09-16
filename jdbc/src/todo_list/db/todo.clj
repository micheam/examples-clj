(ns todo-list.db.todo
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]

            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]

            [todo-list.spec :as todo-list]
            [todo-list.db.util :refer [get-update-count]]))

(defn create!
  [conn params]
  (let [todo (select-keys
              params [:todo/id
                      :todo/title
                      :todo/done])]
    (sql/insert! conn :todo todo)))

(defn list-all
  [conn] (sql/query conn ["SELECT * FROM todo"]))

(defn get-one
  [conn id]
  (sql/get-by-id conn :todo id))

(defn exist?
  [conn id]
  (let [found (sql/get-by-id conn :todo id)]
    (not (nil? found))))

(defn update!
  [conn id param]
  (-> (sql/update! conn :todo param {:id id})
      (get-update-count)
      (= 1)))

(defn delete!
  [conn id]
  (-> (sql/delete! conn :todo ["id = ?" id])
      (get-update-count)
      (= 1)))
