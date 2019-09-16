(ns todo-list.db
  (:require [next.jdbc :as jdbc]

            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]

            [todo-list.spec :as todo-list]
            [todo-list.db.todo :as todo]
            [todo-list.db.tag :as tag]
            [todo-list.db.todo-tag :as todo-tag]))

(def db-spec {:dbtype "pgsql"
              :dbname "todo-list"
              :user "postgres"})

(def ds (jdbc/get-connection db-spec))
