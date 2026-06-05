# K8s Component Inventory

Ovo je trenutna lista komponenti na osnovu `setup/docker-compose.yml` i servisnih konfiguracija. Namenjena je kao priprema za deployment na fakultetski Kubernetes klaster.

## 1. Obavezne aplikativne komponente

### Ulazne komponente

- `frontend` - Angular aplikacija, build kontekst pokazuje na poseban frontend repo `../../Banka-1-Frontend-frontendNew`
- `api-gateway` - Nginx gateway ispred svih backend servisa

### Backend servisi koji se trenutno deploy-uju

- `notification-service`
- `user-service`
- `banking-core-service`
- `market-service`
- `trading-service`
- `credit-service`
- `saga-orchestrator-service`
- `interbank-service`

Napomena: u repou postoje i stariji moduli (`employee-service`, `client-service`, `account-service`, `card-service`, `transaction-service`, `transfer-service`, `verification-service`, `exchange-service`, `stock-service`, `order-service`), ali se prema aktuelnom `docker-compose` deployment-u ne dižu kao zasebni runtime servisi, već su funkcionalno konsolidovani u servise gore.

## 2. Obavezna zajednička infrastruktura

- `postgres` - jedan deljeni PostgreSQL za više servisa
- `rabbitmq` - asinhrona komunikacija i eventovi
- `redis` - keš za `market-service`

### Baze unutar deljenog PostgreSQL-a

- `notification_db`
- `user_service`
- `banking_core`
- `market_service`
- `trading`
- `credit_db`
- `saga_db`
- `interbank_service`

## 3. Obavezni konfiguracioni podaci i secret-i

- `JWT_SECRET`
- PostgreSQL kredencijali
- RabbitMQ kredencijali
- CORS origin-i za frontend domen
- `LIQUIBASE_CONTEXTS`

### Dodatni obavezni ili praktično obavezni secret-i po servisima

- `notification-service`
  - SMTP parametri: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`
- `market-service`
  - market data API ključevi: `TWELVE_DATA_API_KEY`, `ALPHA_VANTAGE_API_KEY`
- `interbank-service`
  - partner routing i token konfiguracija: `BANKA1_ROUTING_NUMBER`, `BANKA2_ROUTING_NUMBER`, `BANKA2_BASE_URL`, `BANKA2_INBOUND_TOKEN`, `BANKA2_OUTBOUND_TOKEN`

### Opcioni secret-i

- Firebase credentials JSON za push notifikacije
  - `FIREBASE_CREDENTIALS_HOST_PATH`
  - `FIREBASE_CREDENTIALS_PATH`
- Grafana admin password
- Discord bot token i Discord user ID za alerting

## 4. Opciona observability infrastruktura

Ovo nije strogo neophodno da aplikacija funkcioniše, ali postoji u aktuelnom setup-u:

- `otel-collector`
- `jaeger`
- `prometheus`
- `alertmanager`
- `discord-alert-bot`
- `loki`
- `promtail`
- `grafana`

## 5. Opciona analytics komponenta

U repou postoji i poseban `analytics-spark-job`, ali nije deo glavnog `docker-compose` runtime-a.

Za njega treba:

- Spark Operator na klasteru
- Spark Operator CRD-ovi
- Kubernetes secret `banka-analytics-db`

Ako profesor traži samo šta je potrebno da glavna aplikacija radi, ovu komponentu verovatno treba navesti kao opcionu / van osnovnog deployment-a.

## 6. Ukratko

> Za rad nase aplikacije na Kubernetes-u potrebni su nam:   
> frontend, API gateway i 8 backend servisa (`notification-service`, `user-service`, `banking-core-service`, `market-service`, `trading-service`, `credit-service`, `saga-orchestrator-service`, `interbank-service`).      
> Od zajednicke infrastrukture potrebni su jedan deljeni PostgreSQL (sa vise baza: `notification_db`, `user_service`, `banking_core`, `market_service`, `trading`, `credit_db`, `saga_db`, `interbank_service`), RabbitMQ i Redis.          
> Pored toga su nam potrebni application secret-i i konfiguracija: JWT secret, DB/RabbitMQ kredencijali, SMTP kredencijali za email notifikacije, market data API kljucevi i interbank partner token/config.            
> Imamo i observability stack (`otel-collector`, `jaeger`, `prometheus`, `alertmanager`, `loki`, `promtail`, `grafana`) koji je opcion za osnovni rad aplikacije, ali postoji u nasem setup-u.          
    
## 7. Operativne napomene za k8s termin

- Frontend je u posebnom repou i treba ga imati spremnog zajedno sa backend-om.
- Trenutni local setup koristi jedan PostgreSQL container sa vise logickih baza, pa na Kubernetes-u to treba mapirati na isti nacin.
- `notification-service` bez SMTP naloga nece moci da salje email notifikacije.
- `market-service` bez market data API kljuceva moze imati ogranicenu ili neispravnu integraciju prema spoljnim izvorima podataka.
- Ako Firebase credentials nisu postavljeni, push notifikacije mogu ostati iskljucene.
- Za produkcioni deployment treba izbeci `LIQUIBASE_CONTEXTS=dev` i proveriti sve domene/CORS vrednosti.
