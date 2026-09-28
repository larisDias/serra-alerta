# Serra Alerta

Monitoramento ambiental colaborativo de queimadas em São João da Boa Vista (SP), com ênfase na Serra da Paulista.
Projeto de extensão — Bacharelado em Ciência da Computação / Ciências do Ambiente (SBVCIAM), IFSP Câmpus São João da Boa Vista.

## Estrutura

| Pasta | O que é |
|---|---|
| [`landing/`](landing/) | Site de apresentação (Laravel 12 + Blade, HTML, CSS e JS puros), demonstração web do app, API REST de relatos e proxy dos focos de calor do INPE |
| [`app-android/`](app-android/) | Aplicativo Android nativo (Kotlin + Jetpack Compose/Material 3, MVVM, OSMdroid, Room, CameraX, Fused Location Provider). Detalhes em [`app-android/README.md`](app-android/README.md) |

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
- **http://127.0.0.1:8000/api/relatos** — API REST de relatos (GET lista, POST cria)
- **http://127.0.0.1:8000/api/focos?dias=7** — focos de calor do INPE/BDQueimadas na região (1 a 30 dias)

`php artisan migrate:fresh --seed` restaura os 8 relatos de exemplo. Rode antes da apresentação para limpar os relatos de teste.

A demo web reproduz as telas do app: introdução, mapa com relatos e focos do INPE, filtros por período e categoria,
registro (foto obrigatória, categoria, localização com ajuste no mapa, descrição), confirmação, detalhes, foco oficial,
lista de relatos, prevenção, emergência e sobre. As categorias são as mesmas do app: queima controlada, queimada
irregular, incêndio florestal e fumaça não identificada.

Os focos de calor vêm dos CSVs diários públicos do INPE (`dataserver-coids.inpe.br`), filtrados para o mesmo recorte
do app e guardados em cache pelo Laravel. Sem internet, a camada aparece como indisponível e o resto funciona normalmente.

### Dica para a apresentação

Um relato enviado pelo celular do topo da landing aparece na hora no mapa colaborativo mais abaixo.
Para mostrar a demo num celular de verdade na mesma rede Wi-Fi, rode `php artisan serve --host=0.0.0.0` e acesse
`http://IP-DO-COMPUTADOR:8000/app`. Sem HTTPS, o navegador do celular pode bloquear o GPS; nesse caso use
**Ajustar no mapa** para posicionar o pino.

## Rodar o app Android

1. Abra a pasta `app-android/` no **Android Studio** (versão recente, com suporte ao AGP 9).
2. O projeto usa o Gradle Wrapper (Gradle 9.6) e o **JDK 25** como toolchain do daemon. Se o Gradle não conseguir
   baixar o JDK sozinho, instale-o (por exemplo com `mise use --global java@25.0.2`) e aponte o Android Studio para ele
   em *Settings → Build Tools → Gradle → Gradle JDK*.
3. Rode em um emulador ou aparelho com Android 7.0+ (API 24).

Pela linha de comando: `./gradlew :app:testDebugUnitTest :app:assembleDebug` dentro de `app-android/`; o APK sai em
`app-android/app/build/outputs/apk/debug/app-debug.apk`.

No emulador, defina a localização em *Extended controls → Location* para um ponto de São João da Boa Vista
(ex.: `-21.9694, -46.7981`).

## Pendências (para o grupo)

- **Focos do INPE no app Android:** o endpoint usado em `app-android/app/src/main/java/br/ifsp/serraalerta/sync/FocosOficiaisDataSource.kt`
  (`queimadas.dgi.inpe.br/.../focos.json`) passou a responder 404, então o app mostra "Focos INPE indisponíveis".
  Uma fonte que funciona são os CSVs diários públicos:
  `https://dataserver-coids.inpe.br/queimadas/queimadas/focos/csv/diario/Brasil/focos_diario_br_AAAAMMDD.csv`
  (colunas `id, lat, lon, data_hora_gmt, satelite, municipio, ..., frp`). O site já usa essa fonte em
  `landing/app/Http/Controllers/FocoController.php`, que pode servir de referência para o parser em Kotlin.
- **APK:** gerar com JDK 25 (`./gradlew :app:assembleDebug` em `app-android/`) e publicar, por exemplo em uma
  release do GitHub, para colocar o link de download na landing.
- **Demo web x app:** a demo aceita 1 foto por relato (o app aceita 3), não tem exclusão de relato nem a tela de
  configurações.
