package net.kozelka.args;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.*;
import net.kozelka.args.annotation.SubCommand;
import net.kozelka.args.api.MetaCommand;
import net.kozelka.args.model.CommandlineDeclaration;
import net.kozelka.args.model.OptionDeclaration;
import net.kozelka.args.model.ParamDeclaration;
import net.kozelka.args.model.SubCommandDeclaration;

/**
 * Usecases:
 * main help - list of commands:
 * javaapp help
 * javaapp BADCMD
 * detailed help - synopsis of one command
 * javaapp help SOMECMD
 * javaapp --help SOMECMD
 * @todo support also: javaapp SOMECMD --help
 */
@SubCommand(name = "help", aliases = {"--help", "?", "h"}, description = "shows help for the whole app. or for specified command")
public class DefaultHelpCommand implements MetaCommand {
    private CommandlineDeclaration declaration = null;
    private final String command;
    private PrintStream out = System.err;

    public DefaultHelpCommand(String... params) {
        this.command = params.length == 0 ? null : params[0];
    }

    public void setDeclaration(CommandlineDeclaration declaration) {
        this.declaration = declaration;
    }

    public Integer call() throws Exception {
        if (command == null) {
            globalHelp();
            return 0;
        } else {
            final SubCommandDeclaration cmdDecl = declaration.lookupCommand(command, false);
            if (cmdDecl == null) {
                throw new ParseException(command + ": unknown subcommand - cannot help", 0);
            }
            commandHelp(cmdDecl);
            return 0;
        }
    }

    private void commandHelp(SubCommandDeclaration cmdDecl) {
        final String programName = declaration.getProgramName();
        final String shortDesc = cmdDecl.getDescription();
        out.println("Subcommand '" + cmdDecl + "': " + shortDesc);
        out.println();
        out.println("Usage:");
        final StringBuilder usage = new StringBuilder("   ");
        usage.append(programName);
        usage.append(" ");
        usage.append(cmdDecl.getName());
        usage.append(" [options]");
        usage.append(paramSynopsis(cmdDecl.getParamDeclarations()));
        out.println(usage);

        showValidOptions(cmdDecl.getOptionDeclarations(), false);
        showValidOptions(cmdDecl.getOptionDeclarations(), true);
    }

    private void showValidOptions(Collection<OptionDeclaration> options, boolean global) {
        out.println();
        final List<OptionDeclaration> sortedOptions = new ArrayList<OptionDeclaration>(options);
        if (!sortedOptions.isEmpty()) {
            Collections.sort(sortedOptions, new Comparator<OptionDeclaration>() {
                public int compare(OptionDeclaration o1, OptionDeclaration o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            final Map<String, String> optMap = new LinkedHashMap<String, String>();
            int max = 0;
            // gather option table, and find longest declaration
            for (OptionDeclaration optionDeclaration : sortedOptions) {
                if (optionDeclaration.isGlobal() != global) continue;
                final String strDecl = optionDeclaration.toString() + paramSynopsis(optionDeclaration.getParamDeclarations());
                if (strDecl.length() > max) {
                    max = strDecl.length();
                }
                final String desc = optionDeclaration.getDescription();
                optMap.put(strDecl, desc);
            }
            if (!optMap.isEmpty()) {
                out.println(global ? "Global options:" : "Valid options:");
                // print it
                max += 2; // two more spaces
                final String fmt = "%-" + max + "s";
                printTable(optMap, fmt);
            }
        }

        //todo: add verbose description - from .txt resource located next to .class
    }

    private void printTable(Map<String, String> optMap, String fmt) {
        for (Map.Entry<String, String> entry : optMap.entrySet()) {
            out.print("   ");
            out.print(String.format(fmt, entry.getKey()));
            if (entry.getValue() != null) {
                out.print(entry.getValue());
            }
            out.println();
        }
    }

    private static String paramSynopsis(List<ParamDeclaration> paramDeclarations) {
        final StringBuilder sb = new StringBuilder();
        for (ParamDeclaration paramDecl : paramDeclarations) {
            final String symbolicName = paramDecl.getSymbolicName();
            sb.append(" <");
            sb.append(symbolicName);
            sb.append(">");
        }
        return sb.toString();
    }

    public void globalHelp() {
        final String programName = declaration.getProgramName();
        final List<SubCommandDeclaration> subcommands = new ArrayList<SubCommandDeclaration>(declaration.getCommandDeclarations());
        //TODO: handle commands like help specially
        out.println("Usage:");
        final StringBuilder usage = new StringBuilder("   ");
        usage.append(programName);
        if (!subcommands.isEmpty()) {
            usage.append(" <subcommand>");
        }
        usage.append(" [options]");
        usage.append(" [args]");
        out.println(usage);
        out.println();
        if (!subcommands.isEmpty()) {
            out.println("Available subcommands:");
            Collections.sort(subcommands, new Comparator<SubCommandDeclaration>() {
                public int compare(SubCommandDeclaration o1, SubCommandDeclaration o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            final Map<String, String> optMap = new LinkedHashMap<String, String>();
            int max = 0;
            // gather option table, and find longest declaration
            for (SubCommandDeclaration subCommandDeclaration : subcommands) {
                final String cmdName = subCommandDeclaration.getName();
                if (cmdName.length() > max) {
                    max = cmdName.length();
                }
                final String desc = subCommandDeclaration.getDescription();
                optMap.put(cmdName, desc);
            }
            // print it
            max += 4; // two more spaces
            final String fmt = "%-" + max + "s";
            printTable(optMap, fmt);
        }
        final SubCommandDeclaration defaultSubCommand = declaration.getDefaultSubCommand();
        // show help for default command (if unnamed)
        if (defaultSubCommand == null) {
            showValidOptions(declaration.getOptionDeclarations(), true);
            out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
        } else if (defaultSubCommand.getName() == null) {
            showValidOptions(defaultSubCommand.getOptionDeclarations(), false);
            showValidOptions(defaultSubCommand.getOptionDeclarations(), true);
        } else {
            showValidOptions(declaration.getOptionDeclarations(), true);
            out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
        }
    }

}
