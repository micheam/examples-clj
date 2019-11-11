(ns todo-list.usecase.todo
  (:require
   [clojure.tools.logging :as log]
   [todo-list.core :refer [new-todo-id]]))

(defn list-all [list-all-todo]
  (list-all-todo))

(defn get-one [id get-one-todo]
  (let [found (get-one-todo id)]
    (if (nil? found)
      (throw (ex-info "Todo Not Found" {:type :not-found}))
      found)))

(defn create [todo create-todo]
  (->>
   (new-todo-id)
   (assoc todo :id)
   (create-todo)))

(defn edit [todo get-one-todo update-todo]
  (log/debugf "edit todo %s" todo)
  (let [id (get todo :id)
        found (get-one id get-one-todo)]
    (->
     (merge found todo)
     (update-todo))))

(defn delete-one [id get-one-todo delete-todo]
  (let [found (get-one-todo id)]
    (do (if (nil? found)
          (log/debugf "todo %s not found. " id)
          (delete-todo found))
        nil)))
