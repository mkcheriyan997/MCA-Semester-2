SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN Emp_Name FORMAT A25;
COLUMN Dept FORMAT A20;
COLUMN Designation FORMAT A25;
COLUMN DOB FORMAT A12;

CREATE TABLE Employee (
    Emp_ID NUMBER PRIMARY KEY,
    Emp_Name VARCHAR2(50),
    DOB DATE,
    Dept VARCHAR2(30),
    Designation VARCHAR2(30),
    Salary NUMBER(10, 2)
);

INSERT ALL
    INTO Employee VALUES (101, 'Alice Smith', TO_DATE('15-JAN-1985', 'DD-MON-YYYY'), 'HR', 'Manager', 75000.00)
    INTO Employee VALUES (102, 'Bob Johnson', TO_DATE('20-FEB-1990', 'DD-MON-YYYY'), 'IT', 'Developer', 60000.50)
    INTO Employee VALUES (103, 'Charlie Brown', TO_DATE('01-MAR-1988', 'DD-MON-YYYY'), 'Finance', 'Analyst', 55000.75)
    INTO Employee VALUES (104, 'Diana Prince', TO_DATE('10-APR-1992', 'DD-MON-YYYY'), 'IT', 'Senior Developer', 80000.00)
SELECT * FROM DUAL;

SELECT * FROM Employee;