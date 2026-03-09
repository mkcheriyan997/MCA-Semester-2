SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN "Dept Name" FORMAT A20;
COLUMN "Head of Department" FORMAT A20;
COLUMN Name FORMAT A20;

DROP TABLE Employee CASCADE CONSTRAINTS;
DROP TABLE Department CASCADE CONSTRAINTS;

CREATE TABLE Department (
    "Department Id" NUMBER CONSTRAINT pk_dept PRIMARY KEY,
    "Dept Name" VARCHAR2(50) CONSTRAINT uq_dept_name UNIQUE NOT NULL,
    "Head of Department" VARCHAR2(50) NOT NULL
);

CREATE TABLE Employee (
    "Employee Id" NUMBER CONSTRAINT pk_emp PRIMARY KEY,
    Name VARCHAR2(100) NOT NULL,
    "Department Id" NUMBER CONSTRAINT fk_dept REFERENCES Department("Department Id") NOT NULL,
    Salary NUMBER CONSTRAINT chk_salary CHECK (Salary > 0)
);

ALTER TABLE Employee ADD CONSTRAINT uq_emp_name UNIQUE (Name);

ALTER TABLE Employee ADD CONSTRAINT chk_emp_id CHECK ("Employee Id" > 100);

INSERT ALL
    INTO Department VALUES (1, 'CSE', 'Dr. Smith')
    INTO Department VALUES (2, 'ECE', 'Dr. Jones')
SELECT * FROM DUAL;

INSERT ALL
    INTO Employee VALUES (101, 'Ravi', 1, 50000)
    INTO Employee VALUES (102, 'Priya', 2, 55000)
SELECT * FROM DUAL;

SELECT * FROM Department;
SELECT * FROM Employee;