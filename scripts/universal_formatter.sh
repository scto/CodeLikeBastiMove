#!/bin/bash

# ==============================================================================
# Universelles Formatierungs-Skript (Java, Kotlin, XML)
# ==============================================================================
# Nutzung: ./universal_formatter.sh [Verzeichnis] [Optionen]
# Beispiel: ./universal_formatter.sh src/main --kotlin
# Beispiel: ./universal_formatter.sh --all (nutzt aktuelles Verzeichnis)

CACHE_DIR="$HOME/.cache/formatters"

# Standardmäßig das aktuelle Verzeichnis nutzen (wie pwd)
TARGET_DIR="."

# Versionen
JAVA_FORMATTER_VERSION="1.25.2"
XML_FORMATTER_VERSION="1.1.1"
KOTLIN_FORMATTER_VERSION="0.53"

# URLs
JAVA_JAR="$CACHE_DIR/google-java-format.jar"
JAVA_URL="https://github.com/google/google-java-format/releases/download/v$JAVA_FORMATTER_VERSION/google-java-format-$JAVA_FORMATTER_VERSION-all-deps.jar"

XML_JAR="$CACHE_DIR/android-xml-formatter.jar"
XML_URL="https://github.com/teixeira0x/android-xml-formatter/releases/download/v$XML_FORMATTER_VERSION/android-xml-formatter.jar"

KOTLIN_JAR="$CACHE_DIR/ktfmt.jar"
KOTLIN_URL="https://github.com/facebook/ktfmt/releases/download/v$KOTLIN_FORMATTER_VERSION/ktfmt-$KOTLIN_FORMATTER_VERSION-jar-with-dependencies.jar"

mkdir -p "$CACHE_DIR"

# Hilfsfunktion zum Download
ensure_formatter() {
    local jar_path="$1"
    local url="$2"
    local name="$3"

    if [ ! -f "$jar_path" ]; then
        echo "Lade $name herunter..."
        if command -v curl &> /dev/null; then
            curl -L -o "$jar_path" "$url"
        elif command -v wget &> /dev/null; then
            wget -q -O "$jar_path" "$url"
        else
            echo "Fehler: Weder curl noch wget gefunden."
            exit 1
        fi
    fi
}

format_kotlin() {
    ensure_formatter "$KOTLIN_JAR" "$KOTLIN_URL" "KtFmt"
    echo "Formatiere Kotlin Dateien in $TARGET_DIR..."
    # --google-style ist Standard für Android
    find "$TARGET_DIR" -name "*.kt" -not -path "*/build/*" -exec java -jar "$KOTLIN_JAR" --google-style {} +
}

format_java() {
    ensure_formatter "$JAVA_JAR" "$JAVA_URL" "Google Java Format"
    echo "Formatiere Java Dateien in $TARGET_DIR..."
    find "$TARGET_DIR" -name "*.java" -not -path "*/build/*" -exec java -jar "$JAVA_JAR" --replace {} +
}

format_xml() {
    ensure_formatter "$XML_JAR" "$XML_URL" "Android XML Formatter"
    echo "Formatiere XML Dateien in $TARGET_DIR..."
    find "$TARGET_DIR" -name "*.xml" -not -path "*/build/*" -not -path "*/.idea/*" -exec java -jar "$XML_JAR" --indention 4 --attribute-indention 4 {} \;
}

# --- Logik zur Bestimmung des Zielverzeichnisses ---

# Prüfen, ob Argumente vorhanden sind
if [ $# -eq 0 ]; then
    echo "Keine Argumente übergeben."
    echo "Nutzung: $0 [Verzeichnis] [--all | --kotlin | --java | --xml]"
    echo "Beispiel: $0 . --all"
    exit 1
fi

# Prüfen, ob das erste Argument ein Pfad ist (und keine Option wie --all)
if [[ "$1" != -* ]]; then
    if [ -d "$1" ]; then
        TARGET_DIR="$1"
        shift # Erstes Argument entfernen, damit die Schleife unten nur noch Flags verarbeitet
    else
        echo "Fehler: Verzeichnis '$1' existiert nicht."
        exit 1
    fi
fi

# Ab hier ist TARGET_DIR gesetzt (entweder durch User oder Standard ".")

# Argumente parsen
ACTION_PERFORMED=false

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --all) format_kotlin; format_java; format_xml; ACTION_PERFORMED=true ;;
        --kotlin) format_kotlin; ACTION_PERFORMED=true ;;
        --java) format_java; ACTION_PERFORMED=true ;;
        --xml) format_xml; ACTION_PERFORMED=true ;;
        *) echo "Unbekannte Option: $1"; exit 1 ;;
    esac
    shift
done

if [ "$ACTION_PERFORMED" = false ]; then
    echo "Keine Formatierungs-Option gewählt."
    echo "Bitte nutze --all, --kotlin, --java oder --xml."
    exit 1
fi

echo "Formatierung abgeschlossen."