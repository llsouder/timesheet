CREATE TABLE IF NOT EXISTS employee
(employee_number VARCHAR(20) PRIMARY KEY,
 email VARCHAR(30),
 first_name VARCHAR(30),
 last_name VARCHAR(30),
 dob DATE,
 street VARCHAR(30),
 city VARCHAR(30),
 state VARCHAR(2),
 zip VARCHAR(10),
 admin BOOLEAN,
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(300));
--;;
CREATE TABLE IF NOT EXISTS work_group
(group_id INT auto_increment primary key,
name VARCHAR(30));
--;;
CREATE TABLE IF NOT EXISTS employee_in_group
(employee_number VARCHAR(20),
group_id INT,
foreign key (employee_number) references employee,
foreign key (group_id) references work_group,
primary key (employee_number, group_id));
