-- 1. Baza autoryzacyjna (użytkownicy i role)
CREATE DATABASE BibliotekaAuth;
GO
USE BibliotekaAuth;
GO

CREATE TABLE users (
                       user_name VARCHAR(50) NOT NULL PRIMARY KEY,
                       user_pass VARCHAR(50) NOT NULL
);

CREATE TABLE user_roles (
                            user_name VARCHAR(50) NOT NULL,
                            role_name VARCHAR(50) NOT NULL,
                            PRIMARY KEY (user_name, role_name),
                            FOREIGN KEY (user_name) REFERENCES users(user_name)
);

INSERT INTO users (user_name, user_pass) VALUES
                                             ('admin', 'admin'),
                                             ('czytelnik', 'czytelnik');

INSERT INTO user_roles (user_name, role_name) VALUES
                                                  ('admin', 'admin'),
                                                  ('admin', 'user'),
                                                  ('czytelnik', 'user');
GO

-- 2. Baza danych aplikacji (katalog książek)
CREATE DATABASE BibliotekaData;
GO
USE BibliotekaData;
GO

CREATE TABLE t_ksiazki (
                           id BIGINT IDENTITY(1,1) PRIMARY KEY,
                           tytul NVARCHAR(150) NOT NULL,
                           autor NVARCHAR(100) NOT NULL,
                           rok_wydania INT NOT NULL,
                           dostepna BIT NOT NULL DEFAULT 1
);

INSERT INTO t_ksiazki (tytul, autor, rok_wydania, dostepna) VALUES
                                                                (N'Wiedźmin: Ostatnie życzenie', N'Andrzej Sapkowski', 1993, 1),
                                                                (N'Solaris', N'Stanisław Lem', 1961, 1),
                                                                (N'Lalka', N'Bolesław Prus', 1890, 0),
                                                                (N'Clean Code', N'Robert C. Martin', 2008, 1);
GO

-- 3. Konto SQL dla TomEE z dostępem do obu baz
USE master;
GO
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'biblioteka_user')
BEGIN
    CREATE LOGIN biblioteka_user WITH PASSWORD = 'Tomcat123!', CHECK_POLICY = OFF;
END
GO

USE BibliotekaAuth;
CREATE USER biblioteka_user FOR LOGIN biblioteka_user;
ALTER ROLE db_datareader ADD MEMBER biblioteka_user;
GO

USE BibliotekaData;
CREATE USER biblioteka_user FOR LOGIN biblioteka_user;
ALTER ROLE db_datareader ADD MEMBER biblioteka_user;
ALTER ROLE db_datawriter ADD MEMBER biblioteka_user;
GO