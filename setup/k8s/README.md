# Kubernetes manifesti

Ovaj folder sadrzi osnovni Kubernetes deployment za aktuelni runtime ovog projekta:

- shared `postgres` sa vise logickih baza
- `redis`
- `rabbitmq`
- `notification-service`
- `user-service`
- `banking-core-service`
- `market-service`
- `trading-service`
- `credit-service`
- `saga-orchestrator-service`
- `interbank-service`
- `api-gateway`

## Struktura

- `base/` - namespace
- `base/route.yaml` - spoljasnji pristup kroz fakultetski Gateway API
- `config/` - shared `ConfigMap`
- `secrets/` - shared `Secret` sa dev placeholder vrednostima
- `infra/` - postgres, redis i rabbitmq
- `apps/` - po jedan YAML po aplikativnom servisu; `api-gateway.yaml` u sebi sadrzi i nginx konfiguraciju

## Primena

```bash
kubectl apply -k setup/k8s
```

## Bitne napomene

1. Image reference vrednosti su placeholder-i oblika `banka/<service>:latest`. Pre deploy-a ih uskladite sa vasim registry/tag naming-om.
2. `secrets/app-secrets.yaml` trenutno nosi dev vrednosti i placeholder-e. Obavezno promenite makar `JWT_SECRET`, API kljuceve i mail kredencijale.
3. `api-gateway` je interni `ClusterIP` servis; spoljasnji pristup ide kroz `base/route.yaml`.
4. `base/route.yaml` je dodat za Envoy Gateway i prosledjuje sav saobracaj sa `banka-1.k8s.elab.rs` na interni `api-gateway` servis.
5. `db.yaml` prati trenutni projekat: jedan PostgreSQL pod sa vise baza koje svi servisi koriste.
6. Ovo ne ukljucuje observability stack (`otel`, `prometheus`, `grafana`, `loki`) jer nije neophodan za osnovni rad aplikacije.
