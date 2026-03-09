SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN Sname FORMAT A20;

CREATE TABLE Sailors (
    Sid NUMBER PRIMARY KEY,
    Sname VARCHAR2(20),
    Rating NUMBER,
    Age NUMBER
);

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
    INTO Sailors VALUES (101, 'Prachi', 8, 22)
    INTO Sailors VALUES (102, 'Priya', 7, 25)
    INTO Sailors VALUES (103, 'Nayana', 9, 32)
SELECT * FROM DUAL;

COMMIT;

SELECT Sname, Age FROM Sailors WHERE Age = (SELECT MIN(Age) FROM Sailors);

SELECT Rating, AVG(Age) FROM Sailors GROUP BY Rating;

SELECT COUNT(DISTINCT Sname) FROM Sailors;

SELECT AVG(Rating), MIN(Rating), MAX(Rating) FROM Sailors;

SELECT Sname FROM Sailors WHERE Sname LIKE 'P%i';

SELECT Sname FROM Sailors WHERE Sname LIKE '%ya%';

SELECT Sname, Age FROM Sailors WHERE TO_CHAR(Age) LIKE '%2%';

SELECT Sname FROM Sailors WHERE Sname LIKE 'Na__na';

SELECT Sname FROM Sailors WHERE Sname NOT LIKE 'Priya';