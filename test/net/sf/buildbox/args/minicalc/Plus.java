package net.sf.buildbox.args.minicalc;

import net.sf.buildbox.args.annotation.SubCommand;
import java.util.concurrent.Callable;

@SubCommand(name = "plus", description = "computes sum of all given numbers")
public class Plus implements Callable<Integer> {
    private final int[] numbers;

    public Plus(int... numbers) {
        this.numbers = numbers;
    }

    public Integer call() throws Exception {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        System.out.println(sum);
        return 0;
    }
}
