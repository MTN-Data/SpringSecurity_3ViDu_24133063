-- Chạy một lần trong SQL Server Management Studio.
IF DB_ID(N'UTEShop2') IS NULL
BEGIN
    EXEC('CREATE DATABASE UTEShop2');
END;
