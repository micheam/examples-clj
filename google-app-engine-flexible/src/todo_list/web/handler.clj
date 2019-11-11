(ns todo-list.web.handler
  (:require
   [clojure.tools.logging :as log]
   [todo-list.core :refer [->todo ->new-todo resolve-todo-id]]
   [todo-list.usecase.todo :as uc]
   [todo-list.web.view :as v]
   [todo-list.interface.onmemory :as intf]))

(defn- handle-err [ex]
  (let [data (ex-data ex)
        type (get data :type)]
    (case type
      :not-found (v/not-found)
      :illegal-argument (v/bad-request)
      (v/internal-server-error))))

(defn handle-healthy [_]
  (log/debug :handle-healthy)
  (v/ok))

(defn handle-list-todo [_]
  (log/debug "handle-list-todo")
  (-> (uc/list-all intf/list-todo)
      (v/todos->ok)))

(defn handle-new-todo [{params :params}]
  (log/debugf "handle-new-todo with params %s" params)
  (-> (->new-todo params)
      (uc/create intf/add-new-todo)
      (v/todo->created)))

(defn handle-get-todo [{params :params}]
  (log/debugf "handle-get-todo with params %s" params)
  (try

    (-> (resolve-todo-id params)
        (uc/get-one intf/get-todo)
        (v/todo->ok))

    (catch Exception ex
      (handle-err ex))))

(defn handle-edit-todo [{params :params}]
  (log/debugf "handle-edit-todo with params %s" params)
  (try (-> (->todo params)
           (uc/edit intf/get-todo intf/update-todo)
           (v/updated))
       (catch Exception ex
         (handle-err ex))))

(defn handle-delete-todo [{params :params}]
  (log/debug "handle-delete-todo with params %s" params)
  (some-> (get params :id)
          (java.util.UUID/fromString)
          (uc/delete-one intf/get-todo intf/remove-todo))
  (v/deleted))
