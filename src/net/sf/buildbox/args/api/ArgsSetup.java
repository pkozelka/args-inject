package net.sf.buildbox.args.api;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.ParsedOption;
import net.sf.buildbox.args.model.SubCommandDeclaration;

public interface ArgsSetup {
    CommandlineDeclaration getDeclaration();

    ArgsCommand createSubCommand(String cmdName, SubCommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException;

    void injectOptions(ArgsCommand subCommandInstance, List<ParsedOption> parsedOptions, String cmdName) throws ParseException;
}
