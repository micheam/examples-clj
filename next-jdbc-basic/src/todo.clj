(ns todo
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]
            [java-time :as t]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as result-set]))
;;; specs
(s/def ::local-date-time
  (s/with-gen t/local-date-time?
    #(g/fmap (fn [^java.lang.Long ms] (t/local-date-time (t/instant ms) (t/zone-id)))
             (s/gen (s/int-in (t/to-millis-from-epoch (t/instant (t/zoned-date-time 2019 1 1)))
                              (t/to-millis-from-epoch (t/instant (t/zoned-date-time 2019 12 31))))))))

(s/def ::id uuid?)
(s/def ::title (s/and string? #(>= 256 (count %))))
(s/def ::done boolean?)
(s/def ::created-at ::local-date-time)
(s/def ::todo (s/keys :req-un [::id ::title ::done]
                      :opt-un [::created-at]))

(defn random-todo [] (g/generate (s/gen ::todo)))

;;; main
(def db-spec {:dbtype "pgsql"
              :dbname "next-jdbc-basic"
              :user "postgres"})

(def ds (next.jdbc/get-connection db-spec))

(defn init-schema []
  (jdbc/with-transaction [tx ds]
    (jdbc/execute!
     tx ["DROP TABLE IF EXISTS todo;"])
    (jdbc/execute!
     tx ["CREATE TABLE todo (
            id UUID PRIMARY KEY,
            title VARCHAR(256) NOT NULL,
            done BOOLEAN NOT NULL DEFAULT false,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
          );"])))

(def snake-to-kebab {:column-fn #(str/replace % #"-" "_")
                     :builder-fn result-set/as-modified-maps
                     :label-fn #(str/replace % #"_" "-")
                     :qualifier-fn #(str/replace % #"_" "-")})

(defn insert-init-data [n]
  (jdbc/with-transaction [tx ds]
    (dotimes [_ n]
      (sql/insert! tx :todo (random-todo) snake-to-kebab))))

(defn create-todo [title]
  (let [todo {:id (g/generate (s/gen ::id)) :title title :done false}]
    (sql/insert! ds :todo todo snake-to-kebab)))

(defn done-todo [id]
  (jdbc/with-transaction [tx ds]
    (let [found (sql/get-by-id tx :todo (java.util.UUID/fromString id))]
      (if-not (nil? found)
        (sql/update! tx :todo {:done true} {:id (:todo/id found)})
        (throw (ex-info "Not Found" {:type :not-found}))))))

(defn list-todo []
  (sql/query ds ["select * from todo;"] snake-to-kebab))
