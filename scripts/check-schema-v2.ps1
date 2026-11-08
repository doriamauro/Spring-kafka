$ErrorActionPreference = "Stop"
$registry = "http://localhost:8081"
$subject = "orders-avro-value"
$schema = Get-Content "$PSScriptRoot/../schemas/OrderEvent-v2.avsc" -Raw
$body = @{ schemaType = "AVRO"; schema = $schema } | ConvertTo-Json -Compress
Write-Host "Checking backward compatibility against $subject..."
Invoke-RestMethod -Method Post -Uri "$registry/compatibility/subjects/$subject/versions/latest?verbose=true" -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
