# Docker
### Remove specific service from compose
docker compose down <service-name>

### Rebuild specific service in compose
docker compose up -d --no-deps --build <service-name>

### Test Sendgrid hook
ngrok http http://localhost:9000