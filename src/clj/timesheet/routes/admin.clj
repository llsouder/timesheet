(ns timesheet.routes.admin
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [clojure.tools.logging :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]))

(defn is-not-used 
  "Returns true if the employee number in not already a key in the employee table."
  [employee_number]
  (< (:rows (into {} (db/check-employee-number {:employee_number employee_number}))) 1 ))

(def employee-schema
  [[:employee_number
    st/required
    st/string
    {:message "Employee number is already in use."
     :validate #(is-not-used %)}]
  ;;[:email
  ;;st/required
  ;;st/email]
  [:first_name
    st/required
    st/string]
  [:last_name
    st/required
    st/string]
  [:dob
    st/required
    st/string
   {:message "DOB must be in the format yyyy-mm-dd"
     :validate #(> (count %) 9)}]
  ;;[:street
  ;;  st/required
  ;;  st/string]
  ;;[:city
  ;;  st/required
  ;;  st/string]
  ;;[:state
  ;;  st/required
  ;;  st/string]
  ;;[:zip
  ;;  st/required
  ;;  st/string]])
   ])

(defn validate-employee [params]
  (first (st/validate params employee-schema)))

(defn create-employee! [{:keys [params]}]
  (if-let [errors (validate-employee params)]
    (-> (response/found "/admin")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-employee!
       (assoc params :last_login (java.util.Date.)))
      (response/found "/admin"))))

(defn admin-page [{:keys [flash]}]
  (let [stuff (layout/render-hiccup view/employee "admin"
   (merge {:employees (db/get-all-employees)}
          (select-keys flash [:employee_number :first_name :last_name :dob :errors])))]
  (view/add-base "admin" stuff)))

(defroutes admin-routes
  (GET "/admin" request (admin-page request))
  (POST "/admin" request (create-employee! request)))

