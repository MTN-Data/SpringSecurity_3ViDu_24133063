-- Chạy một lần trong SQL Server Management Studio.
IF DB_ID(N'UTEShop1') IS NULL
BEGIN
    EXEC('CREATE DATABASE UTEShop1');
END;
