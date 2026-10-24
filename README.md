# 🎣 RodLink - Fishing Rod Link Plugin

![RodLink](https://img.shields.io/badge/RodLink-v1.0.1-blue?style=for-the-badge&logo=java)
![Paper](https://img.shields.io/badge/Paper-26.2-red?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)

> Ein Paper-Plugin, das Angeln mit Redstone-Druckplatten verlinkt. Die Platte gibt dauerhaft ein Signal ab, die Verlinkung steckt in der Angel selbst.

**Stand:** Version 1.0.1 (in Arbeit). Entwickelt und getestet 2026 auf **Paper 26.2** mit **Java 21**.
Andere Minecraft- oder Paper-Versionen sind nicht getestet. Es kann dort funktionieren, muss aber nicht.

---

## 📋 Inhaltsverzeichnis
- [Übersicht](#-übersicht)
- [Features](#-features)
- [Installation](#-installation)
- [Bedienung](#-bedienung)
- [Resource Pack](#-resource-pack)
- [Konfiguration](#-konfiguration)
- [Teststand](#-teststand)
- [Bekannte Einschränkungen](#-bekannte-einschränkungen)
- [Credits](#-credits)
- [Projekt-Status](#-projekt-status)

---

## 📖 Übersicht

Mit RodLink wirfst du eine Angel auf eine Druckplatte und ziehst sie zurück. Danach bleibt die Platte dauerhaft an (Signalstärke 15), egal ob jemand darauf steht. Die Verlinkung steckt in der Angel und nicht im Spieler. Die Angel lässt sich also weitergeben, tauschen oder in einer Map als Schlüssel verwenden.

Mögliche Einsatzzwecke sind zuverlässige Signalquellen für Redstone-Maschinen, Adventure-Maps und Rätsel.

---

## ✨ Features

- Verlinken mit allen Druckplatten, auch den gewichteten (Gold und Eisen). Türen, Hebel, Knöpfe und Ähnliches werden nicht verlinkt.
- Eine Platte kann immer nur mit einer Angel gleichzeitig verlinkt sein.
- Ein unsichtbarer Marker (ArmorStand) hält die Verlinkung fest. Wird die Platte abgebaut oder zerstört, verschwindet er.
- Entlinken durch Benutzen der Angel (mit Haltbarkeitsverlust) oder über das Crafting-Gitter (ohne Verlust).
- Einweg-Angeln über `[1]` im Namen.
- Partikel an verlinkten Platten (einstellbar).
- Optionales Resource Pack: je nach Namen der Angel ein anderes Aussehen.

---

## 🚀 Installation

### ✅ Anforderungen
- Paper 26.2
- Java 21
- ⚠️ Andere Versionen sind nicht getestet

### Schritte
1. `.jar`-Datei aus den [Releases](../../releases) laden.
2. In den Ordner `plugins/` des Servers legen.
3. Server starten. Die Konfiguration liegt danach unter `plugins/RodLink/config.yml`.

---

## 🎮 Bedienung

**Verlinken:** Wirf die Angel so, dass der Haken auf der Druckplatte landet, und ziehe sie zurück. Du bekommst eine Bestätigung im Chat.

**Entlinken über Benutzen:** Die verlinkte Angel einmal verwenden. Die Platte wird freigegeben und die Angel verliert Haltbarkeit (Standard: 10, einstellbar). Würde sie dadurch kaputtgehen, zerbricht sie.

**Entlinken über das Crafting-Gitter:** Die verlinkte Angel allein ins Crafting-Gitter legen und das Ergebnis nehmen. Du bekommst die normale Angel mit allen Verzauberungen und der ursprünglichen Haltbarkeit zurück, ohne Abzug.

**Einweg-Angeln:** Steht `[1]` im Namen (zum Beispiel `Schlüssel [1]`), wird die Angel beim Entlinken über Benutzen immer zerstört. Beim Verlinken wird das `[1]` aus dem Namen entfernt und die Haltbarkeit auf 1 gesetzt. Über das Crafting-Gitter bekommst du sie kostenlos zurück, dann ist es aber eine normale Angel.

---

## 🎨 Resource Pack

Das Pack liegt im Ordner `Rod_Link_ResourcePack` und ist komplett freiwillig. Ohne Pack sehen die Angeln normal aus. Die Texturen stammen von Thorny Devil Studio.

Das Plugin setzt beim Verlinken je nach Namen ein Custom Model Data. Der Name wird nur beim Verlinken geprüft, späteres Umbenennen ändert nichts mehr.

| Name enthält (Groß-/Kleinschreibung egal) | Custom Model Data | Aussehen |
|---|---|---|
| `stasis` | 1001 | Angel mit Enderperle |
| `tnt`, `orbital`, `snapshot`, `nuke`, `stabshot` | 1002 | Angel mit TNT |
| `trap`, `falle` | 1003 | Angel mit Redstone |

Beim Entlinken wird das Custom Model Data wieder entfernt. Mehr dazu in [RESOURCEPACK_STATUS.md](RESOURCEPACK_STATUS.md).

---

## ⚙️ Konfiguration

`plugins/RodLink/config.yml`:

```yaml
debug: false              # ausführliche Konsolen-Ausgaben
durability_penalty: 10    # Haltbarkeitsverlust beim Entlinken über Benutzen

particles:
  enabled: true           # Partikel an verlinkten Platten
  radius: 10.0            # nur sichtbar, wenn ein Spieler in diesem Umkreis ist

messages:                 # MiniMessage-Format
  linked_success: "<green>[RodLink] Druckplatte erfolgreich verlinkt!"
  link_broken: "<red>[RodLink] Verlinkung wurde getrennt (Ziel nicht gefunden)."
  already_linked: "<red>[RodLink] Diese Druckplatte ist bereits verlinkt."
```

Beim Update wird eine bestehende `config.yml` nicht automatisch ergänzt. Fehlende Nachrichten nutzen dann einen Standardtext. Du kannst die Einträge aber von Hand nachtragen.

---

## 🧪 Teststand

Version 1.0.1, alles auf einem Testserver mit Paper 26.2.

| Was | Stand |
|---|---|
| Verlinken und Entlinken, alle Druckplattentypen, Signalstärke 15 | ✅ getestet |
| Platte abbauen, während sie verlinkt ist | ✅ getestet |
| Entlinken über das Crafting-Gitter | ✅ getestet |
| Mehrere Angeln auf einer Platte, Angel in Haupt- oder Nebenhand | ✅ getestet |
| Custom Model Data setzen und zurücksetzen, Resource Pack | ✅ getestet |
| Server-Neustart (Verlinkung bleibt erhalten) | ⚠️ noch nicht getestet |
| Entlinken bei weit entfernter, nicht geladener Platte | ⚠️ noch nicht getestet |
| Nether und End | ⚠️ lief in 1.0.0, in 1.0.1 nicht erneut getestet |
| Andere Minecraft-/Paper-Versionen | ⚠️ nicht getestet |

---

## 🐛 Bekannte Einschränkungen

- Geht eine verlinkte Angel verloren (Tod, Lava, Kiste weggeworfen), bleibt die Platte an, bis man sie abbaut. Beim Abbauen wird alles sauber aufgeräumt.
- Geplant: Beim Verlinken warnen, wenn die Angel beim Entlinken zerbrechen würde (mit Hinweis auf das Crafting-Gitter).
- Idee, noch offen: Entlinken ohne Haltbarkeitsverlust, wenn man in der Nähe der Platte ist.
- Offene Fehler und Verbesserungen stehen in den [Issues](../../issues). Änderungen findest du im [Changelog](CHANGELOG.md).

---

## 👥 Credits

- **EinMensch002**: Plugin und Code
- **Thorny Devil Studio**: Texturen für das Resource Pack

Das Projekt ist frei. Jeder darf mitarbeiten und den Code für beliebige Zwecke verwenden. Repository: [EinMensch002/Rod_Link](https://github.com/EinMensch002/Rod_Link)

---

## 📊 Projekt-Status

![Status](https://img.shields.io/badge/Status-Stable-brightgreen?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0.1-blue?style=flat-square)
![Tested On](https://img.shields.io/badge/Tested%20On-Paper%2026.2-green?style=flat-square)
