package net.sf.buildbox.args;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.*;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;
import net.sf.buildbox.args.model.ParamDeclaration;
import net.sf.buildbox.args.model.SubCommandDeclaration;

/**
 * Usecases:
 * main help - list of commands:
 * javaapp help
 * javaapp BADCMD
 * detailed help - synopsis of one command
 * javaapp help SOMECMD
 * javaapp SOMECMD --help
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
        out.println("Usage:");
        final StringBuilder usage = new StringBuilder("   ");
        usage.append(programName);
        usage.append(" ");
        usage.append(cmdDecl.getName());
        usage.append(" [options]");
        paramSynopsis(usage, cmdDecl.getParamDeclarations());
        out.println(usage);

        showValidOptions(cmdDecl);
    }

    private void showValidOptions(SubCommandDeclaration cmdDecl) {
        final List<OptionDeclaration> sortedOptions = new ArrayList<OptionDeclaration>(cmdDecl.getOptionDeclarations());
        //TODO: somehow add global options
        if (!sortedOptions.isEmpty()) {
            out.println();
            out.println("Valid options:");
            Collections.sort(sortedOptions, new Comparator<OptionDeclaration>() {
                public int compare(OptionDeclaration o1, OptionDeclaration o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            final Map<String, String> optMap = new LinkedHashMap<String, String>();
            int max = 0;
            // gather option table, and find longest declaration
            for (OptionDeclaration optionDeclaration : sortedOptions) {
                final String strDecl = optionDeclaration.toString();
                // TODO: add value synopsis to strDecl
                if (strDecl.length() > max) {
                    max = strDecl.length();
                }
                final String desc = optionDeclaration.getDescription();
                optMap.put(strDecl, desc);
            }
            // print it
            max += 2; // two more spaces
            final String fmt = "%-" + max + "s";
            printTable(optMap, fmt);
        }

        out.println();
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
        out.println();
    }

    private static void paramSynopsis(StringBuilder sb, List<ParamDeclaration> paramDeclarations) {
        for (ParamDeclaration paramDecl : paramDeclarations) {
            final String symbolicName = paramDecl.getSymbolicName();
            sb.append(" <");
            sb.append(symbolicName);
            sb.append(">");
        }
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
            out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
        } else if (defaultSubCommand.getName() == null) {
            showValidOptions(defaultSubCommand);
        } else {
            out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
        }
    }

}
