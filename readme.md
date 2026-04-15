Library-API

Huvudfunktioner:
Skapa och hämta böcker
Skapa och hämta författare
Lista böcker för en författare
Skapa lån
Returnera lån
Förhindra dubbla aktiva lån för samma bok
API-versionering med v1 och v2(dock använde jag inte v2 efter skapandet. det behövs inte en attribut för available iom kan kolla om bok är tillgänglig eller ej på enklare sätt)
v2 för böcker innehåller information om tillgänglighet
Skydd mot race conditions vid skapande av lån

Projektstruktur:
controller: REST-endpoints
service: affärslogik
repository: databasåtkomst
dto: request/responseklasser
model: JPA-entiteter
exception: egna exceptions och global exception handler

API-vägar:
/api/v1/books
/api/v1/authors
/api/v1/loans
/api/v2/books

Exempel på endpoints:
POST /api/v1/books
GET /api/v1/books
GET /api/v1/books/{id}
POST /api/v1/authors
GET /api/v1/authors/{id}
GET /api/v1/authors/{id}/books
POST /api/v1/loans
GET /api/v1/loans
PATCH /api/v1/loans/{id}/return
GET /api/v2/books
Så startar du applikationen:

Kör appen:
./mvnw spring-boot:run
Kör tester:
./mvnw test

Swagger UI(appen måste vara igång för detta, men täcker endast delar av API:et):
http://localhost:8080/swagger-ui/index.html

H2 Console(appen måste vara igång för detta):
http://localhost:8080/h2-console

H2-inställningar:

JDBC URL: jdbc:h2:mem:testdb
Användarnamn: sa
Lösenord: lämnas tomt
Övrigt:

Applikationen använder H2-databas
Data skapas om vid varje uppstart eftersom ddl-auto är satt till create-drop
Skapande av lån skyddas med transaktioner och pessimistic locking för att undvika race conditions
Tillgänglighet för böcker i v2 beräknas baserat på om det finns ett aktivt lån eller inte
Alla integrationstester passerar. För manuell testning i terminal eller via postman skapas ett mindre dataset vid uppstart.