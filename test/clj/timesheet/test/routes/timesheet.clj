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
