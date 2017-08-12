(ns timesheet.routes.home
  (:require [timesheet.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [timesheet.db.core :as db]
            [timesheet.views :as view]
            [hiccup.core :as hc]
            [clojure.tools.logging :as log]
            [ring.util.response :refer [redirect]]
            [struct.core :as st]))

(defn home-page []
  ;;(layout/render "home.html"))
  (hc/html (view/base "home" "" "")))

(defn about-page []
  (let[stuff (layout/render "about.html")]
    (view/add-base "about" stuff)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/home" [] (home-page))
  (GET "/about" [] (about-page)))

