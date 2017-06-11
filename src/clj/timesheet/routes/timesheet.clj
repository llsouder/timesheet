(ns timesheet.routes.timesheet
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [clojure.tools.logging :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [clj-time.format :as f]
            [clj-time.core :as t]))

(def formatter (f/formatter "MM-dd"))

(def day-names '("days are not 0 indexed so 0 is this :)" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday" "Sunday"))

(defn day-name [date]
  "Return the name of the day."
  (nth day-names (.get (.dayOfWeek date))))

(defn first-day-of-week [date]
  "Return the first day of the week."
  (if (= 7 (.get (.dayOfWeek date)))
    date
    (.withDayOfWeek (.minusWeeks date 1) 7)))

(defn work-week [date]
  "Return seven days of the week starting on Sunday."
  (let [firstday (first-day-of-week date)]
    (map #(t/plus firstday (t/days %)) (range 0 7))))

(defn formatted-work-week [date]
  "Return seven days starting on Sunday. Format is name, <br>,  MM-dd."
  (map #(str (day-name %) "<br>" (f/unparse formatter %)) (work-week date)))

(defn timesheet-page [{:keys [flash]}]
  (layout/render
   "timesheet.html"
   {:days (formatted-work-week (t/now)) }))
   

(defroutes timesheet-routes
  (GET "/timesheet" request (timesheet-page request)))

