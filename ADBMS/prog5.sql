SET LINESIZE 200;
SET PAGESIZE 100;
COLUMN CategoryName FORMAT A20;
COLUMN Description FORMAT A30;
COLUMN ProductName FORMAT A20;

DROP TABLE Products;
DROP TABLE Categories;

CREATE TABLE Categories (
    CategoryID NUMBER PRIMARY KEY,
    CategoryName VARCHAR2(50),
    Description VARCHAR2(100)
);

CREATE TABLE Products (
    ProductID NUMBER PRIMARY KEY,
    ProductName VARCHAR2(50),
    CategoryID NUMBER,
    Price NUMBER(10, 2),
    CONSTRAINT fk_category FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID)
);

INSERT ALL
    INTO Categories VALUES (1, 'Electronics', 'Gadgets and devices')
    INTO Categories VALUES (2, 'Clothing', 'Apparel and fashion')
    INTO Categories VALUES (3, 'Furniture', 'Home and office furniture')
    INTO Categories VALUES (4, 'Books', 'Fiction and Non-fiction')
    INTO Categories VALUES (5, 'Sports', 'Outdoor and Indoor games')
SELECT * FROM DUAL;

INSERT ALL
    INTO Products VALUES (101, 'Laptop', 1, 55000.00)
    INTO Products VALUES (102, 'Smartphone', 1, 25000.00)
    INTO Products VALUES (103, 'T-Shirt', 2, 500.00)
    INTO Products VALUES (104, 'Jeans', 2, 1200.00)
    INTO Products VALUES (105, 'Table', 3, 4500.00)
    INTO Products VALUES (106, 'MysteryItem', NULL, 100.00)
    INTO Products VALUES (107, 'Headphones', 1, 2000.00)
    INTO Products VALUES (108, 'Novel', 4, 350.00)
    INTO Products VALUES (109, 'Football', 5, 800.00)
    INTO Products VALUES (110, 'Chair', 3, 1500.00)
SELECT * FROM DUAL;

SELECT P.ProductName, P.Price, C.CategoryName, C.Description
FROM Products P 
INNER JOIN Categories C ON P.CategoryID = C.CategoryID 
ORDER BY P.Price;

SELECT P.ProductName, P.Price, C.CategoryName 
FROM Products P 
LEFT JOIN Categories C ON P.CategoryID = C.CategoryID 
WHERE P.ProductName = 'Laptop';

SELECT P.ProductName, C.CategoryName 
FROM Products P 
RIGHT JOIN Categories C ON P.CategoryID = C.CategoryID 
WHERE C.CategoryName = 'Clothing';

SELECT MIN(Price) AS Min_Price
FROM Products P 
FULL JOIN Categories C ON P.CategoryID = C.CategoryID;

SELECT MAX(Price) AS Max_Price
FROM Products 
NATURAL JOIN Categories;
