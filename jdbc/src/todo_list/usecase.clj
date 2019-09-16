(ns todo-list.usecase
  (:require [next.jdbc :refer [with-transaction]]
            [todo-list.spec]
            [todo-list.db :refer [ds]]
            [todo-list.db.todo :as todo]
            [todo-list.db.tag :as tag]
            [todo-list.db.todo-tag :as todo-tag]))

(defn ex-not-found
  ([msg]
   (ex-info msg {:type :todo-list/not-found}))
  ([fmt & args]
   (ex-not-found (apply format fmt args))))

(defn ->todo-id
  ([]
   (java.util.UUID/randomUUID))
  ([arg]
   (if (uuid? arg) arg (java.util.UUID/fromString arg))))

(defn get-todo
  [id]
  (with-transaction [tx ds]
    (let [todo-id (->todo-id id)]
      (if (todo/exist? tx todo-id)
        (-> (todo/get-one tx todo-id)
            (assoc :tags (todo-tag/list-all-tag-by-todo tx todo-id)))
        (throw (ex-not-found "TODO(%s)は存在しません" todo-id))))))

(defn create-todo
  ([title]
   (create-todo title []))
  ([title tag-names]
   (with-transaction [tx ds]
     (let [todo-id (->todo-id)
           todo {:todo/id todo-id, :todo/title title, :todo/done false}]
       (todo/create! tx todo)
       (doseq [tag-name tag-names]
         (let [tag (tag/get-one-by-name tx tag-name)]
           (if-not (nil? tag)
             (todo-tag/create! tx todo-id (:tag/id tag))
             (throw (ex-not-found "TAG(%s)は存在しません" tag-name)))))
       (get-todo todo-id)))))

(defn remove-todo
  [id]
  (with-transaction [tx ds]
    (let [todo-id (java.util.UUID/fromString id)]
      (if (todo/exist? tx todo-id)
        (do (todo-tag/delete-by-todo! tx todo-id)
            (todo/delete! tx todo-id))
        (throw (ex-not-found "TODO(%s)は存在しません" id))))))

(defn list-all-todo
  [] (todo/list-all ds))

(defn list-undone-todo
  [] (filter #(= (:todo/done %) false) (todo/list-all ds)))

(defn list-done-todo
  [] (filter #(= (:todo/done %) true) (todo/list-all ds)))

(defn create-tag
  [n]
  (tag/create! ds {:tag/id (->todo-id), :tag/name n}))

(defn append-tag
  [todo-id tag-name]
  (with-transaction [tx ds]
    (let [todo (todo/get-one tx (->todo-id todo-id))
          tag (tag/get-one-by-name tx tag-name)]
      (cond
        (nil? todo) (throw (ex-not-found "TODO(%s)は存在しません" todo-id))
        (nil? tag) (throw (ex-not-found "TAG(%s)は存在しません" tag-name))
        :else (todo-tag/create! tx (:todo/id todo) (:tag/id tag))))))

(defn list-all-tags
  []
  (tag/list-all ds))
