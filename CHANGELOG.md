# Changelog - RodLink

Alle nennenswerten Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

---

## [1.0.0] - 2026 (Initial Release)

### ✨ Features
- 🎣 **Angelruten-Verlinkung**: Werfe Angelruten auf Druckplatten zum Verlinken
- 🔌 **Dauerhaftes Redstone-Signal**: Verlinkte Platten geben permanent Signalstärke 15 ab
- 📍 **Anker-System**: Unsichtbare ArmorStand-Marker für Positionsverwaltung
- 💧 **Partikeleffekte**: Shimmer-Effekte an verlinkten Platten (konfigurierbar)
- 🎯 **Single-Use Ruten**: Ruten mit `[1]` im Namen werden nach Nutzung zerstört
- 🛠️ **Crafting-Entlinkung**: Verlinkte Ruten im Crafting-Gitter automatisch bereinigen
- 💾 **Persistente Daten**: Alle Verlinkungen bleiben nach Server-Restart erhalten
- ⚙️ **CustomModelData**: Automatische Zuweisung basierend auf Ruten-Namen (Stasis/Orbital/Trap)
- 🎛️ **Vollständig konfigurierbar**: Debug-Mode, Haltbarkeitspönale, Partikelradius uvm.

### 🔧 Technische Implementierung
- **Manager-System**:
  - `RodDataManager` - Persistente Datenverwaltung im ItemMeta (PDC)
  - `PressurePlateManager` - Redstone-Signal-Kontrolle und Event-Handling
  - `AnchorManager` - ArmorStand-Verwaltung mit Fallback-Mechaniken
  - `ShimmerParticleTask` - Periodischer Timer für Partikeleffekte

- **Event-Listener**:
  - `PlayerFishListener` - Hauptlogik für Verlinkung/Entlinkung
  - `PrepareItemCraftListener` - Anti-Exploit und Crafting-Bereinigung

- **Sicherheitsfeatures**:
  - ✅ Chunk-Ticket-System während Entlinkung
  - ✅ Crash-Sicherheit durch Fallback-Entity-Suche
  - ✅ Physics-Event-Blockierung
  - ✅ Entity-Interaktions-Blockierung
  - ✅ Anti-Exploit-Validierung im Crafting-Gitter

### 📦 Abhängigkeiten
- Paper API 1.20+
- Java 17+

### 🐛 Bekannte Probleme
- ⚠️ Seltene Chunk-Load-Probleme in der Nether/End-Dimension (wird beobachtet)
- ⚠️ Entity-Fallback-Suche kann bei extrem vielen Entities performant beeinträchtigt sein

### 🙏 Credits
- **Programmierung**: EinMensch002
- **Ressourcen & Design**: Thorny Devel Studio

### 📝 Anmerkungen
Dies ist die erste offizielle Release-Version. Das Plugin wurde auf **Paper 1.20.1** getestet und ist einsatzbereit, wird jedoch zeitnah auf weiteren Plattformen und Versionen validiert.

---

## Geplante Versionen

### [1.1.0] - Geplant
- 🔄 Verbesserter Nether/End-Support
- 📡 Optionale Plugin-Integrationen
- 🎨 Zusätzliche Partikeltypen
- 🔍 Erweiterte Debug-Befehle

### [2.0.0] - Zukünftig
- 🎯 Multilink-Unterstützung (mehrere Platten pro Rute)
- 🎮 Erweiterte Ruten-Typen
- 🌐 Multi-Welt-Kompatibilität
- 💾 Optionale Datenbankpersistenz

---

**Letzte Aktualisierung**: v1.0.0 Initial Release
