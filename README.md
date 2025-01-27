# Platform Runner

## Download Game Here (Desktop Only):
[![Download Here](https://img.shields.io/badge/Download-Here-brightgreen?style=for-the-badge&logo=github)](https://github.com/jobedev95/Platform-Runner/releases/latest)

After downloading the file, the game can easily be launched via the terminal:
```bash
java -jar path/to/file.jar
```

>You can also run the game by double clicking the *.jar* file. On macOS however, Gatekeeper might prevent you from launching the game directly which will require you to go to **System Settings > Privacy & Security > Security** and press *Open Anyway*. 


## Beskrivning:
Platform Runner är ett så kallat "Jump N Run" spel, det går ut på att när spelet startar börjar karaktären springa framåt och ska hoppa framåt mellan platformar för att undvika att landa i "lavan" som löper under plattformarna. Under spelets gång kommer det att genereras mynt som skall samlas in för att öka sin poäng. Landar spelaren i "lavan" kommer spelet vara över och spelaren får skriva in sitt namn som sparas i en highscore-lista.
## Spelutvecklingens fas
Platform Runner ligger för nuvarnde fortfarande i utvecklingsfas men spelet har utvecklats tillräckligt mycket för att kunna erbjudas för spelare som en testvariant. Skulle tillräckligt med intresse skapas kan planer för vidare utveckling med nya banor, funktioner etc kunna komma att fullföljas.

## Installation
Se till att ha Java-8 installerat. Om inte installera via deras hemsida
* https://www.oracle.com/java/technologies/downloads/
  
Se till att ha en IDE installerad. Om inte rekommenderas IntelliJ IDEA.
* https://www.jetbrains.com/idea/download/?section=linux

Börja med git clone/git pull i github repot, kör sedan i en IDE. Kör sedan programmet i IDE'n för att spela.
## Spelmekanik
När spelet startats upp kommer du till en huvudmeny med tre val.
* Play, klicka på play för att spela. Detta leder dig till gamescreen, där klickar du "enter" för att starta, "space" för att hoppa, "p" för att pausa.
    * I paus menyn kan du klicka på play för att fortsätta spela alternativt quit för att återgå till huvudmeny.
* Highscore, Visar tidigare registrerade highscores.
* Quit, avslutar spelet.
## Projektets Struktur med hänvisning till "Förenklad filstruktur"
* I PlatformJumper/assets återfinns alla assets som har använts för spelet såsom bilder, musik, effekter, skins.
### Nedan följer kort beskrivng av de olika klassernas funktion.
* AnimationManager.java hanterar de olika animationerna som används.
* Backgound.java hanterar olika paralaxa bakgrunder.
* Coin.Java och CoinManager.java hanterar myntets fysik samt hur de interageras mot.
* EffectsManager.java hanterar de olika partikeleffekter som används.
* GameOverState.java hanterar game over UI.
* HighScores.java hanterar hur poäng lagras efter spelet.
* Main.java startar spelet och kallar på de övriga klasserna.
* Physics.java hanterar fysiken och kollisioner i spelet.
* Player.java hanterar karaktären.
* Score.java och ScoreManager hanterar poängen i spelet.
* SharedAssets.java hanterar assets för alla klasserna.
* SoundManager.java hanterar ljud/musik för alla klasserna
* Tiles.java hanterar skapandet av plattorna man springer på.
* Screens hanterar de olika skärmarna.
    * Highscorescreen: Poängskärmen.
    * PlayScreen: Spelskärmen.
    * StartMenu: Startskärmen.


## Förenklad filstruktur:
```bash
├── docs
│   └── bed.md
├── high_scores.json
├── PlatformJumper
│   ├── assets
│   │   ├── assets.txt
│   │   ├── atlas
│   │   │   ├── character.atlas
│   │   │   ├── character.png
│   │   │   ├── coin.atlas
│   │   │   ├── coin.png
│   │   │   ├── lava_theme.atlas
│   │   │   ├── lava_theme.png
│   │   │   ├── main_logo2.png
│   │   │   ├── main_logo3.png
│   │   │   ├── main_logo4.png
│   │   │   ├── main_logo.atlas
│   │   │   ├── main_logo.png
│   │   │   ├── main_menu.atlas
│   │   │   ├── main_menu.json
│   │   │   └── main_menu.png
│   │   ├── coin.png
│   │   ├── effects
│   │   │   ├── fire_ball.png
│   │   │   ├── fire_line.png
│   │   │   ├── gold_line.png
│   │   │   ├── lava_explosion.p
│   │   │   ├── lava_sparkles.p
│   │   │   ├── main_menu_sparkles.p
│   │   │   ├── particle.png
│   │   │   ├── particle_star.png
│   │   │   └── trace_06.png
│   │   ├── fonts
│   │   │   └── Jersey10-Regular.ttf
│   │   ├── gameover_background.png
│   │   ├── high_score_skin.atlas
│   │   ├── high_score_skin.json
│   │   ├── high_score_skin.png
│   │   ├── Jersey10-Regular(10).fnt
│   │   ├── Jersey10-Regular(1).fnt
│   │   ├── Jersey10-Regular(5).fnt
│   │   ├── Jersey10-Regular(8).fnt
│   │   ├── menu_background.png
│   │   ├── skins
│   │   │   ├── game_over_skin.atlas
│   │   │   ├── game_over_skin.json
│   │   │   ├── game_over_skin.png
│   │   │   ├── jersey10-75.fnt
│   │   │   ├── Rationale-Regular(1).fnt
│   │   │   ├── Rationale-Regular(2).fnt
│   │   │   ├── Rationale-Regular(3).fnt
│   │   │   ├── Rationale-Regular(4).fnt
│   │   │   ├── Rationale-Regular(5).fnt
│   │   │   ├── Rationale-Regular(6).fnt
│   │   │   └── Rationale-Regular.fnt
│   │   ├── sounds
│   │   │   ├── backgroundMusic.ogg
│   │   │   ├── coinPickup.wav
│   │   │   ├── Credits
│   │   │   ├── gameOver.wav
│   │   │   └── menuMusic.ogg
│   │   └── tile.png
│   ├── core
│   │   └── src
│   │       └── main
│   │           └── java
│   │               └── com
│   │                   └── twodstudios
│   │                       └── platformjumper
│   │                           ├── AnimationManager.java
│   │                           ├── Background.java
│   │                           ├── Coin.java
│   │                           ├── CoinManager.java
│   │                           ├── EffectsManager.java
│   │                           ├── GameOverListener.java
│   │                           ├── GameOverState.java
│   │                           ├── HighScores.java
│   │                           ├── Main.java
│   │                           ├── PauseState.java
│   │                           ├── PhysicsManager.java
│   │                           ├── Player.java
│   │                           ├── Resettable.java
│   │                           ├── Score.java
│   │                           ├── ScoreManager.java
│   │                           ├── ScoreUpdater.java
│   │                           ├── screens
│   │                           │   ├── HighscoreScreen.java
│   │                           │   ├── PlayScreen.java
│   │                           │   └── StartMenu.java
│   │                           ├── SharedAssets.java
│   │                           ├── SoundManager.java
│   │                           └── Tiles.java
│   ├── README.md
│   └── settings.gradle
└── README.md

```
## Byggt med:
* Java: Programmeringsspråket för projektet.
* IntelliJ IDEA: IDE som utvecklarna använt sig av.
* Libgdx: Spelutvecklingsapplikationsramverk för Java.
    * Skin Composer: För att skapa Scene2D skins snabbt och effektivt.
    * GDX Texture Packer: Kombinerar mindre bilder till en större bild för bättre prestanda.
    * GDX Particle Editor: För att skapa och redigera partikeleffekter.
    * Hiero: För att skapa, redigera och optimera fontar.
* Gradle: Byggautomationsverktyg för programvaruutveckling

### Credits
Videos:

* https://www.youtube.com/channel/UCO9JvZ75Usyzgd1puurLF6A | Brent Aureli Codes
* https://www.youtube.com/channel/UCUQHh_FiVs8u5VE9NJS11Cw | Philip Mod Dev
* https://www.youtube.com/channel/UCNUVTPcvaSTZPCmC9GPVfmw | Studio JavaKhan
* https://www.youtube.com/channel/UCZhkLaB67rHVjwH1PFai0SA | Raelus  
* https://www.youtube.com/channel/UC_IV37n-uBpRp64hQIwywWQ | ForeignGuyMike 


Assets: 

* https://opengameart.org/ | Assets
* https://craftpix.net/    | Assets
* Tobias kontakt | Assets

* "Ancient Mystery Waltz (Presto)" Kevin MacLeod (incompetech.com)
Licensed under Creative Commons: By Attribution 4.0 License
http://creativecommons.org/licenses/by/4.0/

* "Big Mojo" Kevin MacLeod (incompetech.com)
Licensed under Creative Commons: By Attribution 4.0 License
http://creativecommons.org/licenses/by/4.0/

Documentation: 
https://docs.oracle.com/en/java/ | Documentation
https://libgdx.com/wiki/ | Documentation
https://docs.gradle.org/ | Documentation

### Utvecklat utav
* Mateo Guerra Calderon
* Josef Benchakroun
* Natalie Lundgren
* Silan Aslan
* Tobias Östman
* Patrik Wadelius


