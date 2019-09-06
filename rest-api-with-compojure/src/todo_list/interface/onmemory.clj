(ns todo-list.interface.onmemory
  (:require
   [clojure.spec.alpha :as s]
   [clojure.tools.logging :as log]
   [java-time :as time]
   [todo-list.core]))

(def todo-repository (ref {}))

(defn get-todo
  "メモリ上に保持された TODO から、 ID が一致するものを取得する
  指定された TODO が存在しない場合は、 nil を返す"
  [id]
  (get @todo-repository id))

(defn add-new-todo
  "指定された TODO をメモリ上のリポジトリに登録する。
  id がすでに登録されている場合は、 ex-info を返却する。"
  [new-todo]
  (let [todo (assoc new-todo :created-at
                    (time/local-date-time))]
    (dosync
     (if
      (not (contains? @todo-repository (get todo :id)))
       (do (alter todo-repository assoc (get todo :id) todo)
           todo)
       (throw
        (ex-info "Not Found" {:type :entity-not-found}))))))

(defn list-todo
  "登録されている TODO を全て返却する
   結果は :created-at の降順にてそーとされている"
  []
  (->>
   (reduce-kv
    (fn [acc k v] (conj acc v))
    []
    (deref todo-repository))
   (sort-by :created-at)))

(defn find-todo
  "登録されている TODO のうち、条件に合致するものの一覧を返却する
   一致するものが見つからない場合は、 空のベクタを返却する
   
   filter :args todo, :ret bool"
  [pred]
  (vec (filter pred (list-todo))))

(defn update-todo
  "指定された TODO を登録する（存在する場合は上書きする）"
  [todo]
  (dosync
   (alter todo-repository assoc (get todo :id) todo)
   nil))

(defn remove-todo
  "指定された TODO をリポジトリから削除する。"
  [todo]
  (dosync
   (log/debugf "remove-todo id(%s)" (get todo :id))
   (alter todo-repository
          dissoc (get todo :id))
   nil))

;;; ヘルパ

#_(; リポジトリを初期化
   dosync (alter todo-repository (ref {})))

#_(; 初期データ登録
   dotimes [i 10]
    (let [todo {:id (java.util.UUID/randomUUID) :title (str "test-" i) :created-at (java-time/local-date-time)}]
      (dosync (alter todo-repository assoc (get todo :id) todo))))

#_(print @todo-repository)

#_(list-todo)

#_(find-todo
   (fn [todo]
     (->
      (get todo :title)
      (or (= "test-2")
          (= "test-10")))))
