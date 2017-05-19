(ns timesheet.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [timesheet.layout :refer [error-page]]
            [timesheet.routes.home :refer [home-routes]]
            [timesheet.routes.admin :refer [admin-routes]]
            [compojure.route :as route]
            [timesheet.env :refer [defaults]]
            [mount.core :as mount]
            [timesheet.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (-> #'admin-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
