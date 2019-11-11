FROM gcr.io/google_appengine/openjdk

COPY app.jar /bin/ 

CMD ["java","-cp","/bin/app.jar","clojure.main","-m","greeter"]
