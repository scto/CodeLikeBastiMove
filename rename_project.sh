#!/bin/bash

#Anleitung zur Verwendung
# "======================"
# 1. Erstelle eine neue Datei im Root-Verzeichnis deines Projekts (dort wo gradlew liegt), z.B. rename_project.sh
#2. Füge den untenstehenden Code ein.
# 3. Mache das Script ausführbar: chmod +x rename_project.sh
# 4. Führe es aus: ./rename_project.sh
# "======================"

# Konfiguration
OLD_PACKAGE="com.mobiledevpro"
NEW_PACKAGE="com.scto"
OLD_DIR_PATH="com/mobiledevpro"
NEW_DIR_PATH="com/scto"

# Funktion für OS-kompatibles sed (funktioniert auf Mac und Linux)
run_sed() {
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "$@"
    else
        sed -i "$@"
    fi
}

echo "======================================================="
echo "Startet Umbenennung von $OLD_PACKAGE zu $NEW_PACKAGE"
echo "======================================================="

# 1. Verzeichnisse verschieben (Move Directories)
# Wir suchen nach allen Ordnern, die auf 'com/mobiledevpro' enden
echo ">> [1/3] Verschiebe Verzeichnisse..."

# Findet Verzeichnisse wie: feature/home/src/main/kotlin/com/mobiledevpro
find . -type d -path "*/$OLD_DIR_PATH" -not -path "*/build/*" -not -path "*/.git/*" | while read old_path; do
    # Erstellt den neuen Pfad, indem 'com/mobiledevpro' durch 'com/scto' ersetzt wird
    new_path="${old_path/$OLD_DIR_PATH/$NEW_DIR_PATH}"
    
    echo "  Verschiebe: $old_path -> $new_path"
    
    # Erstelle das Zielverzeichnis
    mkdir -p "$new_path"
    
    # Verschiebe alle Dateien vom alten in das neue Verzeichnis
    mv "$old_path"/* "$new_path"/ 2>/dev/null
    
    # Lösche das alte leere Verzeichnis 'mobiledevpro'
    rmdir "$old_path"
    
    # Lösche das Elternverzeichnis 'com', falls es nun leer ist (optional, hier behalten wir com/ meistens)
    # rmdir "$(dirname "$old_path")" 2>/dev/null || true
done

echo ">> Verzeichnisse verschoben."

# 2. Strings in Dateien ersetzen (Replace Strings)
echo ">> [2/3] Ersetze Package-Namen in Dateien..."

# Liste der Dateiendungen, die bearbeitet werden sollen
grep_args=(
    -rIl "$OLD_PACKAGE" . 
    --exclude-dir=build 
    --exclude-dir=.git 
    --exclude-dir=.gradle 
    --exclude-dir=.idea
)

# Führe die Ersetzung durch
# Wir suchen nach Strings in .kt, .kts, .xml, .pro, .gradle, .proto, .toml Dateien
find . -type f \( -name "*.kt" -o -name "*.kts" -o -name "*.xml" -o -name "*.pro" -o -name "*.gradle" -o -name "*.proto" -o -name "*.toml" \) -not -path "*/build/*" -not -path "*/.git/*" | while read file; do
    if grep -q "$OLD_PACKAGE" "$file"; then
        echo "  Bearbeite: $file"
        run_sed "s/$OLD_PACKAGE/$NEW_PACKAGE/g" "$file"
    fi
done

echo ">> Strings ersetzt."

# 3. Aufräumen und Abschluss
echo ">> [3/3] Abschlussarbeiten..."

# Gradle Clean (optional, aber empfohlen)
echo "  Führe ./gradlew clean aus (das kann kurz dauern)..."
if [ -f "./gradlew" ]; then
    ./gradlew clean > /dev/null 2>&1
else
    echo "  Warnung: gradlew nicht gefunden, überspringe clean."
fi

echo "======================================================="
echo "FERTIG!"
echo "Bitte synchronisiere das Projekt nun in Android Studio (Sync Project with Gradle Files)."
echo "======================================================="