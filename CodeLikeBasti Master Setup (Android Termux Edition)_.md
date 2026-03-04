🚀 CodeLikeBasti Master Setup (Android Termux Edition)
Das CodeLikeBasti Master Setup ist ein vollautomatisches, extrem robustes Bash-Skript, um eine komplette Android-Entwicklungsumgebung (fokussiert auf Kotlin & Jetpack Compose) sowie den hochkomplexen KI-Programmieragenten Aider lokal auf einem Android-Gerät (via Termux) einzurichten.
Dieses Skript verwandelt dein Smartphone oder Tablet in eine voll funktionsfähige, autarke Mobile-IDE mit lokaler KI-Unterstützung – und umgeht dabei meisterhaft die massiven Limitierungen und Compiler-Bugs von Android Termux.
✨ Die Features
* 📱 Native Android SDK & NDK Installation: Richtet Build-Tools, Platform-Tools und das AArch64 Android NDK ein.
* 🧩 Zukunftssicher mit SDK 36: Automatische Installation von Android SDK 36 (Vanilla Ice Cream) für moderne Jetpack Compose Apps.
* 🤖 Blitzschnelles AI-Setup (Aider via uv): Installiert den KI-Assistenten in Rekordzeit durch den nativen Rust-basierten Paketmanager uv.
* 🛡️ Intelligentes Backup-System: Erstellt Sicherheitskopien der ~/.bashrc und ermöglicht chirurgische Rollbacks einzelner Installationen.
* 🔑 Git Credential Auto-Store: Speichert Personal Access Tokens (PAT) dauerhaft, sodass sie bei git push auf dem Handy nicht neu eingetippt werden müssen.
🧙‍♂️ Die Termux-Magie (Spezial-Tricks für die Aider-Installation)
Einen KI-Agenten, der für massive Linux-Server und Desktop-PCs geschrieben wurde, nativ auf Android zu kompilieren, ist berüchtigt für Endlos-Fehler. Android fehlen Fortran-Compiler, Standard-C-Header und PC-spezifische Build-Umgebungen.
Dieses Skript nutzt vier geniale Spezial-Tricks (Spoofing), um diese Hürden zu nehmen, ohne die Funktionalität für die lokale KI-Entwicklung (mit Kotlin) zu beeinträchtigen:
Trick 1: Der "Boss-Skip" (Silent Dummies für grpcio & hf-xet)
Einige Bibliotheken wie grpcio (Google RPC) benötigen auf Android über eine Stunde zum Kompilieren und brechen am Ende oft wegen Speicherfehlern ab. Aider nutzt diese Pakete nur für sehr spezifische Cloud-APIs (wie Vertex AI), die wir für HTTP-basierte APIs (Gemini) gar nicht brauchen.
* Die Lösung: Das Skript baut in Millisekunden "stumme" Dummy-Räder (Wheels) – komplett leere Python-Pakete mit der exakt geforderten Versionsnummer (z.B. 1.74.0). Der Installer (uv) findet diese, installiert sie sofort und überspringt den gigantischen C++-Download komplett.
Trick 2: Die "Smart Dummys" (Tree-Sitter Bypass)
Aider nutzt C-basierte tree-sitter-Pakete (z.B. für C#, YAML), um Code-Strukturen zu analysieren. Einige dieser Pakete sind auf dem offiziellen PyPI-Server beschädigt (es fehlen .c Quelldateien im Archiv), was den Android C-Compiler unweigerlich zum Absturz bringt.
* Die Lösung: Ein leerer Dummy würde hier zum Absturz von Aider führen (ImportError). Daher generiert das Skript "Smart Dummys". Es baut lokale Python-Pakete, die echte Funktionen enthalten (def get_language(): pass). Wenn Aider nach dem YAML-Parser fragt, meldet sich der Dummy, gibt ein leeres Ergebnis zurück und Aider läuft fehlerfrei weiter, anstatt abzustürzen!
Trick 3: Das "Metadata-Spoofing" (Der SciPy / Fortran-Fix)
Die hochkomplexe Mathematik-Bibliothek scipy erfordert einen Fortran-Compiler (gfortran), der auf Android schlichtweg nicht existiert.
* Die Lösung: Termux bietet eine bereits (von den Termux-Entwicklern mühsam) vorkompilierte Version (python-scipy) an. Der Aider-Installer lehnt diese aber ab, weil die Version leicht abweicht. Das Skript manipuliert daraufhin direkt die Metadaten-Datei (METADATA) der nativen Termux-Installation und benennt sie heimlich in die von Aider exakt geforderte Version 1.15.3 um. Zudem bauen wir einen leeren Dummy. Der Installer ist glücklich, überspringt den Fortran-Build und Aider nutzt im Hintergrund die hochoptimierte, native Android-Version!
Trick 4: C++ Compiler-Zwang
Viele Python-Pakete gehen fälschlicherweise davon aus, dass sie mit einem Standard-C-Compiler kompiliert werden können, rufen dann aber C++-Funktionen auf.
* Die Lösung: Das Skript exportiert strikte Umgebungsvariablen (export CXX="aarch64-linux-android-clang++" und export CXXFLAGS="-stdlib=libc++"), die den Build-Prozess zwingen, Androids AArch64 C++ Compiler samt korrekten C++-Standardbibliotheken zu verwenden.
🚀 Installation & Nutzung
Voraussetzungen
* Ein Android-Gerät (ARM64 / AArch64)
* Die Termux-App (Ausschließlich aus dem F-Droid Store!)
* Ca. 3-5 GB freier Speicherplatz
Starten des Skripts
1. Öffne Termux.
2. Erstelle die Skript-Datei:
nano setup_master.sh

3. Füge den Code ein, speichere und mache das Skript ausführbar:
chmod +x setup_master.sh

4. Starte das interaktive Dashboard:
./setup_master.sh

🎛️ Das Menüsystem
[ Installationen & Konfiguration ]
   1. Installiere Android SDK & NDK: Basis-Tools für die App-Kompilierung.
   2. Installiere Android SDK 36: Spezifisch für moderne Jetpack Compose Apps (Kotlin).
   3. Installiere Aider: Wendet die "Spoofing-Magie" an und installiert den KI-Agenten in Sekunden statt Stunden.
   4. Installiere ALLES: Der 1-Klick "Rundum-Sorglos"-Modus.
   5. Installiere nur Dev-Tools: Git, Git-LFS, Gradle, OpenJDK.
   6. Konfiguriere Git: Setzt User-Daten und den dauerhaften Credential-Store.
[ Deinstallationen ] 7) bis 11) Erlauben dir, einzelne Module chirurgisch zu entfernen oder das gesamte System absolut spurlos auf den Ursprungszustand (inkl. .bashrc Rollback) zurückzusetzen.
🔧 Fehlerbehebung (Troubleshooting)
Alle Fehler werden dynamisch abgefangen und am exakten Speicherort des Skripts in der Datei codelikebasti_error.log protokolliert. Wenn ein Download wegen einer instabilen Internetverbindung abbricht, starte das Skript einfach neu. Es erkennt bereits heruntergeladene, intakte Dateien und setzt genau da an, wo es aufgehört hat.