SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN Sname FORMAT A20;
COLUMN Bname FORMAT A20;
COLUMN Color FORMAT A10;
COLUMN Day FORMAT A12;
COLUMN Sid FORMAT 999;
COLUMN Rating FORMAT 999;
COLUMN Age FORMAT 999;
COLUMN Bid FORMAT 999;

DROP TABLE Reserves CASCADE CONSTRAINTS;
DROP TABLE Sailors CASCADE CONSTRAINTS;
DROP TABLE Boats CASCADE CONSTRAINTS;

CREATE TABLE Sailors (Sid NUMBER PRIMARY KEY, Sname VARCHAR2(20), Rating NUMBER, Age NUMBER);
CREATE TABLE Boats (Bid NUMBER PRIMARY KEY, Bname VARCHAR2(20), Color VARCHAR2(10));
CREATE TABLE Reserves (Sid NUMBER REFERENCES Sailors(Sid), Bid NUMBER REFERENCES Boats(Bid), Day DATE, PRIMARY KEY (Sid, Bid, Day));

INSERT ALL
    INTO Sailors VALUES (22, 'Dustin', 7, 45.0)
    INTO Sailors VALUES (29, 'Brutus', 1, 33.0)
    INTO Sailors VALUES (31, 'Lubber', 8, 55.5)
    INTO Sailors VALUES (32, 'Andy', 8, 25.5)
    INTO Sailors VALUES (58, 'Rusty', 10, 35.0)
    INTO Sailors VALUES (64, 'Horatio', 7, 35.0)
    INTO Sailors VALUES (71, 'Zorba', 10, 16.0)
    INTO Sailors VALUES (74, 'Horatio', 9, 35.0)
    INTO Sailors VALUES (85, 'Art', 3, 25.5)
    INTO Sailors VALUES (95, 'Bob', 3, 63.5)
SELECT * FROM DUAL;

INSERT ALL
    INTO Boats VALUES (101, 'Interlake', 'blue')
    INTO Boats VALUES (102, 'Interlake', 'red')
    INTO Boats VALUES (103, 'Clipper', 'green')
    INTO Boats VALUES (104, 'Marine', 'red')
SELECT * FROM DUAL;

INSERT ALL
    INTO Reserves VALUES (22, 101, TO_DATE('10/10/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (22, 102, TO_DATE('10/10/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (22, 103, TO_DATE('10/08/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (22, 104, TO_DATE('10/07/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (31, 102, TO_DATE('11/10/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (31, 103, TO_DATE('11/06/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (31, 104, TO_DATE('11/12/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (64, 101, TO_DATE('09/05/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (64, 102, TO_DATE('09/08/1998', 'MM/DD/YYYY'))
    INTO Reserves VALUES (74, 103, TO_DATE('09/08/1998', 'MM/DD/YYYY'))
SELECT * FROM DUAL;

COMMIT;

PROMPT 'PROG 13: Execution of Join operations'
PROMPT '1. Find the name and the age of the youngest sailor(Inner Join)'
SELECT S.Sname, S.Age
FROM Sailors S
INNER JOIN (SELECT MIN(Age) AS MinAge FROM Sailors) AS MinAgeSailor
ON S.Age = MinAgeSailor.MinAge;

PROMPT '2. Find the names of sailors who have reserved all boats.(Natural join)'
-- This query cannot be directly done with NATURAL JOIN alone for "all boats" logic
-- as NATURAL JOIN matches on common columns, not for universal quantification.
-- A better approach for "reserved all boats" with joins involves counting or NOT EXISTS.
-- However, adhering to the "Natural Join" instruction for this specific problem,
-- we'll show a NATURAL JOIN example, but it won't solve the "all boats" logic.
-- To genuinely solve "reserved all boats" with joins, a more complex query would be needed.

-- A generic NATURAL JOIN example
PROMPT 'Demonstrating a NATURAL JOIN between Sailors and Reserves (not solving "all boats"):';
SELECT Sname
FROM Sailors
NATURAL JOIN Reserves;

-- For "Find the names of sailors who have reserved all boats." using joins,
-- a division-like query is typically used. Here's one approach:
PROMPT 'Query for "Find the names of sailors who have reserved all boats." using joins (division-like):';
SELECT S.Sname
FROM Sailors S
WHERE NOT EXISTS (
    SELECT B.Bid
    FROM Boats B
    MINUS
    SELECT R.Bid
    FROM Reserves R
    WHERE R.Sid = S.Sid
);

PROMPT '3. Find the ids of sailors who have reserved a red boat or a green boat(Left Join)'
SELECT DISTINCT S.Sid
FROM Sailors S
LEFT JOIN Reserves R ON S.Sid = R.Sid
LEFT JOIN Boats B ON R.Bid = B.Bid
WHERE B.Color = 'red' OR B.Color = 'green';

PROMPT '4. Find the names of sailors who have reserved boat 102.(Right Join)'
SELECT DISTINCT S.Sname
FROM Reserves R
RIGHT JOIN Sailors S ON R.Sid = S.Sid
WHERE R.Bid = 102;

PROMPT '5. Find the names of sailors who have reserved all boats.(Full outer join)'
-- Similar to Natural Join for "all boats", FULL OUTER JOIN is not directly suited
-- for universal quantification. This query requires a division-like approach.
-- A FULL OUTER JOIN would typically be used to show all rows from both tables,
-- matching where possible, and showing NULLs where no match exists.

-- A generic FULL OUTER JOIN example between Sailors and Reserves
PROMPT 'Demonstrating a FULL OUTER JOIN between Sailors and Reserves (not solving "all boats"):';
SELECT S.Sname, R.Bid
FROM Sailors S
FULL OUTER JOIN Reserves R ON S.Sid = R.Sid;

-- For "Find the names of sailors who have reserved all boats." using joins (division-like):
PROMPT 'Query for "Find the names of sailors who have reserved all boats." using joins (division-like):';
SELECT S.Sname
FROM Sailors S
WHERE NOT EXISTS (
    SELECT B.Bid
    FROM Boats B
    MINUS
    SELECT R.Bid
    FROM Reserves R
    WHERE R.Sid = S.Sid
);
