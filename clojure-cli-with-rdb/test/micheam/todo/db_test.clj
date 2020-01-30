(ns micheam.todo.db-test
  (:require [clojure.test :as t]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as sgen]
            [micheam.todo.db :as sut]
            [micheam.todo.specs :as specs]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs])
  (:import [org.h2.api TimestampWithTimeZone]
           [java.time ZonedDateTime Instant]
           [java.time.format DateTimeFormatter]))

(stest/instrument)

(def test-tx-opt
  {:isolation     :read-committed
   :readonly      false
   :rollback-only true})

(def test-db-spec
  {:dbtype "h2:mem" :dbname "todo"})

(sut/create-table! test-db-spec)

;;; helper

(defn clear-table! []
  (sut/truncate-table! test-db-spec))

(def date-time-formatter
  (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss.SSSX"))

(extend-protocol rs/ReadableColumn
  TimestampWithTimeZone
  (read-column-by-label ^java.time.Instant [^org.h2.api.TimestampWithTimeZone v _]
    (.toInstant (ZonedDateTime/parse (.toString v) date-time-formatter)))
  (read-column-by-index ^java.time.Instant [^org.h2.api.TimestampWithTimeZone v _2 _3]
    (.toInstant (ZonedDateTime/parse (.toString v) date-time-formatter))))

(defmacro testing-then-clear
  [n & body]
  `(t/testing ~n
     (try 
       (clear-table!)
       ~@body
       (finally (clear-table!)))))

;;; test casese

(t/deftest SQLTodoRepository
  (let [repo (sut/->SQLTodoRepository test-db-spec)]
    (binding [sut/*transaction-opt* test-tx-opt] 
      (t/testing "create todo then return #uuid id"
        (t/is (uuid? (specs/create! repo {:todo/title "This is title for todo"})))))))

(t/deftest SQLTodoRepository_edit!
  (let [repo (sut/->SQLTodoRepository test-db-spec)]

    (testing-then-clear 
     "edit existing todo" 

     (let [id (specs/create! repo {:todo/title "change me!"})
           actual (specs/edit! repo {:todo/id id, :todo/title "changed!"})]
       (t/is (nil? actual))))

    (testing-then-clear
     "edit no-existing todo"

     (t/is (thrown-with-msg? clojure.lang.ExceptionInfo
                             #"No Todo Found"
                             (specs/edit! repo {:todo/id (java.util.UUID/randomUUID),
                                               :todo/title "changed!"}))))
    ))

(t/deftest SQLTodoRepository2
  (let [repo (sut/->SQLTodoRepository test-db-spec)]

    (testing-then-clear 
     "list all todo"

     (dotimes [n 5] (specs/create! repo {:todo/title "test"}))
     (let [result (specs/list-all repo)] 
       (t/is (= 5 (count result)))
       (t/is (s/valid? (s/coll-of :micheam.todo.specs/found-todo) result))))))

(t/deftest get-by-id
  (let [repo (sut/->SQLTodoRepository test-db-spec)]

    (testing-then-clear 
     "get by existing todo/id" 

     (let [title "write test cases"
           id (specs/create! repo {:todo/title title})
           actual (specs/get-by-id repo id)]
       (t/is (not (nil? actual)))
       (t/is (s/valid? :micheam.todo.specs/found-todo actual)
             (s/explain-data :micheam.todo.specs/found-todo actual))))

    (testing-then-clear 
     "get by no-existing todo/id" 

     (let [actual (specs/get-by-id repo (java.util.UUID/randomUUID))]
       (t/is (nil? actual))))

    ))
