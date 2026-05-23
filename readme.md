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
OBS PUT är tillagt nu också

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


För att köra programmet efter implementering av security:
Starta Vault:
vault server -dev -dev-root-token-id="test-token"

token = test-token

I nästa terminal:

$env:VAULT_ADDR="http://127.0.0.1:8200"
$env:VAULT_TOKEN="test-token"

vault kv put secret/library-api db-username=admin db-password=admin123

I app.properties har du detta under, men ändra vad du själv anser är relevant eller lättare för testning:

spring.application.name=library-api
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.config.import=vault://
spring.cloud.vault.uri=http://127.0.0.1:8200
spring.cloud.vault.token=test-token
spring.cloud.vault.kv.enabled=true
spring.datasource.username=${db-username}
spring.datasource.password=${db-password}

kör denna korta raden i en annan terminal än din vault server:
vault kv put secret/library-api db-username=admin db-password=admin123 app.security.user.username=user app.security.user.password=password app.security.admin.username=admin app.security.admin.password=admin123


Starta appen genom att skriva:
mvn spring-boot:run

om det inte startar är något fel ifyllt. Skriv gärna om det bråkar

När appen är igång kan swagger dokumentation nås på:
http://localhost:8080/swagger-ui/index.html

USER:
username: user
password: password

ADMIN:
username: admin
password: admin123

behörigheter:
user: get, patch, post
admin: delete
Starta appen genom att skriva: mvn spring-boot:run

När appen är igång kan swagger dokumentation nås på:

http://localhost:8080/swagger-ui/index.html

USER:
username: user
password: password

ADMIN:
username: admin
password: admin123

behörigheter:
user: get, patch, post
admin: delete


För att testa i postman fyll i admin som användarnamn och admin123 som lösenord

för att hämta ett redan skapat lån
GET http://localhost:8080/api/v1/loans/1

för att returnera redan lånad bok
PATCH  http://localhost:8080/api/v1/loans/1/return

skriv detta för att låna redan skapad bok
POST http://localhost:8080/api/v1/loans
och i body skriv detta
{
"bookId": 1,
"loanDate": "2026-05-22"
}
låna igen utan att ändra något och du får error 409

detta och mer körs i testerna redan, 37st alla ska fungera, annars skriv gärna, så jag kan rätta vad jag gjort fel. 
testnamnen är tydliga och förklara vad dom testar. Använder inte Mvc som vi gjort senaste workshoppar, använder istället
testresttemplate. Testerna täcker inte 100% men större delen av flödet. Kört tester med coverage och rapport finns redan klar.


performancetesting med utan cache, testa med index också?