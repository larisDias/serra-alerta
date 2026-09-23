# Serra Alerta

Monitoramento ambiental colaborativo de queimadas em São João da Boa Vista (SP), com ênfase na Serra da Paulista.
Projeto de extensão — Bacharelado em Ciência da Computação / Ciências do Ambiente (SBVCIAM), IFSP Câmpus São João da Boa Vista.

## Estrutura

| Pasta | O que é |
|---|---|
| [`landing/`](landing/) | Site de apresentação (Laravel 12 + Blade, HTML, CSS e JS puros), demonstração web do app e API REST de relatos |
| [`app-android/`](app-android/) | Protótipo do aplicativo Android nativo (Kotlin + Jetpack Compose, OSMdroid, Room, FusedLocationProvider) |

## Rodar o site (landing + demo do app)

Requisitos: PHP 8.2+ e Composer (o XAMPP já traz o PHP).

```bash
cd landing
composer install              # só na primeira vez, se a pasta vendor/ não existir
cp .env.example .env          # só se o .env não existir
php artisan key:generate      # idem
php artisan migrate:fresh --seed
php artisan serve
```

Abra:

- **http://127.0.0.1:8000** — landing page
- **http://127.0.0.1:8000/app** — demonstração do app em tela cheia (moldura de celular no desktop, tela inteira no celular)
- **http://127.0.0.1:8000/api/relatos** — API REST (GET lista, POST cria)

`php artisan migrate:fresh --seed` restaura os 8 relatos de exemplo. Rode antes da apresentação para limpar os relatos de teste.

### Dica para a apresentação

Um relato enviado pelo celular do topo da landing aparece na hora no mapa colaborativo mais abaixo.
Para mostrar a demo num celular de verdade na mesma rede Wi-Fi, rode `php artisan serve --host=0.0.0.0` e acesse
`http://IP-DO-COMPUTADOR:8000/app`. Sem HTTPS, o navegador do celular pode bloquear o GPS; nesse caso o app usa o centro do mapa, e dá para marcar o local com o botão de lápis.

## Rodar o app Android

1. Abra a pasta `app-android/` no **Android Studio** (Ladybug ou mais recente).
2. Aguarde o Gradle sincronizar (ele baixa o Gradle 8.9 e as dependências).
3. Rode em um emulador ou aparelho com Android 8.0+ (API 26).

No emulador, defina a localização em *Extended controls → Location* para um ponto de São João da Boa Vista
(ex.: `-21.9694, -46.7981`). Fora da área de cobertura o app usa o centro do mapa.
