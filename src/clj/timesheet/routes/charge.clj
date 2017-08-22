(ns timesheet.routes.charge
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes DELETE GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [hiccup.core :as hc]
            [taoensso.timbre :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]))

(def charge-schema
  [[:name
    st/required
    st/string]])

(defn validate-charge [params]
  (first (st/validate params charge-schema)))

(defn create-charge! [{:keys [params]}]
  (if-let [errors (validate-charge params)]
    (-> (response/found "/charge")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-charge! params)
      (response/found "/charge"))))


(defn charge-page [{:keys [flash]}]
  (let [stuff (layout/render
        "charge.html"
        (merge {:charges (db/get-all-charges)}
               (select-keys flash [:employee_number :first_name :last_name :dob :errors])))]
    (view/add-base "charge" stuff)))

(defn delete-charge! [{:keys [params]}]
  (do
    (db/delete-charge! params)
    (response/found "/charge")))

(defroutes charge-routes
  (GET "/charge" request (charge-page request))
  (POST "/charge_delete" request (delete-charge! request))
  (POST "/charge" request (create-charge! request)))
