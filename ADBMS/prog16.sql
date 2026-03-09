SET VERIFY OFF;
SET SERVEROUTPUT ON;
DECLARE
  radius NUMBER := &radius;
  pi CONSTANT NUMBER := 3.14159;
BEGIN
  DBMS_OUTPUT.PUT_LINE('The area is: ' || (pi * radius * radius));
END;
/
