(ns timesheet.routes.timesheet-test
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
    (is (= "Sunday<br>05-31" (nth (work-week-header thedate) 0)))
    (is (= "Monday<br>06-01" (nth (work-week-header thedate) 1)))
    (is (= "Tuesday<br>06-02" (nth (work-week-header thedate) 2)))
    (is (= "Wednesday<br>06-03" (nth (work-week-header thedate) 3))))))

(deftest test-formatted-end-date-1
  (testing "Testing the week of the 4th ends on Saturday the 10th."
    (let [thedate (t/date-time 2017 06 4)]
    (is (= "06-10-2017" (formatted-end-date thedate))))))

(deftest test-formatted-end-date-2
  (testing "Testing the week of the 5-28 ends on Saturday the 3rd."
    (let [thedate (t/date-time 2017 05 30)]
    (is (= "06-03-2017" (formatted-end-date thedate))))))

(deftest test-rows
  (testing "Testing the non zero and inclusive range function I call rows."
    (is (= '(1 2 3) (rows 3)))
    (is (= '() (rows 0)))
    (is (= '() (rows -3)))))

(deftest test-make-cell-key
  (testing "The cell key is the key found on the web page."
    (is (= :row1-1 (make-cell-key 1 1)))
    (is (= :row4-2 (make-cell-key 4 2)))
    (is (= :row2-7 (make-cell-key 2 7)))))

(def row8-keys '(:row8-0 :row8-1 :row8-2 :row8-3 :row8-4 :row8-5 :row8-6))

(deftest test-get-cell-keys-for
  (testing "Testing the that all the keys for a row are built."
    (is (= row8-keys (get-cell-keys-for 8)))))

(deftest test-get-row
  (testing "Testing the get-row conversion from the charge key"
    (is (= 1 (get-row :charge-row1)))
    (is (= 2 (get-row :charge-row2)))))

(deftest test-is-cell
  (testing "for the 0 column, row1-0, row2-0, ... row(n)-0."
    (is (= true (is-cell? [:row1-0 ""])))
    (is (= true (is-cell? [:row4-0 ""])))
    (is (= false (is-cell? [:row1-7 ""])))))

(def params {:row1-7 "not a cell" :row1-0 "" :row1-1 "2" :row1-2 "1" :row1-3 "" :row1-4  "" :row1-5 "" :row1-6 "8" :Signature "Bubba" :test1 "fred"})
(deftest test-zero-empty-cells
  (testing "Test making all cells and only cells zeros."
    (let [zeros (zero-empty-cells params)]
    (is (= "2" (:row1-1 zeros)))
    (is (= "0" (:row1-0 zeros)))
    (is (= "0" (:row1-4 zeros)))
    (is (= "not a cell" (:row1-7 zeros)))
    (is (= "Bubba" (:Signature zeros))))))

(deftest test-validation
  (testing "Test making all cells and only cells zeros."
    (let [result (validate-row (zero-empty-cells params) 1)]
      (let [datamap (second result)]
      (is (= 2 (:row1-1 datamap)))
      (is (= 0 (:row1-0 datamap)))
      (is (= 0 (:row1-4 datamap)))
      (is (= "not a cell" (:row1-7 datamap)))
      (is (= "Bubba" (:Signature datamap)))))))

(def bad-data {:row1-7 "not a cell" :row1-0 "nan" :row1-1 "nan" :row1-2 "1" :row1-3 "" :row1-4  "" :row1-5 "" :row1-6 "8" :Signature "Bubba" :test1 "fred"})
(deftest test-validation
  (testing "Test making all cells and only cells zeros."
    (let [result (validate-row (zero-empty-cells bad-data) 1)]
      (let [errormap (first result)
            datamap (second result)]
        (is (= "must be a long" (:row1-0 errormap)))
        (is (= "must be a long" (:row1-1 errormap)))
        (is (= nil (:row1-2 errormap)))
        (is (= "Bubba" (:Signature datamap)))))))
