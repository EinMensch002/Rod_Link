v0.1.0 (Pack für Minecraft 26.2, Resource-Pack-Format 88)

- pack.mcmeta korrigiert: min_format/max_format 88 statt pack_format 26.2
- Item-Definition assets/minecraft/items/fishing_rod.json: wählt das Modell über Custom Model Data
  - 1001 -> Enderperle (Stasis)
  - 1002 -> TNT (TNT, Orbital, Snapshot, Nuke)
  - 1003 -> Redstone (Trap, Falle)
  - alles andere -> normale Vanilla-Angel (inklusive ausgeworfen-Zustand)
- Modelle in assets/rod_link/models/item/
- Texturen in den eigenen Namespace verschoben: assets/rod_link/textures/item/
- Überflüssige Datei "order" entfernt

Ohne Plugin testen (im Spiel):
  /give @s fishing_rod[custom_model_data={floats:[1001]}]
  /give @s fishing_rod[custom_model_data={floats:[1002]}]
  /give @s fishing_rod[custom_model_data={floats:[1003]}]

Bei neuen Minecraft-Versionen min_format/max_format in der pack.mcmeta anpassen.
