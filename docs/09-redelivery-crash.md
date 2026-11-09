# Lab 09 — Crash del consumer e duplicazione (90 minuti)

## Obiettivo
Simulare la morte della JVM di Payment Service dopo il commit nel DB e prima del commit dell'offset. Dimostrare che at-least-once può ripetere gli effetti applicativi.

## Preparazione
- Usa il checkpoint lab-09-redelivery. Avvia Kafka e Schema Registry; crea orders-avro e orders-avro.DLT (3 partizioni ciascuno) come nel README.
- Il database H2 è persistente e si trova in %USERPROFILE%/payments-lab09.mv.db.
- Non avviare due Payment Service contemporaneamente. Il crash è abilitato solo se LAB_CRASH_AFTER_SAVE=true e orderId=909.

## Terminale 1: producer
    .\mvnw.cmd -pl order-service spring-boot:run

## Terminale 2: consumer con crash simulato
    $env:LAB_CRASH_AFTER_SAVE="true"
    .\mvnw.cmd -pl payment-service spring-boot:run

Aspetta che il consumer sia avviato, quindi nel terminale 3:
    Invoke-RestMethod -Method Post http://localhost:8080/orders/909/publish

## Osservazione
Nel log di Payment Service comparirà LAB09 DB row e poi LAB09 deliberate JVM crash. Il processo Java terminerà con codice 91; Kafka e Docker continuano a funzionare.
Il metodo saveAndFlush del repository completa la sua transazione, mentre il listener non ha avuto modo di confermare il proprio offset (AckMode.RECORD).

Riavvia Payment Service SENZA il crash:
    Remove-Item Env:LAB_CRASH_AFTER_SAVE -ErrorAction SilentlyContinue
    .\mvnw.cmd -pl payment-service spring-boot:run

Verifica tramite API REST:
    Invoke-RestMethod http://localhost:8082/lab/payments/909 | ConvertTo-Json

RISULTATO ATTESO: rowCount=2. Una riga prima del crash e una seconda riga dopo la redelivery dello stesso record Kafka.

## Analisi
1. L'ordine viene inviato su orders-avro con key=909.
2. Il consumer salva in H2, ma muore senza commit offset.
3. Al riavvio Kafka riassegna la partizione e ripresenta il record non confermato.
4. Non esiste un controllo di idempotenza sul pagamento: il DB accetta due righe.

## Se non funziona
- Se rowCount=1 verifica che il primo processo sia morto DOPO il log del salvataggio, che la pubblicazione sia avvenuta una sola volta e che il group-id sia rimasto invariato.
- Se rowCount>2 controlla i vecchi tentativi: H2 è persistente. Per ripartire da zero, FERMA le app, rimuovi il DB del Lab 09 e utilizza un nuovo group-id/topic per non rileggere eventi precedenti.
- Se il messaggio non arriva, pubblica dopo l'avvio del consumer: con auto-offset-reset=latest un nuovo gruppo non riprende i vecchi eventi.
- Il DLT del Lab 08 non impedisce duplicati causati da un crash successivo al commit del DB.
- Non usare mai Runtime.halt in produzione. In questo laboratorio agisce solo sul processo Java di Payment Service.

## Domande
Come cambierebbe il risultato se l'offset fosse committato prima del DB? Perché l'idempotenza del producer Kafka non risolve questo problema? Cosa accade se il provider HTTP addebita prima di andare in timeout?
