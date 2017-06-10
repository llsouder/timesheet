(ns timesheet.routes.timesheet
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [clojure.tools.logging :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [clj-time.core :as t]))

(defn first-day-of-week [date]
  "Return the first day of the week."
  (if (= 7 (.get (.dayOfWeek date)))
    date
    (.withDayOfWeek (.minusWeeks date 1) 7)))

(defn work-week [date]
  "Return seven days of the week starting on Sunday."
  (let [firstday (first-day-of-week date)]
    (map #(t/plus firstday (t/days %)) (range 0 7))))

;;(defroutes timesheet-routes
;;  (GET "/timesheet" request (admin-page request)))

