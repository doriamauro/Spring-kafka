# Lab 11 — Transactional Outbox e doppia scrittura DB + Kafka (120 minuti)

## Problema
Salvare l'ordine con JPA e poi inviare direttamente un evento Kafka sono due operazioni su sistemi differenti. Se il DB conferma e Kafka non è disponibile, l'ordine rimane senza evento. Invertire l'ordine non risolve: l'evento potrebbe essere pubblicato, ma la transazione DB potrebbe fallire.

## Soluzione adottata
OrderCreationService.create() apre UNA transazione H2 e salva sia StoredOrder sia OutboxEntry(published=false). Il componente OutboxRelay, indipendente dalla transazione di creazione, esegue un polling ogni 3 secondi; invia il record Kafka, attende l'ack del broker e poi segna published=true in un'altra transazione DB.

Attenzione: questo è un poller DIDATTICO a istanza singola. Non gestisce lock multiistanza, leasing dei record, backoff con jitter, blocco di record in corso, deduplicazione del producer, metrics né CDC. In produzione un outbox CDC con Debezium è spesso una scelta adatta.

## Preparazione
- Checkout lab-11-outbox, build .\mvnw.cmd clean verify.
- Topic orders-avro e orders-avro.DLT (3 partition). Kafka + Schema Registry avviati.
- Database H2 ordine: %USERPROFILE%/orders-lab11.mv.db.
- Database H2 pagamento: %USERPROFILE%/payments-lab10.mv.db.
- Avvia Payment Service per osservare i messaggi, Order Service per creare gli ordini.
- IMPORTANTISSIMO: da ora usa POST /orders/{id}/create, NON /publish. /publish è il percorso diretto introdotto nei vecchi laboratori e NON usa l'outbox.

## Scenario A — Kafka non disponibile
1. Arresta SOLO Kafka da Docker Desktop (Schema Registry può diventare temporaneamente non operativo). Lascia aperta l'applicazione Order Service.
2. Esegui:
    Invoke-RestMethod -Method Post http://localhost:8080/orders/920/create
3. L'HTTP deve rispondere 201 Created perché la transazione DB non dipende dal broker. Il relay in background segnalerà errore/tentativi.
4. Controlla:
    Invoke-RestMethod http://localhost:8080/orders/920/outbox | ConvertTo-Json
   Risultato atteso: published=false.
5. Riavvia Kafka e aspetta che Schema Registry risponda su http://localhost:8081/subjects.
6. Aspetta qualche ciclo di polling e ripeti il GET.
   Risultato atteso: published=true.
7. Con Payment Service attivo:
    Invoke-RestMethod http://localhost:8082/lab/payments/920 | ConvertTo-Json
   Risultato atteso: rowCount=1.

Nota: se Kafka non è disponibile il client può restare in attesa di metadati per vari secondi e il relay riproverà solo dopo la fine del tentativo precedente. L'API di creazione è indipendente.

## Scenario B — Kafka riceve l'evento, ma Order Service muore prima di segnare pubblicato
1. Accertati che Kafka/Registry siano attivi e Payment Service sia attivo.
2. Arresta Order Service e riavvialo in un terminale:
    $env:LAB_OUTBOX_CRASH_AFTER_ACK="true"
    .\mvnw.cmd -pl order-service spring-boot:run
3. Pubblica un NUOVO ordine (ID speciale 911):
    Invoke-RestMethod -Method Post http://localhost:8080/orders/911/create
4. Il relay invia e riceve l'ack Kafka, poi termina volutamente la JVM con exit code 92 PRIMA del markPublished. Non arresta Kafka.
5. Riavvia Order Service senza flag:
    Remove-Item Env:LAB_OUTBOX_CRASH_AFTER_ACK -ErrorAction SilentlyContinue
    .\mvnw.cmd -pl order-service spring-boot:run
6. Il relay troverà la riga ancora pending e invierà UN SECONDO messaggio Kafka.
7. Osserva il GET:
    Invoke-RestMethod http://localhost:8080/orders/911/outbox | ConvertTo-Json
   Stato finale published=true.
8. Guarda il log Payment Service: seconda consegna, ordine riconosciuto come duplicato e SKIPPED. Verifica:
    Invoke-RestMethod http://localhost:8082/lab/payments/911 | ConvertTo-Json
   rowCount=1.

## Concetto essenziale
L'Outbox risolve la perdita dell'evento causata dal dual-write, MA la pubblicazione è tipicamente at-least-once: se il producer si arresta dopo l'ack del broker e prima di confermare l'outbox, l'evento può essere ripubblicato. Perciò il consumer idempotente del Lab 10 rimane fondamentale.

## Diagnostica
- Se POST /create non funziona quando Kafka è spento, verifica di aver avviato il codice del Lab 11 e non un checkout precedente.
- Se il producer si arresta anche con ID diverso da 911 controlla LAB_OUTBOX_CRASH_AFTER_ACK.
- Se outbox rimane pending dopo il riavvio, verifica il log OUTBOX still pending, Kafka, Schema Registry e la presenza del topic.
- Se il DB possiede già l'ordine 911, HTTP /create restituirà errore per ID duplicato: ferma Order Service e rimuovi SOLO il database ordini del Lab 11 per ripetere il crash, oppure scegli un altro ID e modifica la condizione in OutboxRelay.
- Se Payment Service era spento mentre invii un evento per la prima volta e il gruppo è nuovo con auto-offset-reset=latest, il vecchio record potrebbe non essere riletto; avvia Payment Service prima della creazione.
- Non usare il flag di crash con ordine 911 in produzione.

## Test
    .\mvnw.cmd -pl order-service -am test
OrderCreationServiceTest dimostra il salvataggio simultaneo di ordine + intenzione di pubblicazione nello stesso DB, senza Kafka. Il test non prova automaticamente la consegna Kafka; quella è verificata dai passaggi sopra.
