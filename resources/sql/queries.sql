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
-- :doc Read a user given the id.
SELECT * FROM employee
WHERE employee_number = :employee_number

-- :name get-all-employees :? :*
-- :doc Read all the employees.
SELECT * FROM employee

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM employee
WHERE employee_number = :employee_number


-- :name create-charge! :! :n
-- :doc Creates a new charge record
INSERT INTO charge
(name)
VALUES (:name)

-- :name get-charge :? :1
-- :doc Retrieve a charge given the id.
SELECT * FROM charge
WHERE id = :id

-- :name update-charge! :! :n
-- :doc Update an existing charge record
UPDATE charge
SET name = :name
WHERE id = :id

-- :name delete-charge! :! :n
-- :doc Delete a charge given the id
DELETE FROM charge
WHERE id = :id

-- :name get-all-charges :? :*
-- :doc Read all the charges.
SELECT * FROM charge

-- :name get-all-hours :? :*
-- :doc Read all the hours for this employee.
SELECT * FROM hours
WHERE employee_number = :employee_number AND
end_date = parsedatetime(:end_date , 'MM-dd-yyyy');

-- :name insert-hours! :! :n
-- :doc Insert or create a single hours record returning affected row count
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN TRANSACTION;
UPDATE hours
  SET sun = :sun, mon = :mon, tue = :tue, wed = :wed, thu = :thu, fri = :fri, sat = :sat)
  WHERE employee_number = :employee_number AND
  charge_id = :charge_id AND
  end_date = :end_date;
IF @@ROWCOUNT = 0
BEGIN
  INSERT INTO hours (employee_number, charge_id, end_date, sun, mon, tue, wed, thu, fri, sat)
    VALUES (:employee_number, :charge_id, parsedatetime(:end_date, 'yyyy-MM-dd'), :sun, :mon, :tue, :wed, :thu, :fri, :sat)
END
COMMIT TRANSACTION;

-- :name create-hours! :! :n
-- :doc Insert multiple hour records with :tuple* parameter type
insert into hours (employee_number, charge_id, end_date, sun, mon, tue, wed, thu, fri, sat)
values :tuple*:hours

-- :name update-hour! :! :n
-- :doc Update an existing hour record
UPDATE hours
SET sun = :sun, mon = :mon, tue = :tue, wed = :wed, thu = :thu, fri = :fri, sat = :sat)
WHERE employee_number = :employee_number AND
charge_id = :charge_id AND
end_date = :end_date;

-- :name delete-hour! :! :n
-- :doc Delete an existing hour record
DELETE FROM hours
WHERE employee_number = :employee_number AND
charge_id = :charge_id AND
end_date = :end_date;
