# Serra Alerta

Aplicativo Android nativo para registro colaborativo de fumaça e fogo em São João da Boa Vista e região da Serra da Paulista.

## Stack

- Kotlin + Jetpack Compose / Material 3
- MVVM com separação `ui`, `domain` e `data`
- OpenStreetMap via OSMdroid
- Fused Location Provider para localização sob demanda
- Room/SQLite para relatos e cache de focos oficiais
- CameraX para captura de fotos
- Android Sharesheet via `FileProvider`

## Estrutura

- `app/src/main/java/br/ifsp/serraalerta/data`: Room, DAOs e implementações de repositório
- `app/src/main/java/br/ifsp/serraalerta/domain`: entidades de domínio, contratos e casos de uso
- `app/src/main/java/br/ifsp/serraalerta/location`: acesso pontual ao Fused Location Provider
- `app/src/main/java/br/ifsp/serraalerta/sync`: consulta somente leitura ao cache público INPE/BDQueimadas
- `app/src/main/java/br/ifsp/serraalerta/ui`: navegação, tema, componentes e telas Compose

## Build

O projeto usa o Gradle Wrapper e o JDK 25. O JDK pode ser instalado e selecionado globalmente pelo `mise`:

```bash
mise use --global java@25.0.2
mise exec java@25.0.2 -- ./gradlew :app:testDebugUnitTest :app:assembleDebug
```

O APK debug é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Comportamento offline

O fluxo de registro não depende de internet: a foto é salva em `filesDir/ocorrencias`, e a ocorrência é inserida no Room antes da confirmação. Sem permissão de localização, o usuário pode posicionar o pino manualmente no mapa. Os focos oficiais são somente leitura, ficam em cache local e são atualizados silenciosamente quando o mapa é aberto com conectividade.

## Permissões

- Câmera: solicitada apenas ao abrir o fluxo de captura.
- Localização: solicitada apenas no fluxo de registro; não há coleta em segundo plano.
- Internet/rede: usada para tiles do OSM e atualização pública dos focos oficiais.

Nenhum login, cadastro ou dado pessoal identificável é exigido.
