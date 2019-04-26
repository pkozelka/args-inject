package structopt;

import java.util.Arrays;
import java.util.Map;

public class MinicalcStructopt {

    interface GlobalParams {
        Map<String,String> env();
        @Structopt(defaultValue = "%d", shortOpt = 'f')
        String format();
    }

    interface PlusParams {
        GlobalParams p();
        int[] operands();
    }

    interface MinusParams {
        GlobalParams p();
        int op1();
        int op2();
    }

    interface LogParams {
        GlobalParams p();
        @Structopt(defaultValue = "10", shortOpt = 'b', longOpt = "base")
        int base();
        int number();
    }

    //TODO this will be class, with some special support
    static interface StandardCommandsImplDraft {
        void help(String[] args);
        void version();
        void config();
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

        void supercompute(GlobalParams cli, SuperComputeParams scp) {
            // executes when no subcommand of supercompute command is specified
        }
    }

    interface SuperComputeParams {
        GlobalParams p();
        int threads();
    }

    static class SuperComputeImpl {
        void alpha(AlphaParams ap) {
            ap.p().threads();
        }

        void beta(BetaParams bp) {
            bp.p().threads();
        }

        // other command specified
        void _other(SuperComputeParams p, String... args) {}
    }

    interface AlphaParams {
        SuperComputeParams p();
        int alphaOperand1();
        int alphaOperand2();
    }

    interface BetaParams {
        SuperComputeParams p();
        int betaOperand1();
        int betaOperand2();
        int betaOperand3();
    }

    // minicalc.sh -f '%f' supercompute 5 alpha 12 5

    public static void main(String[] args) throws Exception {
        StructoptParser.execute(args, MinicalcCliImpl.class,StandardCommandsImplDraft.class);
    }
}
