package net.sf.buildbox.args;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.api.ArgsSetup;
import net.sf.buildbox.args.api.ExecutableCommand;
import net.sf.buildbox.args.model.CliDeclaration;
import net.sf.buildbox.args.model.CommandDeclaration;
import net.sf.buildbox.args.model.OptionDeclaration;

/**
 * Supports one subcommand on the commandline.
 * The order of global options, subcommand options, subcommand itself and its parameters does not matter - all can be mixed.
 * Values of options must however directly follow the option name.
 *
 * @todo embedded docs support
 * @todo print synopsis support
 * @todo print full help support
 */
public class SingleCommandBuilder {
    private final ArgsSetup declarationSetup;

    private boolean diagnosticMode = Boolean.getBoolean(System.getProperty("args.diag"));

    public SingleCommandBuilder(ArgsSetup declarationSetup) {
        this.declarationSetup = declarationSetup;
    }

    public void setDiagnosticMode(boolean diagnosticMode) {
        this.diagnosticMode = diagnosticMode;
    }

    private void diag(String format, Object... objects) {
        if (diagnosticMode) {
            System.err.println("DIAGNOSTICS: " + String.format(format, objects));
        }
    }

    /**
     * Parses the commandline args.
     *
     * @param args the args array to parse
     * @return initialized command instance
     * @throws ParseException -
     */
    public ExecutableCommand buildCommand(String... args) throws ParseException {
        final CliDeclaration declaration = declarationSetup.getDeclaration();
        final LinkedList<String> argsList = new LinkedList<String>(Arrays.asList(args));
        diag("Parsing commandline args: %s", argsList);
        final List<ParsedOption> parsedOptions = new ArrayList<ParsedOption>();
        final LinkedList<String> cmdParams = new LinkedList<String>();
        while (!argsList.isEmpty()) {
            final String arg = argsList.removeFirst();
//            System.out.println("arg = " + arg);
            final int n = arg.indexOf('=');
            final String argName;
            if (n >= 0) {
                argName = arg.substring(0, n);
                //TODO: also prepend name as separate option argument
                // TODO: somehow, support property options like -Dkey=value
                final String argValue = arg.substring(n + 1);
                argsList.addFirst(argValue);
            } else {
                //TODO: support -x- meaning boolean option with value of false 
                argName = arg;
            }
            final OptionDeclaration optionDecl;
            if (arg.startsWith("--")) {
                optionDecl = declaration.lookupLongOption(argName);
                if (optionDecl == null) {
                    // little trick to allow stuff like --help or --version behave as command:
                    final CommandDeclaration cmdDecl = declaration.lookupCommand(arg, false);
                    if (cmdDecl == null) {
                        throw new ParseException("invalid option: " + arg, 0);
                    }
                    cmdParams.add(arg);
                    continue;
                }
            } else if (arg.startsWith("-")) {
                optionDecl = declaration.lookupShortOption(argName);
                if (optionDecl == null) {
                    throw new ParseException("invalid option: " + arg, 0);
                }
            } else {
                cmdParams.add(arg);
                continue;
            }
//            System.out.println("optionMethod = " + optionMethod);
            // pre-parse option - command may be still unknown
            final ParsedOption option = new ParsedOption(argName, optionDecl);
            option.parse(argsList);
            parsedOptions.add(option);
        }
        diag("  cmdParams:     %s", cmdParams);
        diag("  parsedOptions: %s", parsedOptions);
        // parse command
        String cmdName = cmdParams.getFirst();
        CommandDeclaration cmdDecl = declaration.lookupCommand(cmdName, false);
//        CommandDeclaration cmdDecl = commandsByName.get(cmdName);
        if (cmdDecl == null) {
            if (declaration.getDefaultCommand() == null) {
                throw new ParseException("unknown command: " + cmdName, 0);
            }
            cmdParams.addFirst(cmdName);
            cmdDecl = declaration.getDefaultCommand();
            cmdName = "<DEFAULT>";
        }
        final ExecutableCommand commandInstance = declarationSetup.createCommandInstance(cmdDecl, cmdParams);
        // fail if any token remains
        if (!cmdParams.isEmpty()) {
            throw new ParseException("unparsed tokens: " + cmdParams, 0);
        }
        declarationSetup.injectOptions(commandInstance, parsedOptions, cmdName);
        return commandInstance;
    }

    public static ExecutableCommand buildCommand(ArgsSetup declarationSetup, String... args) throws ParseException {
        return new SingleCommandBuilder(declarationSetup).buildCommand(args);
    }
}
