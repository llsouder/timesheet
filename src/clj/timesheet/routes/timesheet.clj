(ns timesheet.routes.timesheet
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [timesheet.dateutil :as dutil]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]
            [clojure.string :as str]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [taoensso.timbre :as log]))

(def num-of-rows 6)

(defn rows
  "Returns a seq of numbers starting from 1 and ending wth rows inclusive because (rows 5) reads better than (range 1 6)."
  [rows]
  (range rows))

(def charge-keys
  (map #(keyword (str "charge-row" %)) (rows num-of-rows)))

(defn get-row
  "Take the keyword and return the row number."
  [charge]
  (Integer. (str/replace (name charge) #"charge-row" "")))

(defn make-cell-key
  "Returns a keyword to match the cell on the timesheet template using the integers row and day"
  [row day]
  (keyword (str "row" row "-" day)))

(defn get-cell-keys-for
  "Returns a vector of cell keys."
  [row]
  (into [] (map #(make-cell-key row %) (range 0 7))))

(def all-cells
  "Returns a set of all the cells on the web page.  Used to determine if an item is a cell or something else on the page"
  (into #{} (mapcat get-cell-keys-for (rows num-of-rows))))

(defn is-cell?
  "Examine the keyword in the k v vector and determine if it is a cell on the or something else."
  [[k v]]
  (contains? all-cells k))

(defn timesheet-schema
  [row]
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

(def days [:sun :mon :tue :wed :thu :fri :sat])

(defn sql-cell-key
  "Convert main form cell key to day of the week key."
  [cell]
  ;;based on the column number in the key
  (nth days (Integer. (str (last (name cell))))))

(defn make-sql-row-args
  "pass in just the data part please."
  [data]
  (remove nil?
          (for [charge charge-keys]
            ;;if there is a charge code
            (if (not-empty (charge data))
              ;;collect each cells data for that row
              (reduce #(assoc %1 (sql-cell-key %2) (%2 data))
                      {:charge-id (charge data)}
                      (get-cell-keys-for (get-row charge)))))))

(defn create-hours! [{:keys [data]}]
  (let [rowdata (make-sql-row-args data)]
    ;;(db/create-hours! rowdata)
    (response/found "/timesheet")))

(defn parse-submitted-data [params]
  (let [results (keep #(validate-data %1 params) charge-keys)]
    {:errors (into {} (map :errors results))
     :data (into {} (map :data results))}))

(defn timesheet-page-for [{:keys [flash]}]
  (let [stuff (layout/render-hiccup view/timesheet "timesheet"
                                    {:date (:date flash)
                                     :enddate (dutil/formatted-end-date (:date flash))
                                     :dates (map #(f/unparse dutil/MM-dd-yyyy-formatter %) (dutil/work-week (:date flash)))
                                     :days (dutil/work-week-header (:date flash))
                                     :rows (rows num-of-rows)
                                     :charges (db/get-all-charges)})]
    (view/add-base "timesheet" stuff)))

(defn submit-time [{:keys [params]}]
  (let [date (f/parse dutil/MM-dd-yyyy-formatter (:enddate params))
        datamap (parse-submitted-data params)]
    ;;datamap looks like:
    ;;{:errors {} :data {}}
    (if (empty? (:errors datamap))
      (do
        (log/info "add data to DB:" (:data datamap))
        (-> (response/found "/timesheet")
            (assoc :flash (assoc params :date date ))))
      (do
        (log/error "data was bad:" (:errors datamap))
        (-> (response/found "/timesheet")
            (assoc :flash (assoc params :date date :errors "errors")))))))

(defn timesheet-page [{:keys [flash] :as request }]
  (log/info "*************")
  (if  (nil? flash)
    (timesheet-page-for {:flash (assoc {} :date (t/now)) })
    (timesheet-page-for {:flash flash})))

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
  (GET "/timesheet" request (timesheet-page request)))
