package org.soraworld.violet.command;

import org.bukkit.command.CommandSender;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class SpigotCommandTest {

    @Sub
    public void test(CommandSender sender, CommandArgs args) {
    }

    @Sub
    public void test2(CommandSender sender, CommandArgs args) {
    }

    @Sub
    public static void test3(CommandSender sender, CommandArgs args) {
    }

    @Sub
    protected void test4(CommandSender sender, CommandArgs args) {
    }

    @Sub
    protected static void test5(CommandSender sender, CommandArgs args) {
    }

    @Test
    public void extractSub() {
        SpigotCommand command=new SpigotCommand("sss",null,false,null);
        command.extractSub(this);
        System.out.println(command);
    }
}