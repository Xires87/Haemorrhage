# Haemorrhage
Makes player get Bleeding status effect after taking damage and adds bandages.
Length of bleeding depends on damage taken and players armor.

------------------------------------------------------------

### [CurseForge](https://www.curseforge.com/minecraft/mc-mods/haemorrhage)
### [Modrinth](https://modrinth.com/mod/haemorrhage)

------------------------------------------------------------

# Credits:
* Thanks to **sheslong** for making textures

------------------------------------------------------------

# Datapacks:
Use following tags for:
* excluding mobs from giving Bleeding: `imbleeding/data/tags/entity_type/no_bleeding_apply_mobs.json`
* selecting which damage types should apply Bleeding: `imbleeding/data/tags/damage_type/damage_apply_bleed.json`
* selecting which projectiles cause Bleeding (damage type of projectile must be in damage_apply_bleed tag to work properly): `imbleeding/data/tags/entity_type/projectiles_causing_bleeding.json`
* selecting which mobs get Bleeding after taking damage: `imbleeding/data/tags/entity_type/can_bleed.json`
* selecting which mobs are immune to Bleeding: `imbleeding/data/tags/entity_type/bleed_resistant_to.json`
* selecting which damage types should apply Health Loss: `imbleeding/data/tags/damage_type/damage_apply_health_loss.json`
* selecting which mobs apply Health Loss: `imbleeding/data/tags/entity_type/projectiles_causing_bleeding.json`
* selecting which mobs are immune to Health Loss: `imbleeding/data/tags/entity_type/health_loss_resistant_to.json`
* selecting which items ignore some part of bleeding protection: `imbleeding/data/tags/item/weapons_pierce_bleeding_protection.json`
* ~~making items remove~~:
  1. ~~Bleeding: `imbleeding/data/tags/item/items_remove_bleeding.json`~~
  2. ~~Bleedout: `imbleeding/data/tags/item/items_remove_bleedout.json`~~
  3. ~~Health Loss: `imbleeding/data/tags/item/items_remove_health_loss.json`~~
  4. ~~Broken: `imbleeding/data/tags/item/items_remove_broken.json`~~
* ^^ see [https://github.com/Xires87/UnremovableEffects](https://github.com/Xires87/UnremovableEffects?tab=readme-ov-file#making-items-remove-selected-status-effects)