# Lab 12 — Kafka transactions, read_committed e test (90 minuti)

## Obiettivo
Dimostrare atomicità di due invii in una transazione KAFKA e il limite della garanzia rispetto al database H2. Il producer transazionale è separato dal KafkaTemplate usato per outbox/direct publish.

## Setup
Checkout lab-12-kafka-transactions. Esegui .\mvnw.cmd clean verify e avvia Kafka, Schema Registry, Order Service (8080) e Payment Service (8082).

Crea topic orders-avro-tx (3 partizioni) con:
    docker compose -f docker/compose.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --create --if-not-exists --topic orders-avro-tx --partitions 3 --replication-factor 1 --bootstrap-server kafka:29092

Se Kafka è stato avviato con docker run, usa docker exec kafka e bootstrap-server localhost:9092 dentro il container.

## Scenario A — Rollback
In PowerShell:
    try { Invoke-RestMethod -Method Post "http://localhost:8080/lab/kafka-transactions/940?fail=true" } catch { Write-Host "HTTP 500 atteso" }
Il servizio invia il primo evento, poi solleva un errore intenzionale PRIMA del commit.
    Invoke-RestMethod http://localhost:8082/lab/payments/940 | ConvertTo-Json
Risultato atteso: rowCount=0. L'evento abortito non è visibile al consumer, configurato con isolation.level=read_committed.

## Scenario B — Commit
    Invoke-RestMethod -Method Post "http://localhost:8080/lab/kafka-transactions/950?fail=false"
Il producer invia due eventi (950 e 951) e conferma la transazione.
    Invoke-RestMethod http://localhost:8082/lab/payments/950 | ConvertTo-Json
    Invoke-RestMethod http://localhost:8082/lab/payments/951 | ConvertTo-Json
Atteso: una riga per ciascun ordine. Usa ID nuovi per ripetere la prova.

## Cosa si impara
executeInTransaction rende atomici gli invii Kafka nel blocco, ma NON il DB H2. Non esiste una transazione distribuita ACID tra Kafka, DB e provider HTTP. In un consumer read-process-write con transazione Kafka si possono includere gli offset e i nuovi record nella stessa transazione Kafka; non anche l'addebito esterno.
Il log Kafka può contenere record abortiti, ma i consumer read_committed non li ricevono. Il prefisso transactional.id deve essere univoco per istanza.

## Test automatici senza broker
    .\mvnw.cmd clean verify
PaymentProcessorTest prova l'unicità del pagamento; OrderCreationServiceTest prova ordine+outbox; AvroSchemaCompatibilityTest controlla V1/V2. Sono test unitari/slice H2 senza broker Kafka: NON sostituiscono il test di integrazione end-to-end svolto negli scenari A/B.

## Piccola prova di concurrency
Ferma Payment Service, poi avvialo con:
    .\mvnw.cmd -pl payment-service spring-boot:run "-Dspring-boot.run.arguments=--spring.kafka.listener.concurrency=3"
Pubblica molti eventi con orderId nuovi. Guarda thread, partition e offset nel log LAB12. A parità di consumer group, le partizioni vengono distribuite sui consumer; più thread del numero di partizioni non garantiscono più parallelismo. Il listener ascolta due topic, quindi la distribuzione reale dipende da entrambi.

## Diagnostica
- Se la prima prova salva il pagamento 940, assicurati di essere in lab-12, di usare orders-avro-tx e che il consumer abbia isolation.level=read_committed.
- Se non arriva l'evento 950, controlla che il consumer fosse già avviato: il gruppo nuovo usa auto-offset-reset=latest.
- Se il topic non esiste, crealo esplicitamente; non affidarti alla creazione automatica.
- Se il test H2 fallisce per dati preesistenti, è un problema di configurazione del DB di test; i test usano un database embedded, non il DB persistente della giornata.
