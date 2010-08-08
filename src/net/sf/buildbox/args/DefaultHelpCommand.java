package net.sf.buildbox.args;

import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
 *
 * @todo implement option onelinedesc, param symbolic name
 */
@SubCommand(name = "help", aliases = {"--help", "?", "h"}, description = "shows help for the whole app. or for specified command")
public class DefaultHelpCommand implements MetaCommand {
    private static final String SP = "            ";
    private static final String SPO = "                              ";
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
                throw new ParseException(command + ": unknown command", 0);
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
        //TODO: better args synopsis
        for (ParamDeclaration paramDecl : cmdDecl.getParamDeclarations()) {
            usage.append(" ");
            final String symbolicName = paramDecl.getType().getSimpleName();
            usage.append(symbolicName);
        }
        out.println(usage);

        final List<OptionDeclaration> sortedOptions = new ArrayList<OptionDeclaration>(cmdDecl.getOptionDeclarations());
        if (!sortedOptions.isEmpty()) {
            out.println();
            out.println("Valid options:");
            Collections.sort(sortedOptions, new Comparator<OptionDeclaration>() {
                public int compare(OptionDeclaration o1, OptionDeclaration o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });
            for (OptionDeclaration optionDeclaration : sortedOptions) {
                final StringBuilder sb = new StringBuilder("   ");
                final String s = optionDeclaration.toString();
                final String desc = "todo: desc";
                sb.append(s);
                if (desc != null) {
                    if (s.length() < SPO.length()) {
                        sb.append(SPO.substring(s.length()));
                    }
                    sb.append(" ");
                    sb.append(desc);
                }
                out.println(sb);
            }
        }

        out.println();
        out.println("///todo: longDesc///");
    }

    public void globalHelp() {
        final String programName = declaration.getProgramName();
        out.println("Usage:");
        out.println("   " + programName + " <subcommand> [options] [args]");
        out.println();
        out.println("Available subcommands:");
        final List<SubCommandDeclaration> sortedList = new ArrayList<SubCommandDeclaration>(declaration.getCommandDeclarations());
        Collections.sort(sortedList, new Comparator<SubCommandDeclaration>() {
            public int compare(SubCommandDeclaration o1, SubCommandDeclaration o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        for (SubCommandDeclaration cmd : sortedList) {
            final StringBuilder sb = new StringBuilder("   ");
            final String cmdName = cmd.getName();
            sb.append(cmdName);
            final String description = cmd.getDescription();
            if (description != null && !"".equals(description)) {
                if (cmdName.length() < SP.length()) {
                    sb.append(SP.substring(cmdName.length()));
                }
                sb.append(" ");
                sb.append(description);
            }
            out.println(sb);
        }
        out.println();
        out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
    }

}
