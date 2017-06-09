(ns timesheet.test.routes.timesheet
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [timesheet.routes.timesheet :refer :all]))

(deftest test-first-day-of-week-wed
  (testing "first day of week function on 6/7/2017, Wed, so 6/4/2017 is sunday."
    (let [thedate (t/date-time 2017 06 7)]
    (is (= 4 (t/day (first-day-of-week thedate)))))))

(deftest test-first-day-of-week-sun
  (testing "first day of week function on 6/4/2017 so 6/4/2017 is sunday."
    (let [thedate (t/date-time 2017 06 4)]
    (is (= 4 (t/day (first-day-of-week thedate)))))))

(deftest test-first-day-of-week-last-year
  (testing "first day of week function on 6/4/2016 so 5/29/2016 is sunday."
    (let [thedate (t/date-time 2016 06 4)]
    (is (= 29 (t/day (first-day-of-week thedate)))))))
