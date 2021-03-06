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
  (view/add-base "home" (layout/ready-for-html "")))

(defn about-page []
  (let[stuff (layout/render-hiccup view/about "about" {})]
    (view/add-base "about" stuff)))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/home" [] (home-page))
  (GET "/about" [] (about-page)))

