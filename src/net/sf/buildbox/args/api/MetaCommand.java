package net.sf.buildbox.args.api;

import net.sf.buildbox.args.model.CliDeclaration;

public interface MetaCommand extends ExecutableCommand {
    void setDeclaration(CliDeclaration declaration);

    void call() throws Exception;
}
