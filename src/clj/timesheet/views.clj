(ns timesheet.views
  (:require [hiccup.core :as hc]
            [hiccup.page :as hp]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [timesheet.dateutil :as dutil]
            [taoensso.timbre :as log]
            [clj-time.format :as f]
            [clj-time.core :as t]
            [hiccup.page :as hp]))

(defn about
  [{:keys [servlet-context]}]
  [:img {:src (str servlet-context "/img/warning_clojure.png")}])

(defn list-employees
  [employees]
  (for [employee employees]
    [:tr
     [:td (:employee_number employee)]
     [:td (:first_name employee)]
     [:td (:last_name employee)]
     [:td (:dob employee)]]))

(defn employee
  [{:keys [errors employee_number employees employee_number first_name last_name dob]}]
  [:div {:class "row"}
   [:div {:class "row"}
    [:table {:class "employess table table-borderd table-striped table-highlight"}
     [:thead
      [:th "Employee Number"]
      [:th "First Name"]
      [:th "Last Name"]
      [:th "DOB"]]
     [:tbody
      (list-employees employees)
      [:tr
       [:form {:method "POST" :action "/admin"}
        (anti-forgery-field)
        [:td "Employee Number:"
         [:input {:class "form-control"
                  :type "text"
                  :name "employee_number"
                  :value employee_number}]
         (if (contains? errors :employee_number)
           [:div {:class "alert alert-danger"} (:employee_number errors)])]
        [:td "First Name:"
         [:input {:class "form-control"
                  :type "text"
                  :name "first_name"
                  :value first_name}]
         (if (contains? errors :first_name)
           [:div {:class "alert alert-danger"} (:first_name errors)])]
        [:td "Last Name:"
         [:input {:class "form-control"
                  :type "text"
                  :name "last_name"
                  :value last_name}]
         (if (contains? errors :last_name)
           [:div {:class "alert alert-danger"} (:last_name errors)])]
        [:td "DOB:"
         [:input {:class "form-control"
                  :type "text"
                  :name "dob"
                  :value dob}]
         (if (contains? errors :dob)
           [:div {:class "alert alert-danger"} (:dob errors)])]
        [:tr [:td [:input {:type "submit" :class "btn btn-primary" :value "save"}]]]]
       ]]]]])

(defn list-charges
  [charges]
  (for [charge charges]
    [:tr
     [:td (:id charge)]
     [:td (:name charge)]
     [:td
      [:form {:action "/charge_delete", :method "POST", :id (str "form_" (charge :id)), :onsubmit (str "return confirm('Delete " (:name charge) ".\\n Are you sure?');")}
       (anti-forgery-field)
       [:input {:type "hidden", :value (:id charge) :name "id"}]
       [:input {:type "submit", :value "X", :name (str "submit_" (:id charge) )}]]]]))

(defn charge
  [{:keys [charges name errors]}]
  [:div {:class "row"}
   [:div {:class "span12"}
    [:table {:class "employees table table-bordered table-striped table-highlight"}
     [:thead
      [:tr
       [:th {:style "width:20px"} "Id"]
       [:th "Charge Name"]]]
     [:tbody
      (list-charges charges)
      [:tr
       [:form {:action "/charge", :method "POST"}
        [:td {:colspan "2"} "Charge Name:"
         [:input {:type "text" :value name :name "name" :class "form-control"}]
         (if (contains? errors :name)
           [:div {:class "alert alert-danger"} (:name errors) ])
         [:td
          (anti-forgery-field)
          [:input {:type "submit", :value "save", :class "btn btn-primary"}]]]]]]]]])

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

(defn base
  [{:keys [page servlet-context body]}]
  (list [:head
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
          body]]))

(defn add-day-header [date]
  [:thead
   [:tr {:style "background-color:#0d47a1;color:#ffffff"}
    [:th {:style "width: 180px;text-align: center;"} "Name"]
    (for [day (dutil/work-week date)]
      [:th {:style "width: 30px; text-align: center;"} (dutil/day-name day) [:br] (f/unparse dutil/MM-dd-formatter day)])
    [:th {:style "width: 30px;text-align: center;"} "Total"]]])

(defn select-option-map
  [row charge row-data]
  (let [charge-key (keyword (str "charge-row" row))]
    (if (= (:id charge) (charge-key row-data))
        {:value (:id charge) :selected "true" :disabled "true"}
        {:value (:id charge)})))

(defn charge-select
  [row charges row-data]
  [:select {:name (str "charge-row" row) :form "timesheet"}
   ;;blank
   [:option {:value "" }]
   (for [charge charges]
     [:option (select-option-map row charge row-data) (:name charge)])])

(defn cell-color
  "Return dark gray for the weekend cells, the first and last cell."
  [col]
  (if (some #{col} [0 6])
    "#606060"
    "#FFFFFF"))

(defn hours-cell
  [row col row-data]
  (let [cellname (str "row" row "-" col)]
    [:td {:style (str "width: 30px;text-align: center; background-color:" (cell-color col) "; color: #ff0000 ;")}
     [:input {:onblur (str "findTotal('row" row "')"), :type "text", :name cellname, :size "4" :value ((keyword cellname) row-data)}]]))

(defn hours-row
  [row charges row-data]
  [:tr
   (str "<!--row" row "-->")
   [:td (charge-select row charges row-data)]
   (for [col (range 7)]
     (hours-cell row col row-data))
   [:td {:style "width: 30px;text-align: center;"}
    [:input {:type "text", :id (str "row" row), :size "4"}]" "]
   (str "<!--end of row" row "-->")])

(defn timesheet
  "Take the params and make the timesheet page."
  [{:keys [enddate date charges row-data] :as all}]
  (list
   [:style "form { float:right; }"]
   [:div {:class "span12" :style "text-align:right"}
    "Period End Date:"
    [:input {:type "text" :name "enddate" :style "display-enddate" :value enddate :disabled "disabled" }]
    [:form {:method "POST", :action "/timesheet_next"}
     (anti-forgery-field)
     [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
     [:input {:type "submit", :class "btn btn-primary", :name "forward", :value "->"}]]

    [:form {:method "POST", :action "/timesheet_back"}
     (anti-forgery-field)
     [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
     [:input {:type "submit", :class "btn btn-primary", :name "backward", :value "<-"}]]
    ]
   [:div {:class "row"}
    [:div {:class "span12"}
     [:table {:class "timesheet fixed table table-bordered table-striped table-highlight"}
      (add-day-header date)
      [:form {:method "POST", :id "timesheet", :action "/timesheet_submit"}
       (anti-forgery-field)
       [:input {:type "text", :style "display:none", :name "enddate", :value enddate}]
       [:tbody
        (let [my-data (reduce #(merge %1 %2) {} row-data)]
          (for [row (range 6)]
            (hours-row row charges my-data)))
        [:tr
         [:td {:colspan "2"}
          [:input {:type "text", :name "Signature", :value ""}]]
         [:td {:colspan "5"}]
         [:td {:colspan "2"}
          [:input {:type "submit", :class "btn btn-primary", :name "submit", :value "Submit"}]]]]]
      ]]
    [:script {:type "text/javascript"} "\nfunction findTotal(name){\n  var tot=0;\n  for (i = 0; i < 7; i++) {\n    var arr = document.getElementsByName(name + '-' + i)
  [0];\n    if(parseInt(arr.value))\n      tot += parseInt(arr.value);\n    document.getElementById(name).value = tot;\n  }\n}"]]))

(defn add-base [page {:keys [body] :as all}]
  (assoc all :body (hp/html5 (base {:page page :servlet-context "" :body body}))))
