(ns timesheet.views-test
  (:require [clojure.test :refer :all]
            [hiccup.page :as hp]
            [timesheet.views :refer :all]))

(deftest test-base
  (testing "passing a map and get the base html with an active"
    (prn (hp/html5 (base {:page "home" :servlet-context "" :body ""})))
    (is (.contains (hp/html5 (base {:page "home" :servlet-context "" :body ""})) "active"))))

(deftest test-active
  (testing "returns the string 'active' when the page is equal to arg"
    (is (= "active" (active "home.html" "home.html")))))

(deftest test-not-active
  (testing "returns the string 'active' when the page is equal to arg"
    (is (= "" (active "admin.html" "home.html")))))

(def row-data
  {:charge-row0 1
   :row0-0 1
   :row0-1 2
   :row0-2 3
   :row0-3 4
   :row0-4 0
   :row0-5 0
   :row0-6 0
   :charge-row1 3
   :row1-0 1
   :row1-1 2
   :row1-2 3
   :row1-3 4
   :row1-4 0
   :row1-5 0
   :row1-6 0
   :charge-row2 2
   :row2-0 11
   :row2-1 22
   :row2-2 33
   :row2-3 0
   :row2-4 0
   :row2-5 0
   :row2-6 0
   :charge-row3 4
   :row3-0 0
   :row3-1 0
   :row3-2 0
   :row3-3 0
   :row3-4 8
   :row3-5 0
   :row3-6 0
   :employee_number "000"})

(def charges '({:id 1 :name "10-10 Charge"} {:id 2 :name "20-55 Charge"} {:id 3 :name "Sick"} {:id 4 :name "Vacation"}))

(deftest test-get-charge-name
  (testing "iinputs from the views.clj"
    (let [actual (get-charge-name 0 charges row-data)]
         (is (= "10-10 Charge" actual)))))
