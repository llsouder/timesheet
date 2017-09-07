(ns timesheet.dateutil
  (:require [clj-time.format :as f]
            [clj-time.core :as t]))

(def MM-dd-formatter (f/formatter "MM-dd"))
(def MM-dd-yyyy-formatter (f/formatter "MM-dd-yyyy"))
(def yyyy-MM-dd-formatter (f/formatter "yyyy-MM-dd"))

(def day-names '("days are not 0 indexed so 0 is this :)" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday" "Sunday"))

(defn format-for-sql [date-string]
  (f/unparse yyyy-MM-dd-formatter (f/parse MM-dd-yyyy-formatter date-string)))

(defn day-name [date]
  "Return the name of the day."
  (nth day-names (.get (.dayOfWeek date))))

(defn first-day-of-week [date]
  "Return the first day of the week."
  (if (= 7 (.get (.dayOfWeek date)))
    date
    (.withDayOfWeek (.minusWeeks date 1) 7)))

(defn last-day-of-week
  "Return the last day of the week."
  [date]
  (t/plus (first-day-of-week date) (t/days 6)))

(defn formatted-end-date [date]
  (f/unparse MM-dd-yyyy-formatter (last-day-of-week date)))

(defn work-week
  "Return seven days of the week starting on Sunday."
  [date]
  (let [firstday (first-day-of-week date)]
    (map #(t/plus firstday (t/days %)) (range 0 7))))

(defn work-week-header
  "Return seven days starting on Sunday. Format is name, <br>,  MM-dd."
  [date]
  (map #(str (day-name %) "<br>" (f/unparse MM-dd-formatter %)) (work-week date)))
