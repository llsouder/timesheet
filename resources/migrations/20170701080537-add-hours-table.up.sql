CREATE TABLE IF NOT EXISTS hours
(employee_number VARCHAR(20),
  charge_id INT,
  day DATE,
  hours INT,
  foreign key (employee_number) references employee,
  foreign key (charge_id) references charge(id),
  primary key (employee_number, charge_id, day));
