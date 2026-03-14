SET VERIFY OFF;
SET SERVEROUTPUT ON;
DECLARE
  num       NUMBER := &enter_number;
  factorial NUMBER := 1;
BEGIN
  FOR i IN 1..num LOOP
    factorial := factorial * i;
  END LOOP;
  DBMS_OUTPUT.PUT_LINE('The factorial of ' || num || ' is ' || factorial || '.');
END;
/