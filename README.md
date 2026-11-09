# Spring Kafka Lab

Laboratori cumulativi Java 21, Spring Boot 3.5 e Kafka. Due moduli Maven indipendenti (`order-service` producer e `payment-service` consumer), Kafka e Confluent Schema Registry con Docker.

## Avvio

1. Installa Java 21 (JDK) e Docker Desktop. **Non occorre installare Maven**: il progetto include Maven Wrapper.
2. Ferma eventuali container Kafka/Schema Registry che stanno già usando le porte 9092/8081.
3. `docker compose -f docker/compose.yaml up -d`
4. Crea i topic (comandi sotto).
5. Su Windows, da VS Code / PowerShell: `.\\mvnw.cmd clean verify`
6. In due terminali: `.\\mvnw.cmd -pl order-service spring-boot:run` e `.\\mvnw.cmd -pl payment-service spring-boot:run`.
7. `POST http://localhost:8080/orders/123/publish`.

## Topic

```powershell
docker compose -f docker/compose.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic orders-avro --partitions 3 --replication-factor 1 --bootstrap-server kafka:29092
docker compose -f docker/compose.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic orders-avro.DLT --partitions 3 --replication-factor 1 --bootstrap-server kafka:29092
```

Schema Registry: `http://localhost:8081/subjects`.

## Checkpoint

`lab-00` setup; `lab-01` producer String; `lab-02` consumer; `lab-03` factories/concurrency; `lab-04` properties; `lab-05` JSON; `lab-06` Avro; `lab-07` schema evolution; `lab-08` error handling e DLT.

Ogni lezione è un commit distinto nella storia di `main`. Le versioni precedenti sono ripristinabili con `git switch --detach <SHA>`. Vedi `docs/` per istruzioni e considerazioni didattiche.

## Sicurezza e limiti

Il pagamento è **simulato**. Nessun provider di pagamento esterno o segreto reale. Kafka in PLAINTEXT e replica singola servono soltanto per studio locale. Non usare questa configurazione in produzione. Prima di distribuire un servizio reale occorrono autenticazione, gestione dei segreti, persistenza adeguata, idempotenza applicativa e test di carico.

## Maven Wrapper e VS Code

Apri **la radice del repository** in VS Code. Dal terminale PowerShell:

```powershell
.\mvnw.cmd -version
.\mvnw.cmd clean verify
.\mvnw.cmd -pl order-service spring-boot:run
# In un secondo terminale:
.\mvnw.cmd -pl payment-service spring-boot:run
```

Usa Java 21. Il wrapper `mvnw.cmd` scarica Maven 3.9.16 al primo utilizzo (Internet richiesto) e lo riutilizza successivamente. Su Linux/macOS usa `./mvnw`.

Se hai già clonato il repository, esegui `git pull` sul branch `main` per ottenere i nuovi file.

## Laboratori avanzati per una giornata

I checkpoint a partire da lab-09 aggiungono esperimenti di guasto con database H2 persistenti. Segui le guide dettagliate in docs/09-redelivery-crash.md e successive. Payment Service espone una API diagnostica sulla porta 8082.
