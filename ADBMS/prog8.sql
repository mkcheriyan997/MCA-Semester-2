SET LINESIZE 200;
COLUMN Name FORMAT A20;

CREATE TABLE Savings_Transactions (
    Customer_ID NUMBER PRIMARY KEY,
    Name VARCHAR2(50),
    TransactionAmount NUMBER(10, 2)
);

CREATE TABLE CreditCard_Transactions (
    Customer_ID NUMBER PRIMARY KEY,
    Name VARCHAR2(50),
    TransactionAmount NUMBER(10, 2)
);

INSERT INTO Savings_Transactions VALUES (1, 'Alice', 5000);
INSERT INTO CreditCard_Transactions VALUES (1, 'Alice', 2000);

INSERT INTO Savings_Transactions VALUES (2, 'Bob', 3000);
INSERT INTO CreditCard_Transactions VALUES (2, 'Bob', 1500);

INSERT INTO Savings_Transactions VALUES (3, 'Charlie', 7000);

INSERT INTO CreditCard_Transactions VALUES (4, 'David', 1000);


SELECT Customer_ID, Name FROM Savings_Transactions
UNION
SELECT Customer_ID, Name FROM CreditCard_Transactions;


SELECT Customer_ID, Name FROM Savings_Transactions
INTERSECT
SELECT Customer_ID, Name FROM CreditCard_Transactions;


SELECT Customer_ID, Name FROM Savings_Transactions
MINUS
SELECT Customer_ID, Name FROM CreditCard_Transactions;

DROP TABLE Savings_Transactions;
DROP TABLE CreditCard_Transactions;
