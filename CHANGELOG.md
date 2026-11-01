# Changelog

Alle nennenswerten Änderungen an RodLink. Neueste Version oben.

---

## [1.0.1] - in Arbeit

Fehlerbehebungen nach einem Code-Review. Alles wurde auf Paper 26.2 getestet, außer es steht ausdrücklich dabei, dass es noch offen ist.

### Behoben
- Verlinkte Druckplatte wird zerstört: Marker und Verlinkung werden jetzt entfernt, die Platte darf bei fehlendem Halt wieder abfallen (#1, getestet)
- Entlinken über das Crafting-Gitter gibt Marker und Platte frei, sobald die Angel entnommen wird, nicht schon in der Vorschau (#2, getestet)
- Verlinkungen werden nach einem Server-Neustart aus den Markern wiederhergestellt, Marker ohne Platte werden entfernt (#3, getestet)
- Keine ungewollten Chunk-Ladungen mehr, das Chunk-Ticket beim Entlinken wird wieder freigegeben, der Chunk wird dafür asynchron geladen (#4, noch nicht getestet)
- Custom Model Data wird beim Entlinken zurückgesetzt (#5, getestet)
- Nur echte Druckplatten werden verlinkt (keine Türen, Hebel usw.), gewichtete Platten funktionieren mit Signalstärke 15 (#6, getestet)
- Es wird die Angel der Hand verwendet, die das Event ausgelöst hat (#7, getestet)
- Eine Druckplatte kann nur mit einer Angel gleichzeitig verlinkt sein (#8, getestet)
- `groupId` des `maven-compiler-plugin` in der `pom.xml` korrigiert (#9)
- Redstone-Bauteile am Block unter der Platte (Fackeln, Kolben usw.) werden beim Verlinken und Entlinken jetzt aktualisiert (#11, noch nicht getestet)

### Geändert
- Nachrichten-Präfix von `[Stasis]` auf `[RodLink]`
- Die Schlüsselwörter `tnt` und `snapshot` lösen jetzt ebenfalls das TNT-Modell (1002) aus
- Neue Config-Nachricht `messages.already_linked`
- Paket von `de.example.plugin` auf `io.github.einmensch002.rodlink` umbenannt
- Lizenz: Public Domain (Unlicense)
- README und Changelog überarbeitet, mit ehrlichem Teststand

### Resource Pack (v0.1.0)
- Item-Definition und Modelle für die drei Angel-Varianten (1001 Enderperle, 1002 TNT, 1003 Redstone)
- `pack.mcmeta` korrigiert: Format 88 (Minecraft 26.2) über `min_format` und `max_format`
- Texturen in den Namespace `rod_link` verschoben

### Hinweis zum Update
Eine bestehende `config.yml` wird nicht automatisch ergänzt. Die Nachrichten mit `[Stasis]` kannst du von Hand auf `[RodLink]` ändern und `already_linked` nachtragen.

---

## [1.0.0] - Pre-Release

Erste veröffentlichte Version, auf Paper 26.2 entwickelt. Sie lief, war aber fehleranfällig (siehe unten, alles in 1.0.1 behoben).

### Enthalten
- Angel auf eine Druckplatte werfen verlinkt sie, die Platte gibt dauerhaft Signalstärke 15
- Unsichtbare ArmorStand-Marker an der Platte
- Entlinken über Benutzen der Angel (mit Haltbarkeitsverlust) oder über das Crafting-Gitter
- Einweg-Angeln über `[1]` im Namen
- Partikel an verlinkten Platten (konfigurierbar)
- Custom Model Data anhand von Schlüsselwörtern im Angelnamen (1001, 1002, 1003)
- Konfigurierbar: Debug-Modus, Haltbarkeitsverlust, Partikel, Nachrichten

### Bekannte Fehler in dieser Version
- Wurde die Platte abgebaut, blieben Marker und Verlinkung bestehen
- Crafting-Entlinken ließ Marker und Signal stehen
- Verlinkungen gingen beim Server-Neustart verloren
- Chunks wurden ungewollt geladen, ein Chunk-Ticket wurde nie freigegeben
- Custom Model Data blieb nach dem Entlinken erhalten
- Es wurden auch Türen, Hebel und Ähnliches verlinkt, gewichtete Platten gingen nicht
- Bei Angeln in beiden Händen konnte die falsche Angel geprüft werden
- Eine Platte konnte von mehreren Angeln gleichzeitig verlinkt werden

---

## Geplant (ohne Zeitplan)
- Warnung beim Verlinken, wenn die Angel beim Entlinken zerbrechen würde
- Entlinken ohne Haltbarkeitsverlust in der Nähe der Platte (Idee, noch offen)
