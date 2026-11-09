# Lab 10 — Consumer idempotente con JPA e vincolo UNIQUE (90 minuti)

## Obiettivo
Ripetere lo STESSO guasto del Lab 09, ma impedire un secondo pagamento per lo stesso ordine. Imparare perché il salvataggio del pagamento e il commit dell'offset Kafka sono due operazioni differenti.

## Cosa cambia nel codice
- PaymentProcessor.process(orderId) ha una transazione DB: controlla se orderId esiste, altrimenti salva un nuovo PaymentRecord.
- Il database ha inoltre un vincolo UNIQUE(orderId): è il controllo decisivo anche in presenza di richieste concorrenti.
- Il listener invoca PaymentProcessor; il crash viene simulato DOPO il ritorno del metodo transazionale.
- Questo laboratorio usa un database DIFFERENTE: %USERPROFILE%/spring-kafka-lab/payments-lab10.mv.db. Non modifica né cancella i dati del Lab 09.
- Usa group-id payment-lab10-group.

## Esecuzione
1. Arresta entrambi i microservizi prima di cambiare branch.
2. Checkout: git switch --detach origin/lab-10-idempotency
3. Compila: .\mvnw.cmd clean verify
4. Avvia Order Service:
    .\mvnw.cmd -pl order-service spring-boot:run
5. Avvia Payment Service con crash intenzionale:
    $env:LAB_CRASH_AFTER_SAVE="true"
    .\mvnw.cmd -pl payment-service spring-boot:run
6. Solo DOPO che Payment è pronto, invia:
    Invoke-RestMethod -Method Post http://localhost:8080/orders/909/publish
7. Osserva il log LAB10 stored order=909 ... e la fine della JVM con codice 91.
8. Riavvia senza simulare il guasto:
    Remove-Item Env:LAB_CRASH_AFTER_SAVE -ErrorAction SilentlyContinue
    .\mvnw.cmd -pl payment-service spring-boot:run
9. Interroga il database:
    Invoke-RestMethod http://localhost:8082/lab/payments/909 | ConvertTo-Json

## Risultato atteso
La redelivery avviene ancora (cerca LAB10 duplicate order 909 -> skipped), ma rowCount=1. Il consumer può elaborare lo stesso evento Kafka più volte senza ripetere l'effetto del pagamento.

## Secondo esperimento: invio duplicato applicativo
Con il consumer avviato, invia DUE richieste HTTP identiche:
    1..2 | ForEach-Object { Invoke-RestMethod -Method Post http://localhost:8080/orders/910/publish }
Poi:
    Invoke-RestMethod http://localhost:8082/lab/payments/910 | ConvertTo-Json
Risultato: 1 sola riga nonostante i due record prodotti.

## Perché NON basta existsByOrderId?
Due istanze possono effettuare simultaneamente il controllo, entrambe trovare assente il pagamento e provare a inserirlo. Il vincolo UNIQUE nel DB è l'ultima barriera. Nel sistema reale servono inoltre gestione della DataIntegrityViolationException, transazioni adeguate e idempotency key verso il provider esterno: il solo database locale non rende idempotente un pagamento già effettuato da un servizio esterno.

## Test automatico
    .\mvnw.cmd -pl payment-service -am test
Il test PaymentProcessorTest salva due volte lo stesso orderId, verifica che il secondo tentativo venga saltato e che il DB contenga una sola riga.

## Diagnostica
- Se il producer esegue il POST prima della subscription, con auto-offset-reset=latest il messaggio può non essere ricevuto da un gruppo nuovo.
- Se rowCount è già 1 prima della prova, H2 è persistente e hai dati da una prova precedente. Usa un orderId nuovo per l'esperimento senza crash; per testare il crash l'ID preconfigurato è 909, quindi riparti da un database nuovo e gruppo nuovo.
- Non usare Runtime.halt in produzione. Il codice è volutamente didattico.
- Osserva che il consumer usa ancora AckMode.RECORD: il commit degli offset avviene DOPO che il metodo listener è terminato senza errori.

## Domande
Perché un indice UNIQUE protegge meglio di un controllo in memoria? Un riavvio di Kafka modifica l'unicità del database? Come coordineresti un idempotency key con Stripe/Adyen o altro PSP?
