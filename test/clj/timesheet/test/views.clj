(ns timesheet.test.views
  (:require [clojure.test :refer :all]
            [hiccup.page :as hp]
            [timesheet.views :refer :all]))

(deftest test-base
  (testing "passing a map and get the base html with an active"
    (is (.contains (hp/html5 (base "home" :servlet-context "")) "active"))))

(deftest test-active
  (testing "returns the string 'active' when the page is equal to arg"
    (is (= "active" (active "home.html" "home.html")))))

(deftest test-not-active
  (testing "returns the string 'active' when the page is equal to arg"
    (is (= "" (active "admin.html" "home.html")))))
