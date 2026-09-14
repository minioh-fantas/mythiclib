package io.lumine.mythic.lib.module;

import io.lumine.mythic.lib.util.annotation.NotUsed;

import java.util.function.Supplier;

@NotUsed
public enum MythicLibModuleEnum {


    HEALTH_SCALE(null),
    ;

    private Supplier<Module> instanciator;

    MythicLibModuleEnum(Supplier<Module> instanciator) {
        this.instanciator = instanciator;
    }
}
