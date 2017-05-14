(ns timesheet.routes.home
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [clojure.tools.logging :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]))

(def employee-schema
  [[:employee_number
    st/required
    st/string]
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
   {:message "DOB must be in the format yyyy/mm/dd"
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
    (-> (response/found "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-employee!
       (assoc params :last_login (java.util.Date.)))
      (response/found "/"))))

(defn home-page [{:keys [flash]}]
  (log/info (db/get-all-employees))
  (layout/render
   "home.html"
   (merge {:employees (db/get-all-employees)}
          (select-keys flash [:employee_number :first_name :last_name :dob :errors]))))

(defn about-page []
  (layout/render "about.html"))

(defroutes home-routes
  (GET "/" request (home-page request))
  (POST "/" request (create-employee! request))
  (GET "/about" [] (about-page)))

