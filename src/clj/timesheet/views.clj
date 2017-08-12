(ns timesheet.views
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [taoensso.timbre :as timbre
             :refer [log  trace  debug  info  warn  error  fatal  report
                     logf tracef debugf infof warnf errorf fatalf reportf
                     spy get-env]]
            [hiccup.page :as hp]))


(defn active [page page-route]
  (if (= page page-route)
    "active"
    ""))

(defn page-link 
  "the page is the current page and the page-route is the route."
  [page page-route servlet-context]
  [:li {:class "nav-item"}
   [:a {:href (str servlet-context "/" page-route)
        :class (str "nav-link " (active page page-route)) }
    page-route]])

(defn base [page servlet-context html-page] 
  [:html
   [:head 
   (hp/include-css "/assets/bootstrap/css/bootstrap.min.css")
   (hp/include-css "/assets/font-awesome/css/font-awesome.min.css")
   (hp/include-css "/css/screen.css")]
   [:body [:nav {:class "navbar navbar-dark bg-primary" :role "navigation"}
           [:button {:class "navbar-toggler hidden-sm-up"
                     :type "button"
                     :data-toggle "collapse"
                     :data-target "#collapsing-navbar"}
            "&#9776;" ]
           [:div {:class "collapse navbar-toggleable-xs" :id "collapsing-navbar"}
            [:a {:class "navbar-brand" :href (str servlet-context "/")} "timesheet"]
            [:ul {:class "nav navbar-nav"}
             (page-link page "home" servlet-context)
             (page-link page "admin" servlet-context)
             (page-link page "charge" servlet-context)
             (page-link page "timesheet" servlet-context)
             (page-link page "about" servlet-context)]]]
    [:div {:class "container"}
          html-page]]])


(defn add-base [page {:keys [body] :as all}]
  (assoc all :body (hp/html5 (base page "" body))))

(def body1 [:div {:class "super"} "This is super."])
