Quick CRUD in Luminus
=====================

Add Tables
----------
Using migratus to add and remove tables to the database.  Currently testing with H2 database.  Hopefully when I switch over to Postgres the process I am documenting here will not change.

add table to resources/migrations up and down
  lein migratus create add-<name>-table

Edit the new files created by "migratus create." They are located in resources/migrations and will have the date and time prefixed to the file name.  There is an up and down.  Put the create table statements in up and drop table statements in down.

20170513221513-add-myteststuff-table.up.sql
```sql
CREATE TABLE IF NOT EXISTS employee
(employee_number INT,
  fname VARCHAR(20),
  lname VARCHAR(20));
--;;
CREATE TABLE IF NOT EXISTS group
(id INT,
  name VARCHAR(20));
--;;
CREATE TABLE IF NOT EXISTS employee-group
(employee_id INT,
  group_id INT,
  foreign key (employee_id) references employee(id),
  foreign key (group_id) references group(id),
  primary key (employee_id, group_id));
```

20170513221513-add-myteststuff-table.down.sql
```sql
DROP TABLE employee;
--;;
DROP TABLE group;
--;;
DROP TABLE employee-group;
```
 
Add Queries
-----------

add insert, remove, select statements to resources/queries.sql
Create  INSERT INTO xxxxxx
Read    SELECT * FROM xxxxxx WHERE id=:id
Update  UPDATE xxxxxx SET xxxx=:xxxx WHERE id=:id
Delete  DELETE FROM xxxxxx WHERE id=:id


add html template to resources template.

create a src/clj/<project>/routes/xxxx.clj with:

(def <table>-schema [[:fld1 xxxx] [:fld2 ]])

add new routes to handler.  Note you cannot use the DELETE routes in html forms.


