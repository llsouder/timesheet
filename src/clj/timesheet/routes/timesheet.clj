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

(def MM-dd-formatter (f/formatter "MM-dd"))
(def MM-dd-yyyy-formatter (f/formatter "MM-dd-yyyy"))

(def day-names '("days are not 0 indexed so 0 is this :)" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday" "Sunday"))

(defn day-name [date]
  "Return the name of the day."
  (nth day-names (.get (.dayOfWeek date))))

(defn first-day-of-week [date]
  "Return the first day of the week."
  (if (= 7 (.get (.dayOfWeek date)))
    date
    (.withDayOfWeek (.minusWeeks date 1) 7)))

(defn last-day-of-week [date]
  "Return the last day of the week."
    (t/plus (first-day-of-week date) (t/days 6)))

(defn formatted-end-date [date]
  (f/unparse MM-dd-yyyy-formatter (last-day-of-week (t/now))))

(defn work-week [date]
  "Return seven days of the week starting on Sunday."
  (let [firstday (first-day-of-week date)]
    (map #(t/plus firstday (t/days %)) (range 0 7))))

(defn formatted-work-week [date]
  "Return seven days starting on Sunday. Format is name, <br>,  MM-dd."
  (map #(str (day-name %) "<br>" (f/unparse MM-dd-formatter %)) (work-week date)))

(defn timesheet-page [{:keys [flash]}]
  (let [date (t/now)]
  (layout/render
   "timesheet.html"
   {:enddate (formatted-end-date date) :days (formatted-work-week date) })))
   

(defroutes timesheet-routes
  (GET "/timesheet" request (timesheet-page request)))

