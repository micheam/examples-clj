FROM gcr.io/google-appengine/openjdk

RUN  mkdir /app
COPY target/app.jar /app/

CMD  ["java","-cp","/app/app.jar","clojure.main","-m","todo-list.web.main"]
