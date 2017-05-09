(ns timesheet.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [timesheet.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[timesheet started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[timesheet has shut down successfully]=-"))
   :middleware wrap-dev})
