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

PROMPT 'PROG 11: Union, Intersect and Minus (Set operations)'
PROMPT '1. Find the ids of sailors who have reserved a red boat or a green boat'
SELECT R.Sid FROM Reserves R JOIN Boats B ON R.Bid = B.Bid WHERE B.Color = 'red'
UNION
SELECT R.Sid FROM Reserves R JOIN Boats B ON R.Bid = B.Bid WHERE B.Color = 'green';

PROMPT '2. Find the names of sailors who have reserved boat 102.'
SELECT S.Sname FROM Sailors S JOIN Reserves R ON S.Sid = R.Sid WHERE R.Bid = 102;

PROMPT '3. Find the name and the age of the youngest sailor'
SELECT Sname, Age FROM Sailors WHERE Age = (SELECT MIN(Age) FROM Sailors);

PROMPT '4. Find the names of sailors who have reserved all boats.'
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