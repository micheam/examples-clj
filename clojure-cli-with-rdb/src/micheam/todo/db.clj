(ns micheam.todo.db
  (:require [clojure.string :as str]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
            [java-time :as jtime]
            [micheam.todo.specs :as specs]
            [clojure.spec.alpha :as s])
  (:import [java.util Locale]))

(defn- lower-kebab
  [^String s]
  (-> (.toLowerCase s (Locale/US))
      (str/replace #"_" "-")))

(defn- snake-case 
  [^String s] 
  (str/replace s #"-" "_"))

(s/fdef row->todo
  :args (s/cat :r map?)
  :ret map?)

(defn- row->todo
  [r]
  (let [id (java.util.UUID/fromString (:todo/id r))]
    (assoc r :todo/id id)))

(def opt
  {:builder-fn rs/as-modified-maps
   :qualifier-fn lower-kebab
   :label-fn lower-kebab
   :table-fn snake-case
   :column-fn snake-case})

(def ^:dynamic *transaction-opt*
  {:isolation     :read-committed
   :readonly      false
   :rollback-only false})

(defn drop-table!
  [db-spec]
  (with-open [conn (jdbc/get-connection db-spec)] 
    (jdbc/execute! conn ["DROP TABLE IF EXISTS todo"])))

(defn truncate-table!
  [db-spec]
  (with-open [conn (jdbc/get-connection db-spec)] 
    (jdbc/execute! conn ["truncate TABLE todo;"])))

(defn create-table!
  [db-spec]
  (with-open [conn (jdbc/get-connection db-spec)] 
    (jdbc/execute! 
     conn ["CREATE TABLE IF NOT EXISTS todo (
            id VARCHAR PRIMARY KEY
            ,title VARCHAR NOT NULL
            ,created_at TIMESTAMP WITH TIME ZONE NOT NULL
            )"])))

(defrecord SQLTodoRepository [db-spec]

  specs/TodoRepository
  (_create! [this todo]
    (jdbc/with-transaction [tx (jdbc/get-connection db-spec) *transaction-opt*]
      (let [id (java.util.UUID/randomUUID)
            created-at (jtime/offset-date-time (jtime/zoned-date-time))]
        (as-> (assoc todo 
                     :todo/id id
                     :todo/created-at created-at) $
          (sql/insert! tx :todo $ opt)
          id))))

  (_edit! [this todo]
    (jdbc/with-transaction [tx (jdbc/get-connection db-spec) *transaction-opt*]
      (let [res (sql/update! tx :todo 
                             (select-keys todo [:todo/title]) 
                             (select-keys todo [:todo/id]))
            updated (:next.jdbc/update-count res)]
        (cond
          (= 1 updated) nil
          (= 0 updated) (throw (ex-info "No Todo Found" {:type :not-found}))
          :else (throw (ex-info "Unexpected Result" {:type :error :res res}))))))

  (_list-all [this]
    (jdbc/with-transaction [tx (jdbc/get-connection db-spec) *transaction-opt*]
      (let [rows (sql/query tx ["SELECT * FROM todo;"] opt)]
        (into [] (for [r rows] (row->todo r))))))

  (_get-by-id [this id]
    (with-open [conn (jdbc/get-connection db-spec)]
      (some-> (sql/get-by-id conn :todo (str id) :todo/id opt)
          row->todo))))

