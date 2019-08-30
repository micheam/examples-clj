(ns todo-list.usecase.todo-test
  (:require
   [todo-list.usecase.todo :as sut]
   [clojure.test :refer :all]
   [java-time :as time]))

(defn random-todo-id []
  (java.util.UUID/randomUUID))

(defn random-todo
  ([id] {:id id, :title "test", :desc "this is test todo"})
  ([] (random-todo (random-todo-id))))

(deftest list-all

  (testing "return all existing todos"
    (let [todos (reduce
                 (fn [cur _] (conj cur (random-todo))) [] (range 1 10))
          f (fn [] todos)]
      (is (= (sut/list-all f) todos)))))

(deftest get-one

  (testing "return todo if found"
    (let [id (random-todo-id)
          f (fn [id] (random-todo id))
          actual (sut/get-one id f)]
      (is (= id (get actual :id)))))

  (testing "raise exception with :type :not-found"
    (let [id (random-todo-id)
          f (fn [_] nil)]
      (is (thrown? clojure.lang.ExceptionInfo
                   (sut/get-one id f))))))

(deftest create

  (testing "return created todo on success"
    (let [f (fn [t] (assoc t :createdAt (time/local-date-time)))
          todo {:title "aaaaa", :desc "bbbbb"}
          actual (sut/create todo f)]
      (is (= (get todo :title) (get actual :title)))
      (is (= (get todo :desc) (get actual :desc)))
      (is (contains? actual :createdAt)))))
