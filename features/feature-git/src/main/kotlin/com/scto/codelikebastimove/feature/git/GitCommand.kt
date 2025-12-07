package com.scto.codelikebastimove.feature.git

sealed class GitCommand(
    val name: String,
    val description: String,
    val usage: String,
    val category: GitCommandCategory
) {
    data object Init : GitCommand(
        name = "git init",
        description = "Erstellt ein neues Git-Repository",
        usage = "git init [directory]",
        category = GitCommandCategory.SETUP
    )
    
    data object Clone : GitCommand(
        name = "git clone",
        description = "Klont ein Repository in ein neues Verzeichnis",
        usage = "git clone <repository> [directory]",
        category = GitCommandCategory.SETUP
    )
    
    data object Add : GitCommand(
        name = "git add",
        description = "Fuegt Dateiaenderungen zum Staging-Bereich hinzu",
        usage = "git add <pathspec>...",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Status : GitCommand(
        name = "git status",
        description = "Zeigt den Status des Arbeitsbaums an",
        usage = "git status [options]",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Diff : GitCommand(
        name = "git diff",
        description = "Zeigt Aenderungen zwischen Commits, Commit und Arbeitsbaum, etc.",
        usage = "git diff [options] [<commit>] [--] [<path>...]",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Commit : GitCommand(
        name = "git commit",
        description = "Zeichnet Aenderungen im Repository auf",
        usage = "git commit -m <message>",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Notes : GitCommand(
        name = "git notes",
        description = "Fuegt Objekt-Notizen hinzu oder inspiziert sie",
        usage = "git notes [list | add | copy | append | edit | show | merge | remove]",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Restore : GitCommand(
        name = "git restore",
        description = "Stellt Arbeitsbaumdateien wieder her",
        usage = "git restore [options] [<pathspec>...]",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Reset : GitCommand(
        name = "git reset",
        description = "Setzt den aktuellen HEAD auf den angegebenen Zustand zurueck",
        usage = "git reset [<mode>] [<commit>]",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Rm : GitCommand(
        name = "git rm",
        description = "Entfernt Dateien aus dem Arbeitsbaum und Index",
        usage = "git rm [options] [--] <file>...",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Mv : GitCommand(
        name = "git mv",
        description = "Verschiebt oder benennt eine Datei, ein Verzeichnis oder einen Symlink um",
        usage = "git mv <source> <destination>",
        category = GitCommandCategory.BASIC_SNAPSHOTTING
    )
    
    data object Branch : GitCommand(
        name = "git branch",
        description = "Listet, erstellt oder loescht Branches",
        usage = "git branch [options] [<branchname>]",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Checkout : GitCommand(
        name = "git checkout",
        description = "Wechselt Branches oder stellt Arbeitsbaumdateien wieder her",
        usage = "git checkout [options] <branch>",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Switch : GitCommand(
        name = "git switch",
        description = "Wechselt Branches",
        usage = "git switch [options] <branch>",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Merge : GitCommand(
        name = "git merge",
        description = "Verbindet zwei oder mehr Entwicklungshistorien",
        usage = "git merge [options] <commit>...",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Mergetool : GitCommand(
        name = "git mergetool",
        description = "Fuehrt Merge-Konfliktloeser aus, um Merge-Konflikte zu loesen",
        usage = "git mergetool [options] [<file>...]",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Log : GitCommand(
        name = "git log",
        description = "Zeigt Commit-Protokolle an",
        usage = "git log [options] [<revision-range>] [[--] <path>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Stash : GitCommand(
        name = "git stash",
        description = "Versteckt die Aenderungen in einem schmutzigen Arbeitsverzeichnis",
        usage = "git stash [push | pop | list | show | drop | clear | apply]",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Tag : GitCommand(
        name = "git tag",
        description = "Erstellt, listet, loescht oder verifiziert ein Tag-Objekt",
        usage = "git tag [options] [<tagname>] [<commit>]",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Worktree : GitCommand(
        name = "git worktree",
        description = "Verwaltet mehrere Arbeitsbaeume",
        usage = "git worktree [add | list | lock | move | prune | remove | unlock]",
        category = GitCommandCategory.BRANCHING
    )
    
    data object Fetch : GitCommand(
        name = "git fetch",
        description = "Laedt Objekte und Referenzen von einem anderen Repository herunter",
        usage = "git fetch [options] [<repository> [<refspec>...]]",
        category = GitCommandCategory.SHARING
    )
    
    data object Pull : GitCommand(
        name = "git pull",
        description = "Holt und integriert von einem anderen Repository oder lokalen Branch",
        usage = "git pull [options] [<repository> [<refspec>...]]",
        category = GitCommandCategory.SHARING
    )
    
    data object Push : GitCommand(
        name = "git push",
        description = "Aktualisiert Remote-Referenzen zusammen mit zugehoerigen Objekten",
        usage = "git push [options] [<repository> [<refspec>...]]",
        category = GitCommandCategory.SHARING
    )
    
    data object Remote : GitCommand(
        name = "git remote",
        description = "Verwaltet getrackte Repositories",
        usage = "git remote [add | rename | remove | set-url | show | prune]",
        category = GitCommandCategory.SHARING
    )
    
    data object Submodule : GitCommand(
        name = "git submodule",
        description = "Initialisiert, aktualisiert oder inspiziert Submodule",
        usage = "git submodule [add | status | init | deinit | update | set-branch | set-url | summary | foreach | sync | absorbgitdirs]",
        category = GitCommandCategory.SHARING
    )
    
    data object Show : GitCommand(
        name = "git show",
        description = "Zeigt verschiedene Arten von Objekten an",
        usage = "git show [options] [<object>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Shortlog : GitCommand(
        name = "git shortlog",
        description = "Fasst git log Ausgabe zusammen",
        usage = "git shortlog [options] [<revision-range>] [[--] <path>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Describe : GitCommand(
        name = "git describe",
        description = "Gibt einem Objekt einen lesbaren Namen basierend auf einem verfuegbaren Ref",
        usage = "git describe [options] [<commit-ish>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Bisect : GitCommand(
        name = "git bisect",
        description = "Verwendet binaere Suche, um den Commit zu finden, der einen Bug eingefuehrt hat",
        usage = "git bisect [start | bad | good | skip | reset | visualize | replay | log | run]",
        category = GitCommandCategory.PATCHING
    )
    
    data object Blame : GitCommand(
        name = "git blame",
        description = "Zeigt, welche Revision und welcher Autor jede Zeile einer Datei zuletzt geaendert hat",
        usage = "git blame [options] [<rev>] [--] <file>",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Grep : GitCommand(
        name = "git grep",
        description = "Gibt Zeilen aus, die einem Muster entsprechen",
        usage = "git grep [options] <pattern> [<rev>...] [[--] <path>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Apply : GitCommand(
        name = "git apply",
        description = "Wendet einen Patch auf Dateien und/oder den Index an",
        usage = "git apply [options] [<patch>...]",
        category = GitCommandCategory.PATCHING
    )
    
    data object CherryPick : GitCommand(
        name = "git cherry-pick",
        description = "Wendet die Aenderungen einiger bestehender Commits an",
        usage = "git cherry-pick [options] <commit>...",
        category = GitCommandCategory.PATCHING
    )
    
    data object FormatPatch : GitCommand(
        name = "git format-patch",
        description = "Bereitet Patches fuer E-Mail-Versand vor",
        usage = "git format-patch [options] [<since> | <revision-range>]",
        category = GitCommandCategory.PATCHING
    )
    
    data object Rebase : GitCommand(
        name = "git rebase",
        description = "Wendet Commits auf einer anderen Basis erneut an",
        usage = "git rebase [options] [<upstream> [<branch>]]",
        category = GitCommandCategory.PATCHING
    )
    
    data object Revert : GitCommand(
        name = "git revert",
        description = "Macht einige bestehende Commits rueckgaengig",
        usage = "git revert [options] <commit>...",
        category = GitCommandCategory.PATCHING
    )
    
    data object Config : GitCommand(
        name = "git config",
        description = "Holt und setzt Repository- oder globale Optionen",
        usage = "git config [options]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Reflog : GitCommand(
        name = "git reflog",
        description = "Verwaltet Reflog-Informationen",
        usage = "git reflog [show | expire | delete | exists]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Gc : GitCommand(
        name = "git gc",
        description = "Bereinigt unnoetige Dateien und optimiert das lokale Repository",
        usage = "git gc [options]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Clean : GitCommand(
        name = "git clean",
        description = "Entfernt nicht getrackte Dateien aus dem Arbeitsbaum",
        usage = "git clean [options]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Fsck : GitCommand(
        name = "git fsck",
        description = "Verifiziert die Konnektivitaet und Gueltigkeit der Objekte in der Datenbank",
        usage = "git fsck [options]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Prune : GitCommand(
        name = "git prune",
        description = "Entfernt alle nicht erreichbaren Objekte aus der Objektdatenbank",
        usage = "git prune [options]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Archive : GitCommand(
        name = "git archive",
        description = "Erstellt ein Archiv von Dateien aus einem benannten Baum",
        usage = "git archive [options] <tree-ish> [<path>...]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object Bundle : GitCommand(
        name = "git bundle",
        description = "Verschiebt Objekte und Referenzen per Archiv",
        usage = "git bundle [create | verify | list-heads | unbundle]",
        category = GitCommandCategory.ADMINISTRATION
    )
    
    data object ShowBranch : GitCommand(
        name = "git show-branch",
        description = "Zeigt Branches und ihre Commits an",
        usage = "git show-branch [options] [<rev>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object Range : GitCommand(
        name = "git range-diff",
        description = "Vergleicht zwei Commit-Bereiche",
        usage = "git range-diff [options] <base> <rev1> <rev2>",
        category = GitCommandCategory.INSPECTION
    )
    
    data object LsFiles : GitCommand(
        name = "git ls-files",
        description = "Zeigt Informationen ueber Dateien im Index und Arbeitsbaum an",
        usage = "git ls-files [options] [<file>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object LsTree : GitCommand(
        name = "git ls-tree",
        description = "Listet den Inhalt eines Baumobjekts auf",
        usage = "git ls-tree [options] <tree-ish> [<path>...]",
        category = GitCommandCategory.INSPECTION
    )
    
    data object CatFile : GitCommand(
        name = "git cat-file",
        description = "Gibt Inhalt oder Typ und Groesse fuer Repository-Objekte aus",
        usage = "git cat-file <type> <object>",
        category = GitCommandCategory.INSPECTION
    )
    
    data object RevParse : GitCommand(
        name = "git rev-parse",
        description = "Parst und formatiert Referenzen",
        usage = "git rev-parse [options] <args>...",
        category = GitCommandCategory.INSPECTION
    )
    
    companion object {
        fun getAllCommands(): List<GitCommand> = listOf(
            Init, Clone, Add, Status, Diff, Commit, Notes, Restore, Reset, Rm, Mv,
            Branch, Checkout, Switch, Merge, Mergetool, Log, Stash, Tag, Worktree,
            Fetch, Pull, Push, Remote, Submodule,
            Show, Shortlog, Describe, Bisect, Blame, Grep, ShowBranch, Range, LsFiles, LsTree, CatFile, RevParse,
            Apply, CherryPick, FormatPatch, Rebase, Revert,
            Config, Reflog, Gc, Clean, Fsck, Prune, Archive, Bundle
        )
        
        fun getCommandsByCategory(category: GitCommandCategory): List<GitCommand> {
            return getAllCommands().filter { it.category == category }
        }
    }
}

enum class GitCommandCategory(val displayName: String) {
    SETUP("Setup und Konfiguration"),
    BASIC_SNAPSHOTTING("Grundlegende Schnappschuesse"),
    BRANCHING("Branching und Merging"),
    SHARING("Teilen und Aktualisieren"),
    INSPECTION("Inspektion und Vergleich"),
    PATCHING("Patching"),
    ADMINISTRATION("Administration")
}
