SET VERIFY OFF;
SET SERVEROUTPUT ON;
BEGIN
  IF MOD(&&num, 2) = 0 THEN
    DBMS_OUTPUT.PUT_LINE(&num || ' is an even number.');
  ELSE
    DBMS_OUTPUT.PUT_LINE(&num || ' is an odd number.');
  END IF;
END;
/