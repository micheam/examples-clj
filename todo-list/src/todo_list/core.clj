(ns todo-list.core)

(defn new-task-id []
  (java.util.UUID/randomUUID))
