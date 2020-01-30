(ns micheam.todo.specs
  (:require [clojure.spec.alpha :as s]))

(defprotocol TodoRepository
  (_create! [this todo])
  (_edit! [this todo])
  (_list-all [this])
  (_get-by-id [this id]))

(s/def :todo/id uuid?)
(s/def :todo/title (s/and string? #(> (count %) 0 )))
(s/def :todo/created-at #(instance? java.time.Instant %))

(s/def ::todo-repository? #(satisfies? TodoRepository %))

(s/def ::found-todo
  (s/keys :req [:todo/id :todo/title :todo/created-at])) 

(s/def ::editable-fields
  (s/keys :req [:todo/id :todo/title]))

(s/fdef create!
  :args (s/cat :this #(satisfies? TodoRepository %)
               :todo (s/keys :req [:todo/title]))
  :ret (s/nilable :todo/id))

(s/fdef edit!
  :args (s/cat :this #(satisfies? TodoRepository %)
               :todo ::editable-fields)
  :ret nil?)

(s/fdef list-all
  :args (s/cat :this #(satisfies? TodoRepository %))
  :ret (s/coll-of ::found-todo))

(s/fdef get-by-id
  :args (s/cat :this #(satisfies? TodoRepository %)
               :id :todo/id)
  :ret (s/nilable ::found-todo))

(defn create! [this todo] (_create! this todo))
(defn edit! [this todo] (_edit! this todo))
(defn list-all [this] (_list-all this))
(defn get-by-id [this id] (_get-by-id this id))
