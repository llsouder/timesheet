(ns timesheet.routes.timesheet-test
  (:require [clojure.test :refer :all]
            [clj-time.core :as t]
            [timesheet.routes.timesheet :refer :all]))

(deftest test-rows
  (testing "Testing the non zero and inclusive range function I call rows."
    (is (= '(0 1 2 ) (rows 3)))
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
  (testing "bad-data validation."
    (let [result (validate-row (zero-empty-cells bad-data) 1)]
      (let [errormap (first result)
            datamap (second result)]
        (is (= "must be a long" (:row1-0 errormap)))
        (is (= "must be a long" (:row1-1 errormap)))
        (is (= nil (:row1-2 errormap)))
        (is (= "Bubba" (:Signature datamap)))))))

(def multi-bad-data {:charge-row1 "0" :row1-0 "nan" :charge-row2 "1" :row2-1 "nan" :row2-0 "nan"} )
(deftest test-validation
  (testing "bad-data validation."
    (let [result (validate-row (zero-empty-cells multi-bad-data) 1)]
      (let [errormap (first result)
            datamap (second result)]
        (is (= "must be a long" (:row1-0 errormap)))))))

(deftest test-parse-submitted-data
  (testing "do I get back errors for more than one row?"
    (let [result (parse-submitted-data multi-bad-data)
          expected {:row1-0 "must be a long" :row2-0 "must be a long" :row2-1 "must be a long"}]
      (is (= expected (:errors result))))))


(deftest test-get-row
  (testing "get-row single digit"
    (is (= 9 (get-row :charge-row9)))))

(deftest test-get-row-double-digit
  (testing "get-row double digit"
    (is (= 11 (get-row :charge-row11)))))

(def zero-row-bad-data {:charge-row0 "1" :row0-0 "nan" :row0-1 "nan" :row0-2 "1"} )
(deftest test-validate-data-zero-row
  (testing "does the zero row even validate"
    (let [result (validate-data :charge-row0 zero-row-bad-data)
          expected {:row0-0 "must be a long", :row0-1 "must be a long"}]
      (is (= expected (:errors result))))))

(def zero-row-good-data {:charge-row0 "1" :row0-0 "1" :row0-1 "0" :row0-2 "8"} )
(deftest test-validate-data-zero-row
  (testing "does the zero row even validate"
    (let [result (validate-data :charge-row0 zero-row-good-data)
          expected {:charge-row0 "1" :row0-0 1, :row0-1 0 :row0-2 8}]
      (is (= expected (:data result))))))

(def submit-bad-data { :params {:submit "Submit"
                                :enddate "09-09-2017"
                                :Signature ""
                                :charge-row0 "1"
                                :row0-0 "1"
                                :row0-1 "3"
                                :row0-2 "5"
                                :row0-3 "dd"
                                :row0-4 ""
                                :row0-5 ""
                                :row0-6 ""
                                :charge-row1 ""
                                :row1-0 ""
                                :row1-1 ""
                                :row1-2 ""
                                :row1-3 ""
                                :row1-4 ""
                                :row1-5 ""
                                :row1-6 ""}})

(deftest test-submit-data
  (testing "submit row0 with non num data dd."
    (let [expected {:errors {:row0-3 "must be a long"}
                    :data {}}
          result  (parse-submitted-data (:params submit-bad-data))]
      (is (= expected result)))))

(def submit-good-data (assoc-in submit-bad-data  [:params :row0-3] "6"))

(deftest test-submit-data
  (testing "submit row0 with non num data dd."
    (let [expected {:errors {}
                    :data {:charge-row0 "1" :row0-0 1 :row0-1 3 :row0-2 5 :row0-3 6 :row0-4 0 :row0-5 0 :row0-6 0
                           :charge-row1 "" :row1-0 "0" :row1-1 "0" :row1-2 "0" :row1-3 "0" :row1-4 "0" :row1-5 "0" :row1-6 "0"
                           :Signature "" :submit "Submit" :enddate "09-09-2017"}}
          result  (parse-submitted-data (:params submit-good-data))]
      (is (= expected result)))))

(def good-format-row-data (:data (parse-submitted-data (:params (assoc-in submit-bad-data  [:params :row0-3] "6")))))

(deftest test-make-sql-row-args
  (testing "format data for sql create/update."
    (let [expected '({:charge_id "1"
                     :sun 1
                     :mon 3
                     :tue 5
                     :wed 6
                     :thu 0
                     :fri 0
                     :sat 0})
        actual (make-sql-row-args good-format-row-data)]
      (is (= expected actual)))))

;;you might need to put data into h2 and retrieve it to
;;get the data for this test correct.
(def sql-2-web {:employee_number "000"
                :charge_id 1
                :end_date #inst "2017-09-09T04:00:00.000-00:00"
                :sun 1
                :mon 2
                :tue 3
                :wed 4
                :thu 0
                :fri 0
                :sat 0})

(deftest test-sql-2-web
  (testing "Convert the data into a format for the web page."
    (let [expected {:employee_number "000" :charge-row0 1 :row0-0 1
                    :row0-1 2 :row0-2 3 :row0-3 4
                    :row0-4 0 :row0-5 0 :row0-6 0}
          result  (convert-sql-to-form 0 sql-2-web)]
  (is (= expected result)))))
