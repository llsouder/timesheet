(ns timesheet.handler_test
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [timesheet.handler :refer :all]))

(deftest test-home
  (testing "main route"
    (let [response ((app) (request :get "/"))]
      (is (= 200 (:status response)))))

  (testing "about route"
    (let [response ((app) (request :get "/about"))]
      (is (= 200 (:status response)))))

  (testing "timesheet route with unset "
    (let [response ((app) (request :get "/timesheet"))]
      (is (= 200 (:status response)))))

  (testing "admin route"
    (let [response ((app) (request :get "/admin"))]
      (is (= 200 (:status response)))))

  (testing "charge route"
    (let [response ((app) (request :get "/charge"))]
      (is (= 200 (:status response)))))

  (testing "not-found route"
    (let [response ((app) (request :get "/invalid"))]
      (is (= 404 (:status response))))))

