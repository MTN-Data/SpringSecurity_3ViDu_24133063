-- Chạy một lần trong SQL Server Management Studio.
IF DB_ID(N'UTEShop3') IS NULL
BEGIN
    EXEC('CREATE DATABASE UTEShop3');
END;
