(ns greeter-test
  (:require [clojure.test :as t]
            [greeter :as sut]))

(t/deftest test-for-greet
  (t/testing "return greeting message"
    (t/is (= "Hello, World." (sut/greet)))
    (t/is (= "Hello, World." (sut/greet ())))
    (t/is (= "Hello, Naoto." (sut/greet '("Naoto"))))
    (t/is (= "Hello, Naoto and others." (sut/greet '("Naoto" "Michito"))))))

