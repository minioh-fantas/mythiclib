package io.lumine.mythic.lib.command.api;

import io.lumine.mythic.lib.util.annotation.NotUsed;

@NotUsed
@Deprecated
public @interface RootCommand {

    public String name();

    public String[] aliases() default {};

    public String perm() default "";

    public String description();
}
