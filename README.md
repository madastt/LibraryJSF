# LibraryJSF — Library Management System (Java EE 8 / JSF 2.3)

A JavaServer Faces (JSF 2.3) web application deployed on **Apache TomEE Plume 8.0.12** demonstrating enterprise Java EE features: declarative container-managed security backed by an external MSSQL database, HTTPS with a custom keystore, cache/cookie blocking, CDI bean scopes with dependency injection, internationalization (PL/EN), navigation rules, non-AJAX & AJAX collection tables, and declarative JNDI database persistence using the **DAO + Transfer Object (TO)** pattern.

## Architecture Overview

- **Security & Auth DB (`BibliotekaAuth`):** Container-managed `FORM` authentication via Tomcat `DataSourceRealm` (`server.xml` + `META-INF/context.xml`), role-based access control (`admin`, `user`), forced HTTPS (`CONFIDENTIAL` transport guarantee with custom SSL keystore), and cookie/cache blocking (`NoCacheCookieFilter` + URL session tracking).
- **Presentation Layer (JSF / Facelets):**
    - `views/mainWindow.xhtml` — dashboard displaying data from all bean scopes, language switcher (`PL`/`EN`), and static/dynamic/conditional navigation.
    - `views/collectionTable.xhtml` — full CRUD table on an in-memory collection without AJAX (full form submission).
    - `views/ajaxTable.xhtml` — full CRUD table with inline editing and asynchronous sorting via `<f:ajax>`.
    - `views/dbBooks.xhtml` — full CRUD table persisted to external MSSQL database (`BibliotekaData`) via `BookDAO` and `BookTO`.
    - `logonWindow.xhtml`, `logonError.xhtml`, `logoutWindow.xhtml` — declarative authentication & session invalidation views.
- **Managed Beans (`pl.biblioteka.beans`):**
    - `AppInfoXmlBean` — XML-configured and initialized via `<managed-bean>` / `<managed-property>` in `faces-config.xml`.
    - `LibraryApplicationBean` (`@ApplicationScoped`) — global application state, action counter, and database book count via `BookDAO`/`BookTO`.
    - `LibrarySessionBean` (`@SessionScoped`) — injects `LibraryApplicationBean` (`@Inject`), manages user locale, navigation state, logout, in-memory collections, and database CRUD operations.
    - `LibraryViewBean` (`@ViewScoped`) — injects both application and session beans to demonstrate view-scoped state collaborating with wider scopes.
- **Persistence Layer (`pl.biblioteka.dao`, `pl.biblioteka.to`):**
    - `BookTO` — Transfer Object used exclusively to exchange book records between managed beans and the DAO layer.
    - `BookDAO` — declarative JNDI `DataSource` lookup (`java:comp/env/jdbc/bibliotekaData`) executing parameterized SQL queries against `BibliotekaData.dbo.t_ksiazki`.

## Tech Stack

- **Java:** OpenJDK 17 (compiled with target/source `1.8`)
- **Application Server:** Apache TomEE Plume 8.0.12 (Java EE 8 / Tomcat 9)
- **Web Framework:** JSF 2.3 (Mojarra), CDI (OpenWebBeans), PrimeFaces 11.0.0
- **Database:** Microsoft SQL Server (two separate databases: `BibliotekaAuth` & `BibliotekaData`, JDBC driver `sqljdbc42.jar`)
- **Build Tool:** Apache Maven

## Project Structure

```text
├── config/
│   ├── init-db.sql                  # Creates BibliotekaAuth, BibliotekaData & SQL login
│   └── biblioteka.keystore          # Self-signed SSL keystore for HTTPS (CN=localhost)
├── src/main/java/pl/biblioteka/
│   ├── beans/                       # AppInfoXmlBean, Application/Session/View CDI beans
│   ├── dao/                         # BookDAO (declarative JNDI + JDBC)
│   ├── filters/                     # NoCacheCookieFilter (blocks browser cache & cookies)
│   └── to/                          # BookTO (Transfer Object)
├── src/main/resources/
│   ├── messages.properties          # Default i18n bundle (EN)
│   ├── messages_en.properties       # English i18n bundle
│   └── messages_pl.properties       # Polish i18n bundle
└── src/main/webapp/
    ├── META-INF/context.xml         # DataSourceRealm, cookies="false", ResourceLink
    ├── WEB-INF/web.xml              # Security constraints, HTTPS, filter, FacesServlet
    ├── WEB-INF/faces-config.xml     # i18n, XML-managed bean, static & conditional navigation
    ├── WEB-INF/beans.xml            # CDI discovery configuration
    ├── resources/css/styles.css     # Application stylesheet
    ├── views/                       # Protected JSF views (main, collection, ajax, db)
    ├── logonWindow.xhtml
    ├── logonError.xhtml
    └── logoutWindow.xhtml
```

## Setup & Deployment

### 1. Database Setup (Microsoft SQL Server)
Ensure SQL Server has **Mixed Mode Authentication** and **TCP/IP on port `1433`** enabled (or run MSSQL via Docker):

```powershell
docker run -d --name mssql-biblioteka `
  -e "ACCEPT_EULA=Y" `
  -e "MSSQL_SA_PASSWORD=StrongPassword123!" `
  -p 1433:1433 `
  mcr.microsoft.com/mssql/server:2022-latest
```

Connect in SSMS (`localhost,1433`, Trust Server Certificate checked) and execute `config/init-db.sql`. This creates:
- `BibliotekaAuth` (`users`, `user_roles`)
- `BibliotekaData` (`t_ksiazki`)
- SQL login `biblioteka_user` (`Tomcat123!`)

### 2. TomEE Configuration
1. Place the MSSQL JDBC driver (`sqljdbc42.jar`) in `<TOMEE_HOME>/lib/`.
2. Copy `config/biblioteka.keystore` to `<TOMEE_HOME>/conf/biblioteka.keystore`.
3. In `<TOMEE_HOME>/conf/server.xml`, add both global JNDI data sources inside `<GlobalNamingResources>`:

```xml
<Resource auth="Container"
          driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          maxTotal="50" maxIdle="20" maxWaitMillis="10000"
          name="jdbc/bibliotekaAuth"
          type="javax.sql.DataSource"
          url="jdbc:sqlserver://localhost:1433;databaseName=BibliotekaAuth;encrypt=false"
          username="biblioteka_user"
          password="Tomcat123!" />

<Resource auth="Container"
          driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
          maxTotal="50" maxIdle="20" maxWaitMillis="10000"
          name="jdbc/bibliotekaData"
          type="javax.sql.DataSource"
          url="jdbc:sqlserver://localhost:1433;databaseName=BibliotekaData;encrypt=false"
          username="biblioteka_user"
          password="Tomcat123!" />
```

4. Configure the HTTPS connector on port `8443` in `<TOMEE_HOME>/conf/server.xml`:

```xml
<Connector protocol="org.apache.coyote.http11.Http11NioProtocol"
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           keystoreFile="conf/biblioteka.keystore" keystorePass="tomcat123"
           keyAlias="bibliotekaCert"
           clientAuth="false" sslProtocol="TLS"/>
```

### 3. Build & Run
```powershell
mvn clean package
```

Copy `target/ProjektBibliotekaStarzykMarcin-1.0-SNAPSHOT.war` to `<TOMEE_HOME>/webapps/` and start TomEE with JDK 17:

```powershell
$env:JAVA_HOME="C:\path\to\jdk-17"
cd <TOMEE_HOME>\bin
.\catalina.bat run
```

Open `https://localhost:8443/ProjektBibliotekaStarzykMarcin-1.0-SNAPSHOT/` (accept the self-signed certificate warning).

### Test Accounts
| Username | Password | Roles |
|---|---|---|
| `admin` | `admin` | `admin`, `user` |
| `czytelnik` | `czytelnik` | `user` |