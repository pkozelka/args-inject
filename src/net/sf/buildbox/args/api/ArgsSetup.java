package net.sf.buildbox.args.api;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.ParsedOption;
import net.sf.buildbox.args.model.CliDeclaration;
import net.sf.buildbox.args.model.SubcommandDeclaration;

public interface ArgsSetup {
    CliDeclaration getDeclaration();

    ExecutableCommand createSubcommand(SubcommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException;

    void injectOptions(ExecutableCommand subcommandInstance, List<ParsedOption> parsedOptions, String cmdName) throws ParseException;
}
