# VolleyBar

VolleyBar é um minigame desktop original de gerenciamento de vôlei e progressão RPG. Você escala seis entre doze atletas, disputa partidas rápidas em melhor de três sets e investe as recompensas no elenco. A identidade visual usa formas geométricas, sprites desenhados em Compose Canvas e uma paleta neon retrô; nenhum asset de outro jogo é utilizado.

## Tecnologias

- Kotlin Multiplatform 2.1.20, target JVM Desktop
- Compose Multiplatform 1.8.2 e Gradle Kotlin DSL
- Coroutines 1.10.2, `StateFlow` e Kotlin Serialization JSON 1.8.1
- Testes de domínio com `kotlin.test`

## Arquitetura

O projeto segue Clean Architecture em um módulo KMP. `domain` contém modelos imutáveis, contratos e regras puras; `presentation` contém a Store de fluxo unidirecional, estados/eventos e UI Compose; `desktopMain` contém inicialização e persistência de filesystem. A aleatoriedade é injetada via `RandomProvider`, tornando a simulação determinística em testes.

```text
src/commonMain/kotlin/com/guilherme/volleybar/
├── domain/{model,repository,usecase}
└── presentation/{theme,ui}
src/commonTest/kotlin/com/guilherme/volleybar/
└── DomainRulesTest.kt
src/desktopMain/kotlin/com/guilherme/volleybar/
├── application/Main.kt
└── persistence/JsonGameRepository.kt
```

## Executar e testar

Requer JDK 17 ou mais recente. No Windows, PowerShell ou terminal:

```bash
gradle run
gradle desktopTest
```

Para criar o instalador Windows (a tarefa precisa ser executada no Windows):

```bash
gradle packageMsi
# ou
gradle packageExe
```

Os pacotes usam o nome **VolleyBar** e namespace `com.guilherme.volleybar`. O modo compacto reduz a janela, mantém campo/placar/controles visíveis e ativa `alwaysOnTop`; o botão **Expandir** restaura a janela.

## Partidas e progressão

Uma formação válida tem 1 levantador, 1 oposto, 2 ponteiros, 1 central e 1 líbero. Cada rally combina saque, recepção/defesa, levantamento, ataque, bloqueio, velocidade, energia e dificuldade rival, com uma parcela limitada de aleatoriedade. Sets vão até 15, exigem dois pontos de diferença, e dois sets vencem a partida.

Vitórias concedem 100 XP aos titulares, 40 às reservas e 3 pontos do time; derrotas concedem 60/25 XP e 1 ponto. O MVP recebe 30 XP. A exigência é `100 + (nível - 1) × 50`; excedentes são preservados e cada nível concede um ponto individual, energia máxima e resistência. Pontos individuais melhoram atributos até 100.

## Save e segurança

O progresso é salvo atomicamente em `~/.volleybar/save.json`, incluindo elenco, escalação, atributos, XP, energia, pontos, histórico e partida atual. O save inicial determinístico possui exatamente 12 atletas. Se o JSON estiver corrompido, ele é movido para `save.corrupted.json`, um novo jogo é criado e erros técnicos são registrados no console.

## Roadmap

- Modo carreira.
- Contratação de jogadores.
- Raridade de atletas.
- Habilidades especiais.
- Lesões e recuperação.
- Treinos.
- Customização de uniforme.
- Campeonatos.
- Multiplayer assíncrono.
- Versão Android.
- Sons e músicas originais.
- Conquistas.

Capturas e GIFs da experiência poderão ser adicionados futuramente.
