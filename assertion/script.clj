(defn length [col] (count col))

(assert 
 (= 3 (length [1 2 3])))

(assert 
 (= 0 (length [])))

(assert 
 (= 5 (length "hello")))
