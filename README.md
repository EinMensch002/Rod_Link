# RodLink

Ein Paper-Plugin für Minecraft, mit dem man Angeln mit Redstone-Druckplatten verlinkt. Die verlinkte Druckplatte gibt dauerhaft Signalstärke 15 ab, egal ob jemand darauf steht. Die Verlinkung steckt in der Angel selbst (nicht im Spieler), die Angel lässt sich also weitergeben, tauschen oder als Schlüssel in einer Map verwenden.

> **Stand:** Version 1.0.1 (in Arbeit). Entwickelt und getestet 2026 auf **Paper 26.2** mit **Java 21**.
> Andere Minecraft- oder Paper-Versionen habe ich nicht getestet. Es kann dort funktionieren, muss aber nicht.
> Was genau getestet ist und was nicht, steht weiter unten im Abschnitt "Teststand".

---

## Was das Plugin macht

- Wirf eine Angel auf eine Druckplatte und ziehe sie zurück. Die Angel ist jetzt mit der Platte verlinkt, die Platte bleibt dauerhaft an.
- An der Platte wird ein unsichtbarer Marker (ArmorStand) gesetzt. Er hält die Verlinkung fest und überlebt Chunk-Wechsel.
- Wird die Platte abgebaut oder zerstört, verschwindet der Marker. Die Angel meldet dann beim nächsten Benutzen, dass keine Verlinkung mehr besteht.
- Funktioniert mit allen Druckplatten, auch den gewichteten (Gold und Eisen). Andere Blöcke (Türen, Hebel, Knöpfe usw.) werden nicht verlinkt.
- Eine Platte kann immer nur mit einer Angel gleichzeitig verlinkt sein.

---

## Bedienung

**Verlinken:** Wirf die Angel so, dass der Haken auf der Druckplatte landet, und ziehe sie zurück. Du bekommst eine Bestätigung im Chat.

**Entlinken, Variante 1 (Angel benutzen):** Die verlinkte Angel einmal verwenden. Die Platte wird freigegeben. Dabei verliert die Angel Haltbarkeit (Standard: 10, einstellbar). Würde sie dadurch kaputtgehen, zerbricht sie.

**Entlinken, Variante 2 (Crafting-Gitter):** Die verlinkte Angel allein ins Crafting-Gitter legen und das Ergebnis nehmen. Du bekommst die normale Angel mit allen Verzauberungen und der ursprünglichen Haltbarkeit zurück, ohne Abzug.

**Einweg-Angeln:** Steht `[1]` im Namen (zum Beispiel `Schlüssel [1]`), wird die Angel beim Entlinken über Variante 1 immer zerstört. Beim Verlinken wird die Haltbarkeit auf 1 gesetzt, das `[1]` aus dem Namen entfernt. Über das Crafting-Gitter lässt sie sich ohne Kosten zurückholen, danach ist es aber eine normale Angel.

---

## Resource Pack (optional)

Das Pack liegt im Ordner `Rod_Link_ResourcePack` und ist komplett freiwillig. Ohne Pack sieht man ganz normale Angeln. Die Texturen stammen von Thorny Devil Studio.

Das Plugin setzt beim Verlinken je nach Namen der Angel ein Custom Model Data (der Name wird beim Verlinken geprüft, späteres Umbenennen ändert nichts mehr):

| Name enthält (Groß-/Kleinschreibung egal) | Custom Model Data | Aussehen |
|---|---|---|
| `stasis` | 1001 | Angel mit Enderperle |
| `tnt`, `orbital`, `snapshot`, `nuke`, `stabshot` | 1002 | Angel mit TNT |
| `trap`, `falle` | 1003 | Angel mit Redstone |

Wird die Angel entlinkt, wird das Custom Model Data wieder entfernt. Mehr dazu in [RESOURCEPACK_STATUS.md](RESOURCEPACK_STATUS.md).

---

## Installation

1. `.jar`-Datei aus den [Releases](../../releases) laden.
2. In den Ordner `plugins/` des Servers legen.
3. Server starten. Die Konfiguration liegt danach unter `plugins/RodLink/config.yml`.

Benötigt wird Paper 26.2 und Java 21.

---

## Konfiguration

`plugins/RodLink/config.yml`:

```yaml
debug: false              # ausführliche Konsolen-Ausgaben
durability_penalty: 10    # Haltbarkeitsverlust beim Entlinken mit der Angel

particles:
  enabled: true           # Partikel an verlinkten Platten
  radius: 10.0            # nur sichtbar, wenn ein Spieler in diesem Umkreis ist

messages:                 # MiniMessage-Format
  linked_success: "<green>[RodLink] Druckplatte erfolgreich verlinkt!"
  link_broken: "<red>[RodLink] Verlinkung wurde getrennt (Ziel nicht gefunden)."
  already_linked: "<red>[RodLink] Diese Druckplatte ist bereits verlinkt."
```

Beim Update von einer älteren Version wird eine bestehende `config.yml` nicht automatisch ergänzt. Fehlende Nachrichten nutzen dann einen Standardtext, du kannst die Einträge aber von Hand nachtragen.

---

## Teststand (Version 1.0.1)

Alles auf Paper 26.2, Testserver.

| Was | Stand |
|---|---|
| Verlinken und Entlinken, alle Druckplattentypen, Signalstärke 15 | getestet |
| Platte abbauen, während sie verlinkt ist | getestet |
| Entlinken über das Crafting-Gitter | getestet |
| Mehrere Angeln auf einer Platte, Angel in Haupt- oder Nebenhand | getestet |
| Custom Model Data setzen und zurücksetzen | getestet |
| Server-Neustart (Verlinkung bleibt erhalten) | noch nicht getestet |
| Entlinken bei weit entfernter, nicht geladener Platte | noch nicht getestet |
| Nether und End | in 1.0.0 lief es, in 1.0.1 nicht erneut getestet |
| Andere Minecraft-/Paper-Versionen | nicht getestet |

---

## Bekannte Einschränkungen und Pläne

- Geht eine verlinkte Angel verloren (Tod, Lava, Kiste weggeworfen), bleibt die Platte an, bis man sie abbaut. Beim Abbauen wird alles sauber aufgeräumt.
- Geplant: Beim Verlinken warnen, wenn die Angel beim Entlinken zerbrechen würde (mit Hinweis auf das Crafting-Gitter).
- Idee, noch offen: Entlinken ohne Haltbarkeitsverlust, wenn man in der Nähe der Platte ist.
- Offene Fehler und Verbesserungen stehen in den [Issues](../../issues).

---

## Aufbau (kurz)

- `PlayerFishListener`: Hauptlogik für Verlinken und Entlinken
- `PrepareItemCraftListener`: Entlinken über das Crafting-Gitter
- `RodDataManager`: Verlinkungsdaten in der Angel (PersistentDataContainer)
- `PressurePlateManager`: hält das Signal, räumt bei zerstörten Platten auf, stellt Verlinkungen nach dem Laden von Chunks wieder her
- `AnchorManager`: Marker-ArmorStands
- `ShimmerParticleTask`: Partikel an verlinkten Platten

---

## Credits

- **EinMensch002**: Plugin und Code
- **Thorny Devil Studio**: Texturen für das Resource Pack

## Lizenz

MIT, siehe [LICENSE](LICENSE). Änderungen stehen im [CHANGELOG](CHANGELOG.md).
