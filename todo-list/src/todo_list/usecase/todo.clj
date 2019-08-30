(ns todo-list.usecase.todo
  (:require [todo-list.core :refer [new-task-id]]))

(defn list-all [list-all-todo]
  (list-all-todo))

(defn get-one [id get-one-todo]
  (let [found (get-one-todo id)]
    (if (nil? found)
      (throw (ex-info "Todo Not Found" {:type :not-found}))
      found)))

(defn create [todo create-todo]
  (let [id (new-task-id)]
    (-> todo
        (assoc :id id)
        create-todo)))
