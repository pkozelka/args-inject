package net.kozelka.args.api;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.kozelka.args.model.CommandlineDeclaration;
import net.kozelka.args.model.ParsedOption;
import net.kozelka.args.model.SubCommandDeclaration;

public interface ArgsSetup {
    CommandlineDeclaration getDeclaration();

    ArgsCommand createSubCommand(String cmdName, SubCommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException;

    void injectOptions(ArgsCommand subCommandInstance, List<ParsedOption> parsedOptions, String cmdName) throws ParseException;
}
