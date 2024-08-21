# Haemorrhage
Makes player get Bleeding status effect after taking damage and adds bandages.
Length of bleeding depends on damage taken and players armor.

------------------------------------------------------------

# [CurseForge](https://www.curseforge.com/minecraft/mc-mods/haemorrhage)

------------------------------------------------------------

# Credits:
* Thanks to **sheslong** for making textures

------------------------------------------------------------

# Datapacks:
Use following tags for:
* excluding mobs from giving Bleeding: `imbleeding/data/tags/entity_type/no_bleeding_apply_mobs.json`
* selecting projectiles that cause Bleeding (won't work if projectile deals fire damage): `imbleeding/data/tags/entity_type/projectiles_causing_bleeding.json`
* making items remove:
  1. Bleeding: `imbleeding/data/tags/item/items_remove_bleeding.json`
  2. Bleedout: `imbleeding/data/tags/item/items_remove_bleedout.json`
  3. Health Loss: `imbleeding/data/tags/item/items_remove_health_loss.json`
  4. Broken: `imbleeding/data/tags/item/items_remove_broken.json`

(remember that some directories changed with 1.21, for example `items` is now `item`)