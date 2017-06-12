(ns timesheet.test.routes.timesheet
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [timesheet.routes.timesheet :refer :all]))

(deftest test-first-day-of-week-wed
  (testing "first day of week function on 6/7/2017, Wed, so 6/4/2017 is sunday."
    (let [thedate (t/date-time 2017 06 7)]
    (is (= 4 (t/day (first-day-of-week thedate)))))))

(deftest test-first-day-of-week-sun
  (testing "First day of week function on 6/4/2017 so 6/4/2017 is Sunday."
    (let [thedate (t/date-time 2017 06 4)]
    (is (= 4 (t/day (first-day-of-week thedate)))))))

(deftest test-first-day-of-week-last-year
  (testing "First day of week function on 6/4/2016 so 5/29/2016 is Sunday."
    (let [thedate (t/date-time 2016 06 4)]
    (is (= 29 (t/day (first-day-of-week thedate)))))))

(deftest test-work-week
  (testing "Work week function 6/4/2016 should return the week of 5/29/2016 ."
    (let [thedate (t/date-time 2017 06 6)]
      (is (= (t/date-time 2017 06 04) (nth (work-week thedate) 0 ))
      (is (= (t/date-time 2017 06 10) (nth (work-week thedate) 6 )))))))

(deftest test-day-name-sunday
  (testing "Testing 6/4/2017 is Sunday."
    (let [thedate (t/date-time 2017 06 4)]
    (is (= "Sunday" (day-name thedate))))))

(deftest test-day-name-thursday
  (testing "Testing 6/4/2016 is Thursday"
    (let [thedate (t/date-time 2015 06 4)]
    (is (= "Thursday" (day-name thedate))))))

(deftest test-formatted-week
  (testing "Html strings for the webpage."
    (let [thedate (t/date-time 2015 06 4)]
    (is (= "Sunday<br>05-31" (nth (formatted-work-week thedate) 0)))
    (is (= "Monday<br>06-01" (nth (formatted-work-week thedate) 1)))
    (is (= "Tuesday<br>06-02" (nth (formatted-work-week thedate) 2)))
    (is (= "Wednesday<br>06-03" (nth (formatted-work-week thedate) 3))))))

(deftest test-formatted-end-date-1
  (testing "Testing  the week of the 4th ends on Saturday the 10th."
    (let [thedate (t/date-time 2017 06 4)]
    (is (= "06-10-2017" (formatted-end-date thedate))))))

(deftest test-formatted-end-date-2
  (testing "Testing  the week of the 5-28 ends on Saturday the 3rd."
    (let [thedate (t/date-time 2017 05 30)]
    (is (= "06-03-2017" (formatted-end-date thedate))))))
