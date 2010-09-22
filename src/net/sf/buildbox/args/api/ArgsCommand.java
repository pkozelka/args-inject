package net.sf.buildbox.args.api;

import java.util.concurrent.Callable;

/**
 * Base interface that must be implemented by subcommands. 
 */
public interface ArgsCommand extends Callable<Integer> {
}
