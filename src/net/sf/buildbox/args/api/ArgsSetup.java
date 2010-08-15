package net.sf.buildbox.args.api;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import net.sf.buildbox.args.model.CommandlineDeclaration;
import net.sf.buildbox.args.model.ParsedOption;
import net.sf.buildbox.args.model.SubCommandDeclaration;

public interface ArgsSetup {
    CommandlineDeclaration getDeclaration();

    Callable<Integer> createSubCommand(String cmdName, SubCommandDeclaration cmdDecl, LinkedList<String> cmdParams) throws ParseException;

    void injectOptions(Callable<Integer> subCommandInstance, List<ParsedOption> parsedOptions, String cmdName) throws ParseException;
}
