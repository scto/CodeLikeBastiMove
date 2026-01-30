#!/bin/bash

# ==============================================================================
# Universelles Umbenennungs-Skript für Android/Kotlin Projekte
# ==============================================================================
# Nutzung: ./project_renamer.sh <altes_paket> <neues_paket> [alter_name] [neuer_name]
# Beispiel: ./project_renamer.sh "com.example.old" "com.scto.new" "OldApp" "GoldenIDE"

# Fehler abfangen
set -e

# Farben für Output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

DIRECTORY="../" # Zielverzeichnis (Parent)

# Prüfen der Argumente
if [ "$#" -lt 2 ]; then
    echo -e "${RED}Fehler: Zu wenige Argumente.${NC}"
    echo "Nutzung: $0 <altes_paket> <neues_paket> [alter_app_name] [neuer_app_name]"
    exit 1
fi

OLD_PACKAGE="$1"
NEW_PACKAGE="$2"
OLD_NAME="$3"
NEW_NAME="$4"

# Funktion für sed Kompatibilität (macOS vs Linux)
run_sed() {
    local old="$1"
    local new="$2"
    local file="$3"
    
    # Prüfen ob macOS (Darwin) oder Linux
    if [[ "$OSTYPE" == "darwin"* ]]; then
        sed -i '' "s|$old|$new|g" "$file"
    else
        sed -i "s|$old|$new|g" "$file"
    fi
}

echo -e "${YELLOW}Starte Umbenennung im Verzeichnis: $DIRECTORY${NC}"
echo -e "Ersetze Paket: $OLD_PACKAGE -> $NEW_PACKAGE"
if [ -n "$OLD_NAME" ]; then
    echo -e "Ersetze Name:  $OLD_NAME -> $NEW_NAME"
fi

# Zähler für geänderte Dateien
COUNT=0

# Dateien finden (ohne .git, .idea, build ordner, und ohne das script selbst)
find "$DIRECTORY" -type f \
    -not -path "*/.git/*" \
    -not -path "*/.idea/*" \
    -not -path "*/build/*" \
    -not -path "*/.gradle/*" \
    -not -name "*.sh" \
    -not -name "*.png" \
    -not -name "*.jar" \
    -print0 | while IFS= read -r -d '' file; do

    # Prüfen, ob die Datei Text enthält, der ersetzt werden muss
    if grep -q "$OLD_PACKAGE" "$file" || ([ -n "$OLD_NAME" ] && grep -q "$OLD_NAME" "$file"); then
        
        # Paketnamen ersetzen
        run_sed "$OLD_PACKAGE" "$NEW_PACKAGE" "$file"
        
        # App-Namen ersetzen (falls angegeben)
        if [ -n "$OLD_NAME" ] && [ -n "$NEW_NAME" ]; then
            run_sed "$OLD_NAME" "$NEW_NAME" "$file"
        fi

        echo "Modifiziert: $file"
        ((COUNT++))
    fi
done

# Da die Loop in einer Subshell läuft, ist COUNT hier 0.
# Wir geben einfach eine Erfolgsmeldung aus.
echo -e "${GREEN}Fertig! Bitte prüfe nun die Ordnerstruktur (Ordner müssen evtl. manuell verschoben werden).${NC}"
echo -e "${YELLOW}Hinweis: In Kotlin/Java müssen die Verzeichnispfade (src/main/java/com/...) oft manuell an das neue Paket angepasst werden.${NC}"