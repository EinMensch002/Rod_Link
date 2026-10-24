# Resource Pack Status

Aktuelle Version: v0.1.0, für Minecraft 26.2 (Resource-Pack-Format 88). Das Pack ist optional.

## Inhalt
- `assets/minecraft/items/fishing_rod.json`: wählt das Modell über Custom Model Data
  - 1001: Angel mit Enderperle (Name enthält `stasis`)
  - 1002: Angel mit TNT (Name enthält `tnt`, `orbital`, `snapshot`, `nuke` oder `stabshot`)
  - 1003: Angel mit Redstone (Name enthält `trap` oder `falle`)
  - alles andere: normale Vanilla-Angel, auch im ausgeworfenen Zustand
- `assets/rod_link/models/item/`: die drei Modelle
- `assets/rod_link/textures/item/`: die drei Texturen (96x96 Pixel)

## Teststand
- Modelle werden auf dem Testserver mit dem Plugin angezeigt (Paper 26.2): getestet
- Nach dem Entlinken ist wieder die normale Angel zu sehen: getestet

## Ohne Plugin testen
```
/give @s fishing_rod[custom_model_data={floats:[1001]}]
/give @s fishing_rod[custom_model_data={floats:[1002]}]
/give @s fishing_rod[custom_model_data={floats:[1003]}]
```

## Hinweis
Bei einer neuen Minecraft-Version muss in der `pack.mcmeta` `min_format` und `max_format` angepasst werden.
Texturen: Thorny Devil Studio.

## Versionen
- v0.1.0: Item-Definition, Modelle, Namespace `rod_link`, `pack.mcmeta` korrigiert
- v0.0.1: pack.mcmeta, pack.png und die drei Texturen
