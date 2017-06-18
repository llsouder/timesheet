CREATE TABLE IF NOT EXISTS charge
(id int auto_increment primary key,
name VARCHAR(20));
--;;
CREATE TABLE IF NOT EXISTS employee_charge
(employee_number VARCHAR(20),
charge_id INT,
foreign key (employee_number) references employee,
foreign key (charge_id) references charge(id),
primary key (employee_number, charge_id));
