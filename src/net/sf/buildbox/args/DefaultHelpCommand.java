package net.sf.buildbox.args;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.api.MetaCommand;
import net.sf.buildbox.args.model.CommandlineDeclaration;
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
@SubCommand(name = "help", aliases = {"--help", "?", "h"})
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

    public void call() throws Exception {
        if (command == null) {
            globalHelp();
        } else {
            final SubCommandDeclaration cmdDecl = declaration.lookupCommand(command, false);
            if (cmdDecl == null) {
                throw new IllegalArgumentException(command + ": unknown command");
            }
            commandHelp(cmdDecl);
        }
    }

    private void commandHelp(SubCommandDeclaration cmdDecl) {
        final String programName = declaration.getProgramName();
        final String shortDesc = "///todo: shortDesc///";
        out.println(cmdDecl + ": " + shortDesc);
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
        out.println();
        out.println("///todo: longDesc///");
    }

    public void globalHelp() {
        final String programName = declaration.getProgramName();
        out.println("Usage:");
        out.println("   " + programName + " <subcommand> [options] [args]");
        out.println();
        out.println("Type '" + programName + " help <subcommand>' for help on a specific subcommand");
        out.println();
        out.println("Available subcommands:");
        final List<SubCommandDeclaration> sortedList = new ArrayList<SubCommandDeclaration>(declaration.getCommandDeclarations());
        Collections.sort(sortedList, new Comparator<SubCommandDeclaration>() {
            public int compare(SubCommandDeclaration o1, SubCommandDeclaration o2) {
                return o1.toString().compareToIgnoreCase(o2.toString());
            }
        });
        for (SubCommandDeclaration cmd : sortedList) {
            out.println("   " + cmd);
        }
    }

}
