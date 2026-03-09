CREATE USER MACEMCA2040 IDENTIFIED BY M25CA030;
SELECT username FROM all_users;
GRANT SELECT, INSERT ON your_table TO MACEMCA2040;
GRANT DBA TO MACEMCA2040;
CREATE USER MACE2040 IDENTIFIED BY MACEMCA;
GRANT SELECT, INSERT ON your_table TO MACE2040;
-- As MACE2040, you can now connect and perform SELECT and INSERT on your_table.
-- Example (run these commands after connecting as MACE2040):
-- SELECT * FROM your_table;
-- INSERT INTO your_table (column1, column2) VALUES ('valueA', 'valueB');
REVOKE SELECT, INSERT ON your_table FROM MACEMCA2040;
SELECT grantee, table_name, privilege FROM all_tab_privs WHERE grantee = 'MACEMCA2040';
REVOKE DBA FROM MACEMCA2040;
SELECT grantee, granted_role FROM all_role_privs WHERE grantee = 'MACEMCA2040';
DROP USER MACE2040 CASCADE;
SELECT username FROM all_users;
