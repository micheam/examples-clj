(ns todo-list.migration
  (:require [next.jdbc :as jdbc]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]

            [todo-list.spec]
            [todo-list.db :refer [ds]]
            [todo-list.db.todo :as todo]
            [todo-list.db.tag :as tag]
            [todo-list.db.todo-tag :as todo-tag]))

;;; create database

(defn ^:private create-table-todo [conn]
  (jdbc/execute!
   conn ["CREATE TABLE todo (
         id uuid PRIMARY KEY, 
         title VARCHAR(256) NOT NULL,
         done BOOLEAN NOT NULL
       );"]))

(defn ^:private drop-table-todo [conn]
  (jdbc/execute!
   conn ["DROP TABLE IF EXISTS todo;"]))

(defn ^:private create-table-tag [conn]
  (jdbc/execute!
   conn ["CREATE TABLE tag (
         id uuid PRIMARY KEY, 
         name VARCHAR(128) NOT NULL
       );"]))

(defn ^:private drop-table-tag [conn]
  (jdbc/execute!
   conn ["DROP TABLE IF EXISTS tag;"]))

(defn ^:private create-table-todo-tag [conn]
  (jdbc/execute!
   conn ["CREATE TABLE todo_tag (
         tag uuid REFERENCES tag(id),
         todo uuid REFERENCES todo(id), 
         UNIQUE(todo, tag)
       );"]))

(defn ^:private drop-table-todo-tag [conn]
  (jdbc/execute!
   conn ["DROP TABLE IF EXISTS todo_tag;"]))

;;; entry points

(defn ^:private init-schema [conn]
  (create-table-todo conn)
  (create-table-tag conn)
  (create-table-todo-tag conn))

(defn ^:private drop-all-tables [conn]
  (drop-table-todo-tag conn)
  (drop-table-todo conn)
  (drop-table-tag conn))

;;; helpers
#_(do
    (drop-all-tables ds)
    (init-schema ds))

#_(->> (g/generate (s/gen :todo-list.spec/todo))
       (todo/create! ds))

#_(dotimes [_ 10]
    (->> (g/generate (s/gen :todo-list.spec/todo))
         (todo/create! ds)))

#_(->> (g/generate (s/gen :todo-list.spec/tag))
       (tag/create! ds))
