package com.add.discord.bot.addonmanagers.annotations.sql;

import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CreateTable {
    String name();
}