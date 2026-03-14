SET VERIFY OFF;
SET SERVEROUTPUT ON;
DECLARE
  input_str    VARCHAR2(100) := '&str';
  reversed_str VARCHAR2(100);
BEGIN
  SELECT REVERSE(input_str) INTO reversed_str FROM DUAL;
  DBMS_OUTPUT.PUT_LINE('Reversed string: ' || reversed_str);
END;
/