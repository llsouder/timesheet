(ns timesheet.routes.timesheet
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [timesheet.dateutil :as dutil]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [taoensso.timbre :as timbre
             :refer [log  trace  debug  info  warn  error  fatal  report
                     logf tracef debugf infof warnf errorf fatalf reportf
                     spy get-env]]))

(def num-of-rows 6)

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
  (st/validate params (timesheet-schema row)))

(defn validate-data [charge params]
    ;;if there is a charge code
  (if (not-empty (charge params))
      ;;collect each cells data for that row
    (let [row (get-row  charge)
          params (zero-empty-cells params)
          result (validate-row params row)]
      (if (first result)
        {:errors  (first result)}
        {:data  (second result)}))))

(defn parse-submitted-data [params]
  (let [results (keep #(validate-data %1 params) charge-keys)]
    {:errors (into {} (map :errors results))
     :data (into {} (map :data results))}))

(defn timesheet-page-for [{:keys [flash]}]
  (info ":date = " (:date flash))
  (let [stuff (layout/render
               "timesheet.html"
               {:enddate (dutil/formatted-end-date (:date flash))
                :dates (map #(f/unparse dutil/MM-dd-yyyy-formatter %) (dutil/work-week (:date flash)))
                :days (dutil/work-week-header (:date flash))
                :rows (rows num-of-rows)
                :charges (db/get-all-charges)})]
    (view/add-base "timesheet" stuff)))

(defn submit-time [{:keys [params]}]
  (let [date (f/parse dutil/MM-dd-yyyy-formatter (:enddate params))
        datamap (parse-submitted-data params)]
    (info "datamap:" datamap)
    (if (first datamap)
      (error "data was bad:" (first datamap))
      (timesheet-page-for {:flash (assoc params :date date)}))))

(defn timesheet-page [{:keys [params]}]
  (timesheet-page-for {:flash (assoc params :date (t/now))}))

(defn test-timesheet-page [{:keys [flash]}]
  (let [date (t/now)]
    (view/timesheet{:date date 
                    :enddate (dutil/formatted-end-date date)
                    :dates (map #(f/unparse dutil/MM-dd-yyyy-formatter %) (dutil/work-week date))
                    :days (dutil/work-week-header date)
                    :rows (rows num-of-rows)
                    :charges (db/get-all-charges)})))

(defn timesheet-page-next [{:keys [params]}]
  (let [date (t/plus (f/parse dutil/MM-dd-yyyy-formatter (:enddate params)) (t/days 7))]
    (timesheet-page-for {:flash (assoc params :date date)})))

(defn timesheet-page-back [{:keys [params]}]
  (let [date (t/minus (f/parse dutil/MM-dd-yyyy-formatter (:enddate params)) (t/days 7))]
    (timesheet-page-for {:flash (assoc params :date date)})))

(defroutes timesheet-routes
  (POST "/timesheet_submit" request (submit-time request))
  (POST "/timesheet_next" request (timesheet-page-next request))
  (POST "/timesheet_back" request (timesheet-page-back request))
  (GET "/test" request (test-timesheet-page request))
  (GET "/timesheet" request (timesheet-page request)))
