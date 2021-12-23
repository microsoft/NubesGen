package io.github.nubesgen.cli.util;

import io.github.nubesgen.cli.Nubesgen;
import picocli.CommandLine;

public class Output {
    
    public static void printTitle(String title) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|green,bold " + title + "|@"));
    }

    public static void printError(String message) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|red,bold " + message + "|@"));
    }

    public static void printInfo(String message) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|yellow " + message + "|@"));
    }

    public static void printMessage(String message) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string(message));
    }

    public static void printVerbose(String message) {
        if (Nubesgen.verbose) {
            System.out.println(CommandLine.Help.Ansi.AUTO.string("@|italic " + message + "|@"));
        }
    }
}
