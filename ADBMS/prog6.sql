SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN Sname FORMAT A20;
COLUMN Bname FORMAT A20;
COLUMN Color FORMAT A10;
COLUMN Day FORMAT A12;

DROP TABLE Reserves;
DROP TABLE Sailors;
DROP TABLE Boats;

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

SELECT * FROM Sailors WHERE Sid IN (SELECT Sid FROM Reserves WHERE Bid = 101 OR Bid = 103);

SELECT Sname FROM Sailors WHERE Sid IN (SELECT Sid FROM Reserves);

SELECT S.Sname FROM Sailors S WHERE S.Sid IN (SELECT R.Sid FROM Reserves R JOIN Boats B ON R.Bid = B.Bid WHERE B.Color = 'red') ORDER BY S.Age;

SELECT S.Sid, S.Sname FROM Sailors S WHERE S.Sid IN (SELECT R1.Sid FROM Reserves R1 JOIN Reserves R2 ON R1.Sid = R2.Sid AND R1.Day = R2.Day AND R1.Bid != R2.Bid);

SELECT Sname, Age FROM Sailors WHERE Age = (SELECT MIN(Age) FROM Sailors);

SELECT S.Sname FROM Sailors S WHERE NOT EXISTS (SELECT B.Bid FROM Boats B WHERE B.Bid NOT IN (SELECT R.Bid FROM Reserves R WHERE R.Sid = S.Sid));