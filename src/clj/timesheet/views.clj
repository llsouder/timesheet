(ns timesheet.views
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [timesheet.dateutil :as dutil]
            [clj-time.format :as f]
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

(defn charge-select [row charges]
  [:select {:name (str "charge-row" row) :form "timesheet"}
   ;;blank
   [:option {:value "" }]
   (for [charge charges]
     [:option {:value (:id charge)} (:name charge)])])

(defn timesheet [{:keys [enddate date charges]}]
  (hc/html[:div {:style "text-align:right"}
          [:form {:method "POST", :action "/timesheet_next"}
           [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
           [:input {:type "submit", :class "btn btn-primary", :name "forward", :value "->"}]]
          [:form {:method "POST", :action "/timesheet_back"} "Period End Date:" 
           [:input {:type "text" :name "enddate" :style "displayenddate" :value enddate :disabled "disabled" }]
           [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
           [:input {:type "submit", :class "btn btn-primary", :name "backward", :value "<-"}]]]
[:div {:class "row"}
 [:div {:class "span12"}
  [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
  [:table {:class "timesheet fixed table table-bordered table-striped table-highlight"}
   [:thead
    [:tr {:style "background-color:#0d47a1;color:#ffffff"}
     [:th {:style "width: 180px;text-align: center;"} "Name"]
     ;;(add-day-header date)
     [:th {:style "width: 30px;text-align: center;"} "Total"]]]
   [:form {:method "POST", :id "timesheet", :action "/timesheet_submit"}]
   [:tbody
    "<!--row1-->"
    [:tr
     [:td
      (charge-select 1 charges)
      [:td {:style "width: 30px;text-align: center; background-color:#606060; color: #ff0000 ;"}
       [:input {:onblur "findTotal('row1')", :type "text", :name "row1-0", :size "4"}]" "]]
     [:td {:style "width: 30px;text-align: center; background-color:#FFFFFF; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-1", :size "4"}]" "]
     [:td {:style "width: 30px;text-align: center; background-color:#FFFFFF; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-2", :size "4"}]" "]
     [:td {:style "width: 30px;text-align: center; background-color:#FFFFFF; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-3", :size "4"}]" "]
     [:td {:style "width: 30px;text-align: center; background-color:#FFFFFF; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-4", :size "4"}]" "]
     [:td {:style "width: 30px;text-align: center; background-color:#FFFFFF; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-5", :size "4"}]" "]
     [:td {:style "width: 30px;text-align: center; background-color:#606060; color: #ff0000 ;"}
      [:input {:onblur "findTotal('row1')", :type "text", :name "row1-6", :size "4"}]" "]"<!--total-->"
     [:td {:style "width: 30px;text-align: center;"}
      [:input {:type "text", :id "row1", :size "4"}]" "]]"<!--end of row1-->"  "<!--row2-->"
    [:tr
     [:td {:colspan "2"}
      [:input {:type "text", :name "Signature", :value ""}]]
     [:td {:colspan "5"}]
     [:td {:colspan "2"}
      [:input {:type "submit", :class "btn btn-primary", :name "submit", :value "Submit"}]]]]]]
 [:script {:type "text/javascript"} "\nfunction findTotal(name){\n  var tot=0;\n  for (i = 0; i < 7; i++) {\n    var arr = document.getElementsByName(name + '-' + i)
  [0];\n    if(parseInt(arr.value))\n      tot += parseInt(arr.value);\n    document.getElementById(name).value = tot;\n  }\n}"]]))

(defn add-base [page {:keys [body] :as all}]
  (assoc all :body (hp/html5 (base page "" body))))

(def body1 [:div {:class "super"} "This is super."])
