SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN Name FORMAT A20;
COLUMN Category FORMAT A20;
COLUMN ProductDesc FORMAT A40;
COLUMN Tier FORMAT A10;
COLUMN "Mfg Date" FORMAT A12;

CREATE TABLE Product (
    ProductID NUMBER PRIMARY KEY,
    Name VARCHAR2(100),
    Category VARCHAR2(50),
    ManufacturingDate DATE,
    Quantity NUMBER,
    Price NUMBER(10, 2)
);

INSERT ALL
    INTO Product VALUES (101, 'Laptop', 'Electronics', SYSDATE-10, 5, 1500)
    INTO Product VALUES (102, 'Mouse', 'Electronics', SYSDATE-5, 50, 25)
    INTO Product VALUES (199, 'Cable', 'Misc', SYSDATE, 10, NULL)
SELECT * FROM DUAL;

SELECT Name, Price, Quantity * Price AS TotalValue FROM Product;

SELECT DISTINCT Category FROM Product;

SELECT Name || ' (' || Category || ')' AS ProductDesc FROM Product;

SELECT Name, NVL(Price, 0) AS Price, CASE WHEN Price > 100 THEN 'High' ELSE 'Low' END AS Tier FROM Product;

SELECT Name AS ProductName, ManufacturingDate AS "Mfg Date" FROM Product;

SELECT * FROM Product ORDER BY Price DESC NULLS LAST;

SELECT Category, Name FROM Product ORDER BY Category, Price;

SELECT Category, COUNT(*), AVG(Price) FROM Product GROUP BY Category;

SELECT Category, SUM(Quantity) FROM Product GROUP BY Category HAVING SUM(Quantity) > 10;

SELECT Category, TO_CHAR(ManufacturingDate, 'YYYY'), SUM(Quantity) FROM Product GROUP BY CUBE(Category, TO_CHAR(ManufacturingDate, 'YYYY'));

DELETE FROM Product WHERE Price < 50;

DELETE FROM Product WHERE ProductID = (SELECT ProductID FROM Product WHERE Price = (SELECT MAX(Price) FROM Product));

SELECT * FROM Product;