DROP TABLE hours;

CREATE TABLE IF NOT EXISTS hours
(employee_number VARCHAR(20),
  charge_id INT,
  end_date DATE,
  sun INT,
  mon INT,
  tue INT,
  wed INT,
  thu INT,
  fri INT,
  sat INT,
  foreign key (employee_number) references employee,
  foreign key (charge_id) references charge(id),
  primary key (employee_number, charge_id, end_date));
