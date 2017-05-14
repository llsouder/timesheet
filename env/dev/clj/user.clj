(ns user
  (:require [mount.core :as mount]
            timesheet.core))

(defn start []
  (mount/start-without #'timesheet.core/repl-server))

(defn stop []
  (mount/stop-except #'timesheet.core/repl-server))

(defn restart []
  (stop)
  (start))


