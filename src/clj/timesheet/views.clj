(ns timesheet.views
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]))


(def base [:head 
    (hp/include-css "/assets/bootstrap/css/bootstrap.min.css")
    (hp/include-css "/assets/font-awesome/css/font-awesome.min.css")
    (hp/include-css "/css/screen.css")
])

(def body1 [:div {:class "super"} "This is super."])

(hp/html5 base body1)

