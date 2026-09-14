package io.lumine.mythic.lib.script.mechanic.misc;

import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.MechanicMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;

@MechanicMetadata
public class CloseInventoryMechanic extends Mechanic {
    public CloseInventoryMechanic(ConfigObject config) {
    }

    @Override
    public void cast(SkillMetadata meta) {
        meta.getCaster().getPlayer().closeInventory();
    }
}
