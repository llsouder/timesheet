-- :name create-employee! :! :n
-- :doc creates a new employee record
INSERT INTO employee
(employee_number, first_name, last_name, dob )
VALUES (:employee_number, :first_name, :last_name, :dob)

-- :name check-employee-number :? :*
-- :doc check if employee number already exist
SELECT COUNT(*) as rows FROM employee
WHERE employee_number = :employee_number

-- :name update-employee! :! :n
-- :doc update an existing user record
UPDATE employee
SET employee_number = :employee_number, first_name = :first_name, last_name = :last_name, dob = :dob
WHERE employee_number = :employee_number

-- :name get-employee :? :1
-- :doc retrieve a user given the id.
SELECT * FROM employee
WHERE employee_number = :employee_number

-- :name get-all-employees :? :*
-- :doc retrieve all the employees.
SELECT * FROM employee

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM employee
WHERE employee_number = :employee_number
