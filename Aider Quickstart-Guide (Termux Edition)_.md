🤖 Aider Quickstart-Guide (Termux Edition)
Herzlichen Glückwunsch! Du hast Aider erfolgreich installiert. Aider ist kein gewöhnlicher Chatbot (wie ChatGPT), sondern ein autonomer Programmier-Agent. Das bedeutet: Du sagst ihm, was du willst, und er bearbeitet die Dateien direkt, speichert sie ab und macht automatisch Git-Commits für dich.
Hier ist alles, was du wissen musst, um sofort loszulegen.
1. Aider starten
Navigiere in Termux in den Hauptordner deines Android-Projekts (dort, wo normalerweise deine build.gradle.kts oder der .git Ordner liegt).
Variante A: Einfach starten
aider

Variante B: Direkt mit bestimmten Dateien starten (Empfohlen!) Wenn du weißt, an welchen Dateien du arbeiten willst, übergib sie direkt beim Start:
aider app/src/main/java/com/deinname/app/MainActivity.kt

(Tipp: Nutze die Tab-Taste in Termux zur automatischen Vervollständigung von langen Pfaden!)
2. Die wichtigsten Befehle (Slash-Commands)
Wenn Aider läuft, siehst du einen Prompt (z.B. app> ). Du kannst einfach normalen Text (Anweisungen) eingeben oder spezielle Befehle mit einem Schrägstrich / nutzen.
Befehl
	Was er macht
	/add <datei>
	Fügt eine Datei zum "Gedächtnis" der KI hinzu, damit sie sie lesen/bearbeiten kann. (z.B. /add build.gradle.kts)
	/drop <datei>
	Entfernt eine Datei aus dem Gedächtnis der KI (um Token/Kosten zu sparen).
	/undo
	Der wichtigste Befehl! Macht die letzte Änderung der KI komplett rückgängig.
	/diff
	Zeigt dir an, welche Änderungen die KI gerade gemacht hat.
	/commit
	Zwingt Aider, alle aktuellen Änderungen in Git zu committen.
	/help
	Zeigt dir alle verfügbaren Befehle an.
	/exit
	Beendet Aider und bringt dich zum normalen Termux-Prompt zurück.
	3. Ein praktisches Kotlin-Beispiel
Stell dir vor, du bist in Aider und hast deine MainActivity.kt per /add hinzugefügt. Jetzt schreibst du einfach in den Chat:
Du: "Füge in der MainActivity eine Jetpack Compose Funktion namens LoginScreen hinzu. Sie soll zwei TextFields für Username und Passwort haben und einen Button, der beim Klicken einen Toast anzeigt."
Was Aider jetzt macht:
1. Aider analysiert deine MainActivity.kt.
2. Aider generiert den perfekten Kotlin Compose Code.
3. Aider sucht genau die richtige Stelle in der Datei und fügt den Code ein.
4. Aider speichert die Datei.
5. Aider erstellt vollautomatisch einen Git-Commit mit dem Titel "Add LoginScreen with TextFields and Toast".
Wenn dir die Änderung nicht gefällt, tippst du einfach:
Du: /undo
Und die Datei ist wieder exakt so wie vorher!
4. Workflows für Android-Entwickler
Fehler beheben lassen
Wenn beim Kompilieren (z.B. mit Gradle) ein Fehler auftritt, markiere den Fehler im Termux-Terminal, kopiere ihn und füge ihn in Aider ein:
Du: "Ich bekomme diesen Fehler beim Kompilieren: [Fehlermeldung einfügen]. Bitte repariere das in der MainActivity."
Neues Feature über mehrere Dateien
Du kannst mehrere Dateien gleichzeitig laden:
/add app/src/.../UserViewModel.kt
/add app/src/.../UserScreen.kt

Du: "Füge eine neue StateFlow Variable für das Alter des Users im ViewModel hinzu und zeige dieses Alter im UserScreen unter dem Namen an." Aider bearbeitet daraufhin beide Dateien simultan und hält den Code synchron.
5. Ganze Projekte & Ordner übergeben (Rekursiv)
Ja, du kannst Aider ganze Ordner (inklusive aller Unterordner und Module) übergeben. Aider berücksichtigt dabei automatisch deine .gitignore und ignoriert Build-Ordner.
So geht's:
* Beim Starten: aider app/src/ (Lädt den gesamten Quellcode-Ordner in den Chat)
* Im laufenden Chat: /add app/src/ oder einfach /add . (um das gesamte aktuelle Verzeichnis hinzuzufügen).
⚠️ WICHTIGE WARNUNG: Die "Repo-Map" (Token sparen!)
Du musst (und solltest) für normale Aufgaben niemals dein komplettes Projekt rekursiv hinzufügen! Aider nutzt eine Technologie namens Repo-Map. Sobald du Aider in einem Git-Repository startest, analysiert er automatisch die gesamte Ordnerstruktur und die Funktions-/Klassennamen deines ganzen Projekts.
* Das bedeutet: Aider weiß bereits, welche Module, Klassen und Dateien existieren, auch wenn du sie nicht mit /add in den Chat geladen hast.
* Best Practice: Füge mit /add nur die Dateien hinzu, an denen Aider auch wirklich Änderungen vornehmen soll. Wenn Aider merkt, dass er den Inhalt einer anderen Datei genau lesen muss, wird er dich sogar automatisch danach fragen! Das schont dein API-Limit, beschleunigt die Antworten enorm und verhindert, dass die KI im Code-Salat den Überblick verliert.
💡 Pro-Tipps für die Smartphone-Nutzung
1. Git ist Pflicht: Aider weigert sich oft zu arbeiten, wenn dein Projekt kein Git-Repository ist. Falls du noch keins hast, tippe vor dem Start von Aider in deinem Projektordner einfach git init.
2. Akku & API-Limits: Jeder Prompt sendet Code an die Google Gemini API. Entferne Dateien mit /drop, wenn du sie gerade nicht bearbeiten lässt. Das spart massiv Token und macht die Antworten von Aider schneller.
3. Sprache: Du kannst Aider Befehle auf Deutsch geben! Gemini übersetzt das problemlos und kommentiert den Code sogar auf Deutsch, wenn du das ausdrücklich wünschst (z.B. "Schreibe Kommentare auf Deutsch").
4. Die "AndroidIDE": Nutze Aider am besten parallel zu deiner IDE. Lass Aider in Termux laufen, lass ihn Code schreiben, und wechsle dann rüber in deine AndroidIDE oder deinen Code-Editor, um dir das Ergebnis sofort anzusehen!
Viel Spaß beim Coden mit KI-Superkräften direkt aus der Hosentasche! 🚀