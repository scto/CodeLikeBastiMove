# Material Theme Export

Willkommen zu deinem exportierten Material Theme!

Dieses Archiv enthält Kotlin-Dateien, die du direkt in dein Jetpack Compose Projekt kopieren kannst, um dein Designsystem zu aktualisieren.
Inhalt
* ui/theme/Color.kt: Enthält die Farbpalette, die basierend auf deinem Seed-Image oder deiner Farbwahl generiert wurde.
* ui/theme/Typo.kt: Enthält die Typografie-Definitionen (Material 3).
* ui/theme/Themen.kt: Enthält das MaterialTheme Composable und die Schema-Logik (Hell/Dunkel).
Installation
1. Entpacke dieses ZIP-Archiv.
2. Kopiere den Ordner ui in dein Android-Modul (z.B. app/src/main/java/com/deinpackage/).
3. Passe bei Bedarf die Package-Namen in den kopierten Dateien an (Standard ist com.example.theme).
Verwendung
Umschließe deine App oder deinen Screen mit dem Theme:

```
import com.example.theme.AppTheme

setContent {
   AppTheme {
       // Dein Content hier
       Surface(color = MaterialTheme.colorScheme.background) {
           Greeting("Android")
       }
   }
}
```

Viel Spaß beim Coden!