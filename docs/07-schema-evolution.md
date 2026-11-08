# Lab 07 — Evoluzione del contratto Avro

V1 = OrderEvent senza `currency`. V2 aggiunge `currency` con default `EUR`.

Prima di sostituire V1 nel producer verifica che il nuovo reader possa leggere i vecchi record. Il test usa `scripts/check-schema-v2.ps1` e richiede che il subject `orders-avro-value` contenga già la V1 (dal precedente checkpoint).

**Deployment realistico:** aggiorna Payment Service prima di Order Service quando la modifica è backward-compatible. Il file V2 attivo in questo commit serve a mostrare lo stato finale; per ricreare la transizione usa il commit `lab-06`.

Schema Registry verifica compatibilità strutturale, non la semantica. Un default di valuta sbagliato può essere pericoloso nei pagamenti. Evita di assumere che tutti i vecchi ordini fossero EUR senza una regola di dominio verificata.

Non collocare contemporaneamente V1 e V2 in src/main/avro: produrrebbero classi generate con lo stesso nome.
