package net.sf.buildbox.args.minicalc;

import net.sf.buildbox.args.annotation.SubCommand;
import net.sf.buildbox.args.annotation.Param;
import java.util.concurrent.Callable;

@SubCommand(name = "minus", description = "computes operand1 - operand2")
public class Minus implements Callable<Integer> {
    private final int x;
    private final int y;

    public Minus(@Param("operand1") int x, @Param("operand2") int y) {
        this.x = x;
        this.y = y;
    }

    public Integer call() throws Exception {
        System.out.println(x - y);
        return 0;
    }
}
