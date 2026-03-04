🚀 CodeLikeBasti Master Setup - The Termux Ultimate Edition
Dieses Repository enthält das CodeLikeBasti Master Setup, ein vollautomatisches Bash-Skript, das dein Android-Gerät (via Termux) in eine vollwertige, autarke Kotlin & Jetpack Compose Entwicklungsumgebung verwandelt – inklusive dem Desktop-KI-Agenten Aider.
Da Android (Termux) keine typische Linux-Umgebung ist (fehlende Fortran-Compiler, inkompatible C++ Header, fehlende glibc), scheitern normale Installationen von komplexen Python-Tools wie Aider kläglich. Dieses Skript wendet ausgeklügelte Spoofing- und Bypass-Techniken an, um das System auszutricksen und maximale Performance auf Smartphones herauszuholen.
⚡ Features der v2 (Ultimate Edition)
* Hybrid SDK-Installation: Nutzt offizielle AndroidIDE Manifeste für Struktur, aber native Zig/musl-Binaries für Turbo-Performance.
* Android SDK 36: Bereit für Vanilla Ice Cream und modernes Jetpack Compose.
* Smart Aider Installation: Reduziert die Installationszeit von Stunden auf Sekunden durch Dummy-Wheels und Metadaten-Manipulation.
* Gemini 2.5 Flash: Standardmäßig auf Googles blitzschnelles Next-Gen-Modell für Code-Generierung konfiguriert.
🧙‍♂️ Die Termux-Magie (Technical Deep-Dive & Tricks)
Hier ist dokumentiert, wie und warum das Skript die massiven Limitierungen von Android umgeht. Wenn du wissen willst, was unter der Haube passiert, ist das dein Guide.
Trick 1: Der Hybrid SDK-Ansatz & HomuHomu833 (ab Zeile 144)
* Das Problem: Standard Android SDK Tools (wie aapt2 oder adb) sind oft riesige, schlecht auf Termux abgestimmte Java/C-Wrapper, die extrem langsam laufen oder Speicherfehler werfen.
* Der Trick: Wir nutzen die offizielle manifest.json der AndroidIDE nur für die Basis-Struktur und den sdkmanager (Zeile 146-155). Die tatsächlichen Arbeits-Werkzeuge (Build-Tools & Platform-Tools) laden wir jedoch direkt aus dem HomuHomu833 android-sdk-custom Repository herunter (Zeile 158-166).
* Warum? HomuHomu833 baut diese Binaries nativ mit Zig und musl. Sie haben keine Abhängigkeiten zum Android-System, sind extrem stabil, rasant schnell und sofort API 36 fähig.
Trick 2: C++ Compiler-Zwang (ab Zeile 236)
* Das Problem: Python-Pakete wie Tree-Sitter versuchen standardmäßig mit dem C-Compiler clang zu kompilieren. Auf Android crasht dies beim Linken, weil die C++ Standard-Bibliotheken (libc++) nicht automatisch gefunden werden.
* Der Trick: Wir injizieren harte Environment-Variablen in den Build-Prozess: export CXX="aarch64-linux-android-clang++" export CXXFLAGS="-Wno-incompatible-function-pointer-types -I${PREFIX}/include -stdlib=libc++"
* Warum? Das zwingt Python (uv / pip), den richtigen AArch64 C++ Compiler von Termux samt korrekten Headern zu verwenden.
Trick 3: "Smart Dummys" gegen kaputte PyPI-Pakete (ab Zeile 250)
* Das Problem: Aider braucht tree-sitter-yaml, um YAML-Dateien zu lesen. Die Entwickler dieses Pakets haben aber die wichtige Datei schema.core.c beim Upload auf PyPI (dem Python-Paketserver) vergessen. Jeder Compiler der Welt stürzt beim Versuch ab, dieses Paket zu bauen.
* Der Trick: Wenn wir ein leeres Paket installieren, stürzt Aider beim Starten ab (ImportError). Daher baut das Skript "Smart Dummys". Es generiert on-the-fly echte Python-Dateien (__init__.py) mit leeren Funktionen wie def get_language(): pass (Zeile 259). Dann wird daraus ein lokales Rad (.whl) gebaut und via uv pip install installiert.
* Warum? Aider findet die Funktionen, bekommt eine leere Antwort, wirft keinen Fehler und arbeitet einfach glücklich weiter. Die Code-Generierung durch Gemini (die eh Text nutzt) ist davon völlig unbeeinflusst.
Trick 4: "Silent Dummys" für den Boss-Skip (ab Zeile 272)
* Das Problem: Aider will grpcio und hf-xet installieren. Das sind gigantische Bibliotheken (Google Remote Procedure Calls), die Aider nur für Cloud-Features (Vertex AI) braucht, die wir nicht nutzen. grpcio kompiliert auf dem Handy fast 1,5 Stunden und bricht oft ab.
* Der Trick: Wir bauen setup.py-Metadaten-Dummys (Zeile 277). Diese Pakete enthalten absolut keinen Code, nicht einmal eine leere Funktion.
* Warum? Der Installer (uv) sieht "Aha, Version 1.74.0 ist schon da!" und überspringt den gigantischen Download in einer Millisekunde. Da Aider diese Bibliotheken für unsere Gemini-Nutzung gar nicht erst aufruft, fällt der Bluff nie auf.
Trick 5: Die Scipy-Injection für die Repo-Map (ab Zeile 288)
* Das Problem: Aider nutzt den "PageRank"-Algorithmus, um eine "Landkarte" (Repo-Map) deines Codes zu erstellen. Dafür braucht er Mathematik (scipy). scipy verlangt jedoch beim Installieren einen Fortran-Compiler, den es für Termux nicht gibt!
* Der Trick: Die Termux-Community bietet ein extrem schwer vorkompiliertes natives Paket (pkg install python-scipy). Aider verlangt aber stur Version 1.15.3. Die Termux-Version ist oft leicht anders (z.B. 1.15.1).
* Die Lösung: Das Skript lädt das Termux-Paket (Zeile 288), sucht heimlich dessen METADATA-Datei tief in den Python-Site-Packages (Zeile 290) und überschreibt die Versionsnummer einfach per sed auf 1.15.3 (Zeile 291). Danach wird der Ordner umbenannt. Wenn Aider nun installiert wird, denkt der Installer, die perfekte Version sei bereits da, und Aiders Algorithmus nutzt die übermächtige, native Mathematik deines Handys!
🚀 Installation & Setup
1. Öffne Termux (aus F-Droid).
2. Erstelle die Skriptdatei: nano setup_master.sh
3. Füge den Skript-Code ein, speichere (CTRL+O, Enter) und schließe (CTRL+X).
4. Mache es ausführbar: chmod +x setup_master.sh
5. Starte das Dashboard: ./setup_master.sh
6. Wähle Option 3 oder Option 4 und lehne dich zurück.
🤖 Aider Quickstart-Guide (Kotlin Edition)
Aider ist ein autonomer Programmier-Agent. Er schreibt und refaktoriert deinen Code und committet ihn automatisch.
Starten
Geh in deinen Projektordner (dort, wo der .git Ordner liegt). Das Skript hat bereits gemini-2.5-flash als Standard-Modell konfiguriert!
aider

(Direkt mit einer Datei starten: aider app/src/main/java/com/.../MainActivity.kt)
Wichtige Slash-Commands
* /add <datei>: Fügt eine Datei dem aktuellen Kontext hinzu.
* /drop <datei>: Entfernt eine Datei aus dem Kontext.
* /undo: Zieht die letzten Code-Änderungen von Aider sofort zurück.
* /clear: Leert das gesamte Kurzzeitgedächtnis des Chats (WICHTIG gegen API-Rate-Limits!).
* /exit: Beendet Aider.
⚠️ WICHTIG: Vermeide /add . (Die Rate-Limit Falle)
Füge niemals dein komplettes Projekt rekursiv mit /add . oder /add app/src/ hinzu! Das sendet abertausende Zeilen Code an Google, was sofort zu folgendem Fehler führt:
The API provider has rate limited you... (Code 429)
So geht es richtig: Aider baut im Hintergrund (dank unseres Scipy-Tricks) ohnehin eine Repo-Map. Er weiß, welche Klassen überall im Projekt existieren! Füge immer nur exakt die Datei hinzu (z.B. SettingsScreen.kt), die wirklich umgeschrieben werden soll. Den Rest liest Aider per Map aus oder bittet dich im Chat um Erlaubnis, die Datei lesen zu dürfen.
🐛 Fehlerbehebung (Troubleshooting)
1. "Rate limited" (429 Error)
Du hast zu viel Text auf einmal gesendet.
* Lösung: Warte 1-2 Minuten. Tippe /clear im Aider-Chat und lade danach nur noch ein oder zwei konkrete Dateien per /add in den Chat.
2. "Model is not found" (404 Error)
Google benennt Modelle manchmal um.
* Lösung: Starte Aider explizit mit dem neuesten Namen: aider --model gemini/gemini-2.5-flash oder aider --model gemini/gemini-1.5-pro-002.
* Um es dauerhaft zu ändern: Öffne nano ~/.bashrc, ändere export AIDER_MODEL="..." und tippe danach source ~/.bashrc.
3. Submodule (wie soraX) fehlen in der Repo-Map
Aider gibt eine Info aus, dass er den Submodule-Ordner nicht lesen kann.
* Lösung: Submodule sind für Git anfangs leer. Lade den Code in Termux herunter mit: git submodule update --init --recursive