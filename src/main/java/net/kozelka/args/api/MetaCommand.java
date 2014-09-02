package net.kozelka.args.api;

import net.kozelka.args.model.CommandlineDeclaration;

public interface MetaCommand extends ArgsCommand {
    void setDeclaration(CommandlineDeclaration declaration);
}
