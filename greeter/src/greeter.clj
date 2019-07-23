(ns greeter
  (:gen-class))

(defn greet 
  ([] (greet ()))
  ([persons] 
   (if (empty? persons) "Hello, World."
       (str "Hello, " 
            (first persons) 
            (if (= 1 (count persons)) "."
                " and others.")))))

(defn -main
  [& args]
  (println (greet args)))

