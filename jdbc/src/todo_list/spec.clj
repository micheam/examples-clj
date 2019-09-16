(ns todo-list.spec
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as g]))

(s/def :todo/id uuid?)
(s/def :todo/title (s/and string? #(<= (count %) 256)))
(s/def :todo/done boolean?)
(s/def ::todo (s/keys :req [:todo/id :todo/title :todo/done]))

(s/def :tag/id uuid?)
(s/def :tag/name (s/and string? #(<= (count %) 128)))
(s/def ::tag (s/keys :req [:tag/id :tag/name]))
