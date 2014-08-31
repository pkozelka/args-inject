package net.kozelka.args;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import net.kozelka.args.api.ArgsSetup;
import net.kozelka.args.api.ArgsCommand;
import net.kozelka.args.model.CommandlineDeclaration;
import net.kozelka.args.model.OptionDeclaration;
import net.kozelka.args.model.ParsedOption;
import net.kozelka.args.model.SubCommandDeclaration;

/**
 * Supports one subcommand on the commandline.
 * The order of global options, subcommand options, subcommand itself and its parameters does not matter - all can be mixed.
 * Values of options must however directly follow the option name.
 */
public class BasicArgsParser {
    private final ArgsSetup declarationSetup;

    public BasicArgsParser(ArgsSetup declarationSetup) {
        this.declarationSetup = declarationSetup;
    }

    /**
     * Parses the commandline args.
     *
     * @param args the args array to parse
     * @return initialized command instance
     * @throws ParseException -
     */
    public Callable<Integer> parse(String... args) throws ParseException {
        final CommandlineDeclaration declaration = declarationSetup.getDeclaration();
        final LinkedList<String> argsList = new LinkedList<String>(Arrays.asList(args));
        ArgsUtils.debug("Parsing commandline args: %s", argsList);
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
            if (arg.equals("-") || arg.equals("--")) {
                // allow stdfile value (-) and multi-varargs separator (--)
                cmdParams.add(arg);
                continue;
            } else if (arg.startsWith("--")) {
                optionDecl = declaration.lookupLongOption(argName);
                if (optionDecl == null) {
                    // little trick to allow stuff like --help or --version behave as command:
                    final SubCommandDeclaration cmdDecl = declaration.lookupCommand(arg, false);
                    if (cmdDecl == null) {
                        throw new ParseException("invalid option: " + arg, 0);
                    }
                    cmdParams.add(arg);
                    continue;
                }
            } else if (arg.startsWith("-")) {
                optionDecl = declaration.lookupShortOption(argName);
                if (optionDecl == null) {
                    final Pattern NUMBER_CHARS = Pattern.compile("-\\d+(\\.\\d+)?");
                    if (!NUMBER_CHARS.matcher(argName).matches()) {
                        throw new ParseException("invalid option: " + arg, 0);
                    }
                    // if digits (with dot) follow, pass it as a value => this enables support for negative subcommand param values
                    cmdParams.add(arg);
                    continue;
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
        ArgsUtils.debug("  cmdParams:     %s", cmdParams);
        ArgsUtils.debug("  parsedOptions: %s", parsedOptions);
        // parse command
        final SubCommandDeclaration defaultSubCommand = declaration.getDefaultSubCommand();
        SubCommandDeclaration cmdDecl;
        String cmdName;
        if (cmdParams.isEmpty()) {
            if (defaultSubCommand == null) {
                throw new ParseException("no subcommand specified", 0);
            }
            cmdDecl = defaultSubCommand;
            cmdName = cmdname(cmdDecl);
        } else {
            cmdName = cmdParams.removeFirst();
            cmdDecl = declaration.lookupCommand(cmdName, false);
            if (cmdDecl == null) {
                if (defaultSubCommand == null) {
                    throw new ParseException("unknown subcommand: " + cmdName, 0);
                }
                cmdParams.addFirst(cmdName); // return it back, it's value
                cmdDecl = defaultSubCommand;
                cmdName = cmdname(cmdDecl);
            }
        }
        final ArgsCommand commandInstance = declarationSetup.createSubCommand(cmdName, cmdDecl, cmdParams);
        // fail if any token remains
        if (!cmdParams.isEmpty()) {
            throw new ParseException("unparsed tokens: " + cmdParams, 0);
        }
        declarationSetup.injectOptions(commandInstance, parsedOptions, cmdName);
        return commandInstance;
    }

    private static String cmdname(SubCommandDeclaration cmdDecl) {
        String name = cmdDecl.getName();
        return name == null ? "<UNNAMED>" : name;
    }

    public static Callable<Integer> parse(ArgsSetup declarationSetup, String... args) throws ParseException {
        return new BasicArgsParser(declarationSetup).parse(args);
    }

    /**
     * <p>High-level entry point for commandline processing. Builds command according to setup and args, calls it,
     * and handles any exceptions in a way expectable from commandline application.</p>
     * <p>Expected usage:</p>
     * <pre>
     * ...
     * public static void main(String[] args) {
     *   // ... create your commandline setup here ...
     *   if (!<b> SingleCommandBuilder.main</b>(setup, args)) {
     *     {@link System#exit(int) System.exit(1)}; // indicate failure to shell
     *   }
     * }
     * ...
     * </pre>
     * <p>Note that this way, the dirty job of calling {@link System#exit(int)} is intentionally left up to the caller.</p>
     *
     * @param setup commandline declaration
     * @param args  actual arguments to be processed
     * @return exit code. It is a good idea to indicate failure to shell by terminating with {@link System#exit(int) System.exit(1)}
     * @throws Exception used only if {@link net.kozelka.args.ArgsUtils#debugMode}==true
     */
    public static int process(ArgsSetup setup, String... args) throws Exception {
        try {
            XmlModelSerializer.checkArgsXml(setup.getDeclaration());
            final int exitCode = new BasicArgsParser(setup).parse(args).call();
            ArgsUtils.debug("exitCode = %d", exitCode);
            return exitCode;
        } catch (ParseException e) {
            System.err.println("ERROR: " + e.getMessage());
            if (ArgsUtils.debugMode) {
                e.printStackTrace();
            }
            return 1;
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            if (ArgsUtils.debugMode) {
                e.printStackTrace();
            }
            return 1;
        }
    }

}
