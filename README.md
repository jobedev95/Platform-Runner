# Platform Runner

## Beskrivning:
Platform Runner är ett så kallat "Jump N Run" spel, det går ut på att när spelet startar börjar karaktären springa framåt och ska hoppa framåt mellan platformar för att undvika att landa i "lavan" som löper under plattformarna. Under spelets gång kommer det att genereras mynt som skall samlas in för att öka sin poäng. Landar spelaren i "lavan" kommer spelet vara över och spelaren får skriva in sitt namn som sparas i en highscore-lista.
## Spelutvecklingens fas
Platform Runner ligger för nuvarnde fortfarande i utvecklingsfas men spelet har utvecklats tillräckligt mycket för att kunna erbjudas för spelare som en testvariant. Skulle tillräckligt med intresse skapas kan planer för vidare utveckling med nya banor, funktioner etc kunna komma att fullföljas.

## Installation
Se till att ha Java-23 installerat. Om inte installera via deras hemsida
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


## Förenklad filstruktur:
```bash
├── docs
│   └── bed.md
├── high_scores.json
├── PlatformJumper
│   ├── assets # Innehåller alla assets för spelet
│   │   ├── assets.txt
│   │   ├── atlas # Karaktär, mynt, lava, logo, meny
│   │   ├── effects # Partikel effekter 
│   │   ├── gameover_background.png
│   │   ├── menu_background.png
│   │   ├── skins # Skins
│   │   ├── sounds # Musik, ljudeffekt, Credits
│   │   └── tile.png
│   ├── build.gradle
│   ├── core
│   │   ├── build
│   │   │   ├── libs
│   │   │   │   
│   │   │   └── tmp
│   │   ├── build.gradle
│   │   └── src # De olika javaklasserna
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
│   │                           ├── GameOverState.java
│   │                           ├── HighScores.java
│   │                           ├── Main.java
│   │                           ├── PhysicsManager.java
│   │                           ├── Player.java
│   │                           ├── ResetListener.java
│   │                           ├── Score.java
│   │                           ├── ScoreManager.java
│   │                           ├── ScoreUpdater.java
│   │                           ├── screens
│   │                           │   ├── PlayScreen.java
│   │                           │   └── StartMenu.java
│   │                           ├── SharedAssets.java
│   │                           ├── SoundManager.java
│   │                           └── Tiles.java
│   ├── gradle
│   ├── gradle.properties
│   ├── gradlew
│   ├── gradlew.bat
│   ├── high_scores.json
│   ├── lwjgl3
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


