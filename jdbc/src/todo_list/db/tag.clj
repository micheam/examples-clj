(ns todo-list.db.tag
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]

            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]

            [todo-list.spec :as todo-list]
            [todo-list.db.util :refer [get-update-count]]))

(defn create!
  [conn params]
  (let [tag (select-keys
             params [:tag/id
                     :tag/name])]
    (sql/insert! conn :tag tag)))

(defn list-all
  [conn] (sql/query conn ["SELECT * FROM tag"]))

(defn get-one
  [conn id]
  (->> (str id)
       (sql/get-by-id conn :tag)))

(defn get-one-by-name
  [conn n]
  (first (sql/query conn ["SELECT * FROM tag WHERE name=? limit 1" n])))

(defn update!
  [conn id param]
  (->  (sql/update! conn :tag param {:id id})
       (get-update-count)
       (= 1)))

(defn delete!
  [conn id]
  (-> (sql/delete! conn :tag ["id = ?" id])
      (get-update-count)
      (= 1)))
