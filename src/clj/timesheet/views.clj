(ns timesheet.views
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]))

(defn active [page arg]
  (if (= page arg)
    "active"
    ""))

(defn base [{:keys [:page :servlet-context]}] 
  [:head 
    (hp/include-css "/assets/bootstrap/css/bootstrap.min.css")
    (hp/include-css "/assets/font-awesome/css/font-awesome.min.css")
    (hp/include-css "/css/screen.css")
    [:body [:nav {:class "navbar navbar-dark bg-primary" :role "navigation"}
           [:button {:class "navbar-toggler hidden-sm-up"
                  :type "button"
                  :data-toggle "collapse"
                  :data-target "#collapsing-navbar"}
              "&#9776;" ]
            [:div {:class "collapse navbar-toggleable-xs" :id "collapsing-navbar"}
              [:a {:class "navbar-brand" :href "{{servlet-context}}/"} "timesheet"]
              [:ul {:class="nav navbar-nav"}
                  [:li {:class="nav-item"}
                      [:a {:href servlet-context :class (str "nav-link " (active page "home.html")) }
                      "home"]]]]]]])



(def body1 [:div {:class "super"} "This is super."])

(hp/html5 base body1)

