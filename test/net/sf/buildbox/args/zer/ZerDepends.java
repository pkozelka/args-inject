package net.sf.buildbox.args.zer;

import java.util.Arrays;
import java.util.List;
import net.sf.buildbox.args.annotation.SubCommand;

@SubCommand(name = "depends")
public class ZerDepends extends ZerXmlfulCommand {
    private final List<String> what;

    public ZerDepends(String... what) {
        this.what = Arrays.asList(what);
    }

    public Integer call() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        return 0;
    }
}
