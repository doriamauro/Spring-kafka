# Spring Kafka Lab

Percorso pratico Java 21 / Spring Boot 3.5 / Spring Kafka: due applicazioni indipendenti in un monorepo Maven.

## Prerequisiti
Java 21 (JDK), Docker Desktop. Maven si scarica tramite Maven Wrapper: non installarlo globalmente. Tutto il codice usa i topic Kafka del laboratorio.

## Avvio
```powershell
docker compose -f docker/compose.yaml up -d
.\mvnw.cmd clean verify
.\mvnw.cmd -pl order-service spring-boot:run
.\mvnw.cmd -pl payment-service spring-boot:run
```
Avvia i due comandi Maven in terminali separati. Kafka: localhost:9092; Schema Registry: http://localhost:8081.

**Attenzione:** se stai già usando container Kafka/Schema Registry in Docker Desktop sulle porte 9092 e 8081, fermali prima di avviare Compose. Non eseguire due ambienti sulle stesse porte.

## Percorso versionato
Ogni commit con prefisso `lab-NN` rappresenta la conclusione di una lezione. Le lezioni sono cumulative: `main` contiene l'ultima versione; i commit storici permettono di recuperare i laboratori precedenti con `git switch --detach <SHA>`.

Gli schemi e i formati cambiano tra le lezioni: non usare messaggi di topic vecchi con nuovi deserializer. Vedi `docs/`.

Il sistema di pagamento è simulato: nessuna chiamata a provider reale e nessuna gestione di denaro.
