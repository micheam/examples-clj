(ns todo-list.db.util)

(defn get-update-count [m]
  (get m :next.jdbc/update-count))
