# AnimePower (Forge 1.20.1 / 47.4.23 / Java 17)

OP-only command mod. No gradlew wrapper jar is included here (it's a binary
file this environment can't generate) — follow the setup below once, then
`./gradlew build` works like any normal ForgeGradle project.

## Commands
- `/power <player> <power>` — assign a power (OP only, permission level 2)
- `/power <player> clear` — remove a player's power
- `/power <player> get` — show a player's current power
- `/power list` — list every current assignment

Valid power names: `gojo, sukuna, goku, asta, naruto, yogiri, rimuru, luffy,
tanjiro, kirito, sungjinwoo`

Each player has at most one power; setting a new one overwrites the old one.
Assignments are stored in the overworld's level data (a `SavedData`), so they
persist across server restarts. `<player>` must be online when you run the
command (it uses Brigadier's standard player argument/selector).

## One-time setup (do this once, on your own machine)

You need the gradlew wrapper files (`gradlew`, `gradlew.bat`,
`gradle/wrapper/gradle-wrapper.jar`, `gradle/wrapper/gradle-wrapper.properties`),
which are binary/generated and not something that can be handed over as text.
Easiest path:

1. Download the official Forge 1.20.1 MDK for build **47.4.23** from
   https://files.minecraftforge.net/ (Forge 1.20.1 → version 47.4.23 →
   "Mdk"). Unzip it somewhere.
2. From that unzipped MDK, copy just these into a new empty folder (this
   gives you a working wrapper):
   - `gradlew`
   - `gradlew.bat`
   - `gradle/` (the whole folder, contains `wrapper/gradle-wrapper.jar` etc.)
3. Into that same folder, copy **all the files from this `AnimePower/`
   project** (`build.gradle`, `settings.gradle`, `gradle.properties`, and the
   whole `src/` tree), overwriting the MDK's own `build.gradle`,
   `settings.gradle`, `gradle.properties` and `src/`.
4. From that folder, run:

   ```
   ./gradlew build
   ```

   (Windows: `gradlew.bat build`)

5. The compiled mod jar will be at:

   ```
   build/libs/AnimePower-1.0.0.jar
   ```

   Drop that file into your server's `mods/` folder. This is a real,
   reobfuscated Forge mod jar (built and packaged by ForgeGradle's `jar` +
   `reobfJar` tasks) — you can confirm it's a valid zip with
   `unzip -l AnimePower-1.0.0.jar` and see `META-INF/mods.toml` listed
   inside.

Requires JDK 17 on the machine you build with (same as your server).
