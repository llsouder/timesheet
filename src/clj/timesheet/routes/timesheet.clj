(ns timesheet.routes.timesheet
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [taoensso.timbre :as timbre
      :refer [log  trace  debug  info  warn  error  fatal  report
              logf tracef debugf infof warnf errorf fatalf reportf
              spy get-env]]))

(def num-of-rows 6)
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

(defn rows 
  "Returns a seq of numbers starting from 1 and ending wth rows inclusive because (rows 5) reads better than (range 1 6)."
  [rows]
  (range 1 (+ rows 1)))

(def charge-keys 
  (map #(keyword (str "charge-row" %)) (rows num-of-rows)))

(defn get-row 
  "Take the keyword and return the row number."
  [charge]
  (Integer. (str (last (name charge)))))

(defn make-cell-key 
  "Returns a keyword to match the cell on the timesheet template using the integers row and day"
  [row day]
  (keyword (str "row" row "-" day)))

(defn get-cell-keys-for 
  "Returns a vector of cell keys."
  [row]
  (map #(make-cell-key row %) (range 0 7)))

(def all-cells
  "Returns a set of all the cells on the web page.  Used to determine if an item is a cell or something else on the page"
  (into #{} (mapcat get-cell-keys-for (rows num-of-rows))))

(defn is-cell? 
  "Examine the keyword in the k v vector and determine if it is a cell on the or something else."
  [[k v]]
  (contains? all-cells k))

(defn timesheet-schema [row]
  (into {} (map (juxt identity (constantly [st/integer-str]))) (get-cell-keys-for row)))

(defn zero-empty-cells 
  [params]
  (merge params (reduce #(assoc %1 %2 "0") {} (keys (filter #(and (is-cell? %) (empty? (second %))) params)))))

(defn validate-row [params row]
  (st/validate params (timesheet-schema row )))

(defn parse-submitted-data [params]
  ;;look through the keys
 (doseq [charge charge-keys]
    ;;if there is a charge code
   (if (not-empty (charge params))
      ;;collect each cells data for that row
      (let [row (get-row  charge)]
        (let [params (zero-empty-cells params)]
          (let [result (validate params row)]
            (if (first result)
              (error "no good report:" (first result))
              (info "add to database:" (second result)))
            result))))))

(defn timesheet-page-for [{:keys [flash]}]
  (info ":date = "(:date flash))
  (let[stuff (layout/render
              "timesheet.html"
              {:enddate (formatted-end-date (:date flash)) 
              :dates (map #(f/unparse MM-dd-yyyy-formatter %) (work-week (:date flash)))
              :days (work-week-header (:date flash)) 
              :rows (rows num-of-rows)
              :charges (db/get-all-charges)})]
    (view/add-base "timesheet" stuff )))

(defn submit-time [{:keys [params]}]
  (let [date (f/parse MM-dd-yyyy-formatter (:enddate params))
        datamap (parse-submitted-data params)]
    (info "datamap:" datamap)
    (if (first datamap)
      (error "data was bad:" (first datamap))
      (timesheet-page-for {:flash (assoc params :date date)}))))

(defn timesheet-page [{:keys [params]}]
  (timesheet-page-for {:flash (assoc params :date (t/now))}))
   
(defn timesheet-page-next [{:keys [params]}]
  (let [date (t/plus (f/parse MM-dd-yyyy-formatter (:enddate params)) (t/days 7))]
    (timesheet-page-for {:flash (assoc params :date date)})))

(defn timesheet-page-back [{:keys [params]}]
  (let [date (t/minus (f/parse MM-dd-yyyy-formatter (:enddate params)) (t/days 7))]
    (timesheet-page-for {:flash (assoc params :date date)})))

(defroutes timesheet-routes
  (POST "/timesheet_submit" request (submit-time request))
  (POST "/timesheet_next" request (timesheet-page-next request))
  (POST "/timesheet_back" request (timesheet-page-back request))
  (GET "/timesheet" request (timesheet-page request)))
