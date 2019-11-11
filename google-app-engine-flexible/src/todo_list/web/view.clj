(ns todo-list.web.view
  (:require [ring.util.http-response :as resp]))

(defn- marshal
  [todo]
  (assoc todo
         :created-at (str (get todo :created-at))))

(defn todo->created
  [todo]
  (resp/created
   (str "/todo/" (get todo :id))))

(defn ok
  []
  (resp/ok))

(defn todo->ok
  [todo]
  (resp/ok (marshal todo)))

(defn todos->ok
  [todos]
  (resp/ok (map marshal todos)))

(defn updated [_]
  (resp/ok))

(defn deleted []
  (resp/ok))

(defn not-found []
  (resp/not-found))

(defn bad-request []
  (resp/bad-request))

(defn internal-server-error []
  (resp/internal-server-error))
