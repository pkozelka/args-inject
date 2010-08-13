package net.sf.buildbox.args.api;

import java.util.concurrent.Callable;
import net.sf.buildbox.args.model.CommandlineDeclaration;

public interface MetaCommand extends Callable<Integer> {
    void setDeclaration(CommandlineDeclaration declaration);
}
