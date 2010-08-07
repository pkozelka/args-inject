package net.sf.buildbox.args.api;

import net.sf.buildbox.args.model.CommandlineDeclaration;

public interface MetaCommand extends ExecutableCommand {
    void setDeclaration(CommandlineDeclaration declaration);

    void call() throws Exception;
}
