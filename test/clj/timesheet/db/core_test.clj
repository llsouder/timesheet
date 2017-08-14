(ns timesheet.db.core-test
  (:require [timesheet.db.core :refer [*db*] :as db]
            [luminus-migrations.core :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [timesheet.config :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (mount/start
      #'timesheet.config/env
      #'timesheet.db.core/*db*)
    (migrations/migrate ["migrate"] (select-keys env [:database-url]))
    (f)))

(deftest test-check-employee-number
  (jdbc/with-db-transaction[t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= '({:rows 0}) (db/check-employee-number t-conn {:employee_number 1}))) 
    (is (= 1 (db/create-employee!
               t-conn
               {:employee_number         "1"
                :first_name "Sam"
                :last_name  "Smith"
                ;;:email      "sam.smith@example.com"
                ;;:pass       "pass"
                :dob "2020-03-01"})))
    (is (= '({:rows 1}) (db/check-employee-number t-conn {:employee_number 1})))))

(deftest test-employee
  (jdbc/with-db-transaction [t-conn *db*]
    (jdbc/db-set-rollback-only! t-conn)
    (is (= 1 (db/create-employee!
               t-conn
               {:employee_number         "1"
                :first_name "Sam"
                :last_name  "Smith"
                ;;:email      "sam.smith@example.com"
                ;;:pass       "pass"
                :dob "2020-03-01"})))
    (is (= {:employee_number         "1"
            :first_name "Sam"
            :last_name  "Smith"
            ;;:email      "sam.smith@example.com"
            :email      nil
            ;;:pass       "pass"
            :pass       nil
            :street       nil
            :city       nil
            :state       nil
            :zip       nil
            :admin      nil
            :last_login nil
            :is_active  nil
            :dob #inst "2020-03-01T05:00:00.000-00:00"}
           (db/get-employee t-conn {:employee_number "1"})))
    ))
