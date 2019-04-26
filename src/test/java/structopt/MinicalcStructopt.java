package structopt;

import java.util.Arrays;

public class MinicalcStructopt {

    interface GlobalParams {
        @Structopt(defaultValue = "%d", shortOpt = 'f')
        String format();
    }

    interface PlusParams {
        int[] operands();
    }

    interface MinusParams {
        int op1();
        int op2();
    }

    interface LogParams {
        @Structopt(defaultValue = "10", shortOpt = 'b', longOpt = "base")
        int base();
        int number();
    }

    //TODO this will be class, with some special support
    static interface StandardCommandsImplDraft {
        void help(String[] args);
        void version();
        void completions(String shell);
        void invalidCommand(String command);

        void paramlessExecution();
    }

    static class MinicalcCliImpl {
        void plus(GlobalParams cli, PlusParams pp) {
            final int result = Arrays.stream(pp.operands()).sum();
            System.out.printf(cli.format(), result);
        }

        void minus(GlobalParams cli, MinusParams mp) {
            final int result = mp.op1() - mp.op2();
            System.out.printf(cli.format(), result);
        }

        void log(GlobalParams cli, LogParams p) {
            final double result = Math.log(p.number() / Math.log(p.base()));
            System.out.printf(cli.format(), result);
        }
    }

    public static void main(String[] args) throws Exception {
        StructoptParser.execute(args, MinicalcCliImpl.class,StandardCommandsImplDraft.class);
    }
}
