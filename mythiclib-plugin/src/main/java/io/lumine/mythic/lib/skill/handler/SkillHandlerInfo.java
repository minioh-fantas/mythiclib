package io.lumine.mythic.lib.skill.handler;

public @interface SkillHandlerInfo {
    public String plugin() default "mythiclib";

    public String author() default "teamrequiem";

    public String version() default "1.0";
}
