(ns micheam.todo
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [micheam.todo.handler :refer [handle-list handle-get handle-new]]
            [clojure.stacktrace :as st])
  (:gen-class))

(comment
  "See:
   https://github.com/clojure/tools.cli#example-usage")

(def cli-options
  [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Todo manager"
        "This is a sample cli-app with clojure."
        ""
        "Usage: todo [options] action"
        ""
        "Options:"
        options-summary
        ""
        "Actions:"
        "  list                  list all todos"
        "  get id                get todo by `id`"
        "  new title             create new todo with `title`"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
   should exit (with a error message, and optional ok status), or a map
   indicating the action the program should take and the options provided.

   parse-opts returns a map with four entries:

    {:options     The options map, keyed by :id, mapped to the parsed value
     :arguments   A vector of unprocessed arguments
     :summary     A string containing a minimal options summary
     :errors      A possible vector of error message strings generated during
                  parsing; nil when no errors exist}
   "
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}

      ;; custom validation on arguments
      (and (< 0 (count arguments))
           (#{"list" "get" "new"} (first arguments)))
      {:action (first arguments)
       :arguments (rest arguments)
       :options options}

      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [action options arguments exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (try
        (case action
          "list" (handle-list)
          "get" (handle-get (first arguments))
          "new" (handle-new (first arguments)))
        (catch Exception ex
          (st/print-stack-trace ex))))))

