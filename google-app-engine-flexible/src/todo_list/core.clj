(ns todo-list.core
  (:require
   [clojure.spec.alpha :as s]
   [java-time :as time]))

(s/def ::id uuid?)

(s/def ::title string?)
(s/def ::desc string?)
(s/def ::created-at time/local-date-time?)

(s/def ::todo
  (s/keys :req-un [::id]
          :opt-un [::title ::desc ::created-at]))

(defn ->new-todo [params]
  {:title (get params :title)
   :description (get params :description)
   :done (or (true? (get params :done)) false)})

(defn ->todo [params]
  (let [id (java.util.UUID/fromString (get params :id))]
    (-> (select-keys params [:title :description :done])
        (assoc :id id))))

(defn new-todo-id []
  (java.util.UUID/randomUUID))

(defn resolve-todo-id [params]
  (try
    (-> (get params :id)
        (java.util.UUID/fromString))
    (catch Exception ex
      (throw (ex-info "Failed to resolve todo-id"
                      {:type :not-found} ex)))))
