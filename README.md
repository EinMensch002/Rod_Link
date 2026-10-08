# 🎣 StasisRod - Fishing Rod Link Plugin

![StasisRod Banner](https://img.shields.io/badge/StasisRod-v1.0.0-blue?style=for-the-badge&logo=java)
![Paper](https://img.shields.io/badge/Paper-1.20+-red?style=for-the-badge&logo=apachekafka)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

> **Ein innovatives Minecraft Paper-Plugin, das Angelruten mit Redstone-Druckplatten verbindet und intelligent verwaltet.**

---

## 📋 Inhaltsverzeichnis
- [Übersicht](#-übersicht)
- [Features](#-features)
- [Installation](#-installation)
- [Bedienung](#-bedienung)
- [Konfiguration](#-konfiguration)
- [Credits](#-credits)
- [Support](#-support)

---

## 📖 Übersicht

**StasisRod** ist ein Paper-Plugin für Minecraft 1.20+, das Spielern ermöglicht, Angelruten mit Druckplatten zu verlinken. Diese Verlinkung erzeugt ein **dauerhaftes Redstone-Signal** (Signalstärke 15) an der Druckplatte – unabhängig davon, ob jemand darauf steht.

Damit eröffnen sich völlig neue Möglichkeiten für:

| 🔌 Redstone-Maschinen | 🎯 Custom Maps | ⚙️ Automatisierungssysteme |
|---|---|---|
| Zuverlässige Signalquellen | Adventure-Projekte | Neue Puzzle-Mechaniken |
| Ohne klassische Limitierungen | Spannende Quests | Kreative Konstruktionen |

---

## ✨ Features

### 🎣 **Verlinkung**
- Werfe eine Angelrute einfach auf eine beliebige Druckplatte
- Automatische Verlinkung beim Haken-Kontakt
- Unsichtbare Anker-ArmorStands markieren die Position

### 🔌 **Dauerhaftes Signal**
- Redstone-Signalstärke **15** bleibt permanent erhalten
- Unabhängig von Entity-Gewicht oder Aktivität
- Perfekt für zuverlässige Maschinen

### 💧 **Visuelle Effekte**
- Shimmer-Partikeleffekte an verlinkten Platten
- Nur für Spieler in Nähe sichtbar (optimiert für Performance)
- Konfigurierbare Effektradius und Aktivierung

### 🎯 **Single-Use Ruten**
- Bennenne die Rute mit `[1]` im Namen (z.B. `[1] Spezial-Rute`)
- Nach einmaliger Nutzung → Permanent zerstört
- Ideal für Puzzle-Elemente und Questbelohnungen

### 🛠️ **Crafting-Entlinkung**
- Lege eine verlinkte Rute ins Crafting-Gitter
- Automatische Reinigung ohne Zerstörung
- Ideal zum Zurücksetzen von Ruten

### 💾 **Persistente Daten**
- Alle Verlinkungen bleiben nach Server-Restart erhalten
- Automatische Wiederherstellung im Falle eines Crashes
- Sichere Datenspeicherung im ItemMeta

### 🎨 **CustomModelData**
- Automatische Textur-Zuweisung nach Ruten-Namen
- **Stasis-Rod** (Keyword: "stasis") → CustomModelData 1001
- **Orbital/Nuke** (Keywords: "orbital", "nuke", "stabshot") → CustomModelData 1002
- **Trap** (Keywords: "trap", "falle") → CustomModelData 1003

---

## 🚀 Installation

### ✅ Anforderungen
- **Minecraft Server**: Paper 1.20+
- **Java**: 21 (empfohlen)
  - ℹ️ *Kompatibilität mit älteren Java-Versionen wird derzeit überprüft*

### 📦 Schritt-für-Schritt

1. **Download**
   - Lade die neueste Version aus den [Releases](../../releases) herunter

2. **Installation**
   ```
   plugins/StasisRod.jar → in den plugins/ Ordner kopieren
   ```

3. **Server starten**
   ```bash
   ./start.sh
   # oder dein Server-Startskript
   ```

4. **Konfiguration anpassen** (optional)
   ```
   plugins/StasisRod/config.yml
   ```

5. **Fertig!**
   - Plugin ist sofort einsatzbereit

---

## 🎮 Bedienung

### 🎯 Rute mit Druckplatte verlinken

```
1. Nimm eine Angelrute in die Hand
2. Wirf die Rute auf eine beliebige Druckplatte
3. Sobald der Haken die Platte berührt
   ✅ Automatische Verlinkung
   ✅ Bestätigungsnachricht
   ✅ Partikeleffekte (optional)
```

### 🔥 Single-Use Ruten erstellen

```
Schritt 1: Benenne die Rute um
  → Am Amboss oder mit /rename

Schritt 2: Name muss [1] enthalten
  → Format: [1] Ruten Name
  → Beispiel: [1] Einweg-Spezial

Schritt 3: Verlinke normal
  → Nach Nutzung wird die Rute zerstört
  → Sound & Effekte bestätigen Zerstörung
```

### 🔓 Rute entlinken

**Methode 1: Einholen**
```
1. Werfe die verlinkte Rute aus
2. Ziehe die Angelrute ein (rechtsklick halten)
3. Platte wird freigegeben
4. Rute erhält Haltbarkeitspönale (Mehrweg)
   oder wird zerstört (Single-Use)
```

**Methode 2: Crafting-Gitter**
```
1. Öffne Crafting-Gitter (E)
2. Platziere verlinkte Rute alleine im Gitter
3. Rute wird automatisch gereinigt
4. Kein Haltbarkeitsverlust, keine Zerstörung
```

---

## ⚙️ Konfiguration

**Datei:** `plugins/StasisRod/config.yml`

```yaml
# ═══════════════════════════════════════════════
# 🎣 StasisRod Plugin Konfiguration
# ═══════════════════════════════════════════════

# 🐛 Debug-Modus für Konsolen-Ausgaben
debug: false

# 💔 Haltbarkeitsverlust pro normaler Entlinkung (Mehrweg-Ruten)
#    Single-Use Ruten werden unabhängig davon zerstört
durability_penalty: 10

# ✨ Partikel-Einstellungen
particles:
  # Partikeleffekte aktivieren/deaktivieren
  enabled: true
  
  # Sichtradius für Spieler (in Blöcken)
  # Partikel werden nur erzeugt, wenn ein Spieler im Radius ist
  radius: 10.0

# 💬 System-Nachrichten (MiniMessage-Format)
# Unterstützt Farben: <green>, <red>, <gold>, <aqua>, etc.
messages:
  linked_success: "<green>[Stasis] Druckplatte erfolgreich verlinkt!"
  link_broken: "<red>[Stasis] Verlinkung wurde getrennt (Ziel nicht gefunden)."
```

---

## 🏗️ Technische Architektur

### Manager-System
```
┌─────────────────────────────────────────────┐
│       PlayerFishListener (Main)              │
│  ↙              ↓              ↘             │
RodDataManager  PressurePlateManager  AnchorManager
  (Datenspeicherung)  (Signal-Kontrolle)  (Entity-Verwaltung)
```

- **RodDataManager** - Verwaltet Verlinkungsdaten im ItemMeta (PDC)
- **PressurePlateManager** - Steuert Redstone-Signal und alle Listener
- **AnchorManager** - Erstellt/entfernt Marker-ArmorStands
- **ShimmerParticleTask** - Periodischer Task für Partikeleffekte (alle 0,5s)

### Sicherheitsfeatures
✅ Chunk-Ticket-System während Entlinkung  
✅ Crash-Sicherheit durch Fallback-Entity-Suche  
✅ Physics-Event-Blockierung  
✅ Entity-Interaktions-Blockierung  
✅ Anti-Exploit-Validierung im Crafting-Gitter  

---

## 📝 Versionskompatibilität

| Version | Status | Getestet | Notizen |
|---------|--------|----------|---------|
| **Paper 1.20.x** | ✅ Stabil | Ja (1.20.1) | Vollständig kompatibel |
| **Java 21** | ✅ Empfohlen | Ja | Compilation erfolgreich |
| Ältere Java-Versionen | ⏳ Prüfung | - | Unterstützung wird überprüft |

> ℹ️ Das Plugin wurde auf **Paper 1.20.2** entwickelt und getestet. Kompatibilität mit älteren Versionen wird derzeit evaluiert.

---

## 🐛 Bekannte Issues & Roadmap

### v1.0.0 (Aktuell)
| Status | Issue | Notizen |
|--------|-------|---------|
| ✅ | Basis-Verlinkung | Stabil |
| ✅ | Entlinkung | Stabil |
| ✅ | Single-Use Ruten | Stabil |
| ✅ | Partikeleffekte | Stabil |
| ⚠️ | Nether/End | Seltene Chunk-Load-Probleme |

### Geplant für v1.1.0
- 🔄 Nether/End-Kompatibilität verbessern
- 📊 Erweiterte Debug-Befehle
- 🎨 Zusätzliche Partikeltypen
- 📡 Optionale Plugin-Integrationen

### Zukünftig (v2.0.0+)
- 🎯 Multilink-Unterstützung
- 🌍 Multi-Welt-Systeme
- 💾 Optionale Datenbankpersistenz
- 🔧 Erweiterte Ruten-Typen

---

## 👥 Credits

### 🖥️ Programmierung & Development
**EinMensch002**
- Plugin-Architektur & Core-Mechaniken
- Manager-System
- Event-Handling & Redstone-Logik
- Testing & Optimization

### 🎨 Ressourcen & Design
**Thorny Devel Studio**
- Custom Texturen & Modelle
- Ressource Pack Design
- Visual Effects & Branding
- Asset Creation

---

## 📄 Lizenz

```
MIT License

Copyright (c) 2024 EinMensch002 & Thorny Devel Studio

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

Vollständige Lizenz: [LICENSE](LICENSE)

---

## 🤝 Support & Kontakt

### 📬 Probleme gefunden?
- **[Issues](../../issues)** - Bug Reports & Feature Requests
- **[Discussions](../../discussions)** - Fragen & Community

### 🔗 Links
- **Repository**: [EinMensch002/Rod_Link](https://github.com/EinMensch002/Rod_Link)
- **Changelog**: [CHANGELOG.md](CHANGELOG.md)
- **Ressource Pack Status**: [RESOURCEPACK_STATUS.md](RESOURCEPACK_STATUS.md)

---

## 📊 Projekt-Status

![Development](https://img.shields.io/badge/Status-Stable-brightgreen?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0.0-blue?style=flat-square)
![Tests](https://img.shields.io/badge/Tested-Paper%201.20.2-green?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## 🎮 Gameplay-Beispiele

### Beispiel 1: Redstone-Schleife
```
Angelrute → Druckplatte → Redstone-Leitungen → Lampe
           (Dauersignal 15)
           ↓
       Lampe leuchtet IMMER
```

### Beispiel 2: Quest-Reward
```
[1] Quest-Angel werfen → Platte aktiviert
                         ↓
                    Quest-Mechanik startet
                         ↓
                    Rute wird zerstört
```

### Beispiel 3: Custom-Map-Puzzle
```
3 Angelruten auf 3 verschiedene Platten
                    ↓
          Alle Signale aktiv?
                    ↓
          Tür-Mechanik entsperrt
```

---

<div align="center">

### 🎣 Viel Spaß mit StasisRod! ✨

![Made with Java](https://img.shields.io/badge/Made%20with-Java-orange?style=flat-square)
![Paper API](https://img.shields.io/badge/Paper-API-red?style=flat-square)
![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-green?style=flat-square)

**[⬆ Nach oben](#-stasisrod---fishing-rod-link-plugin)**

</div>
