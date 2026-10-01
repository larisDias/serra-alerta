# Serra Alerta

Monitoramento ambiental colaborativo de queimadas em São João da Boa Vista (SP), com ênfase na Serra da Paulista.
Projeto de extensão — Bacharelado em Ciência da Computação / Ciências do Ambiente (SBVCIAM), IFSP Câmpus São João da Boa Vista.

**Site no ar:** https://larisdias.github.io/serra-alerta/ (demonstração do app em [`/app`](https://larisdias.github.io/serra-alerta/app))

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
registro (de 1 a 3 fotos, categoria, localização com ajuste no mapa, descrição), confirmação, detalhes, foco oficial,
lista de relatos, prevenção, emergência, configurações e sobre. Relatos enviados pelo navegador podem ser
excluídos (o servidor devolve um token de exclusão que fica guardado no `localStorage`). As categorias são as mesmas do app: queima controlada, queimada
irregular, incêndio florestal e fumaça não identificada.

Os focos de calor vêm dos CSVs diários públicos do INPE (`dataserver-coids.inpe.br`), filtrados para o mesmo recorte
do app (que lê os CSVs dos últimos 7 dias direto no aparelho) e guardados em cache pelo Laravel no site. Sem internet, a camada aparece como indisponível e o resto funciona normalmente.

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

### Publicar uma nova versão do APK

Crie e envie uma tag `v*` (ex.: `git tag v1.0.1 && git push origin v1.0.1`). O workflow
`.github/workflows/release-apk.yml` gera o APK e o anexa a uma release do GitHub. Os botões **Baixar para Android** da
landing apontam para `/releases/latest`, então passam a oferecer a versão nova sem mudar o site.

No emulador, defina a localização em *Extended controls → Location* para um ponto de São João da Boa Vista
(ex.: `-21.9694, -46.7981`).

## Pendências (para o grupo)

- **Testar no aparelho:** os testes automáticos passam (`mise run site:test` e `mise run android:test`), mas a leitura
  dos focos do INPE no app e a interface nova da demo (galeria de fotos, exclusão, configurações) ainda não foram
  conferidas na tela.

## Rodar sem instalar nada (mise)

O `mise.toml` da raiz traz JDK 25, PHP e composer, e tarefas prontas: `mise run site:setup`, `site:test`, `site:serve`,
`android:sdk` (baixa o SDK do Android só com as ferramentas de linha de comando, sem o Android Studio),
`android:test` e `android:apk`. O PHP pré-compilado do `mise.toml` é para Linux x64/WSL; em Windows/macOS use o PHP local.
