(ns todo-list.web.route
  (:require
   [compojure.core :refer :all]
   [compojure.route :refer :all]
   [todo-list.web.handler :refer [handle-new-todo,
                                  handle-list-todo,
                                  handle-get-todo
                                  handle-edit-todo
                                  handle-delete-todo
                                  handle-healthy]]))

(defroutes todo-list-routes
  (GET "/healthy" _ handle-healthy)
  (context "/todo" _
    (GET "/" _ handle-list-todo)
    (POST "/" _ handle-new-todo)
    (context "/:id" _
      (GET "/" _  handle-get-todo)
      (PATCH "/" _  handle-edit-todo)
      (DELETE "/" _ handle-delete-todo)))
  (not-found {}))
