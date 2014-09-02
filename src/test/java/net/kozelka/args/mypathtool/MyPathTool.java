package net.kozelka.args.mypathtool;

import java.io.File;
import java.io.IOException;
import java.util.*;
import net.kozelka.args.BasicArgsParser;
import net.kozelka.args.DefaultHelpCommand;
import net.kozelka.args.annotation.AnnottationAwareSetup;
import net.kozelka.args.annotation.Option;
import net.kozelka.args.annotation.Param;
import net.kozelka.args.api.ArgsCommand;

/**
 * Commandline archetype: no subcommands, just single param and some options
 * This sample code demonstrates working with array input arguments.
 */
public class MyPathTool implements ArgsCommand {
    private final File[] pathList;
    private boolean dirPerLine;
    private boolean keepDuplicatePaths;
    private boolean keepMissingPaths;
    private Set<String> exclude = Collections.emptySet();

    public MyPathTool(@Param("path") File[] pathList) {
        this.pathList = pathList;
    }

    @Option(shortName = "-l", longName = "--multi-line-output", description = "list paths one per line")
    public void dirPerLine() {
        dirPerLine = true;
    }

    @Option(shortName = "-kd", longName = "--keep-duplicate-paths", description = "do not remove duplicates")
    public void setKeepDuplicatePaths(boolean keepDuplicatePaths) {
        this.keepDuplicatePaths = keepDuplicatePaths;
    }

    @Option(shortName = "-km", longName = "--keep-missing-paths", description = "do not remove missing paths")
    public void setKeepMissingPaths(boolean keepMissingPaths) {
        this.keepMissingPaths = keepMissingPaths;
    }

    @Option(longName = "--exclude", description="excludes directories containing given subpaths (comma separated)")
    public void setExclude(String... exclude) {
        this.exclude = new HashSet<String>(Arrays.asList(exclude));
    }

    public Integer call() throws Exception {
        final Collection<File> result = keepDuplicatePaths ? new ArrayList<File>() :  new LinkedHashSet<File>();
        for (File path : pathList) {
            try {
                if (! keepMissingPaths) {
                    if (!path.exists()) {
                        System.err.println("missing path: " + path);
                        continue;
                    }
                }
                if (containsExcludedSubpath(path)) {
                    System.err.println("excluding: " + path);
                    continue;
                }
                final File d = path.getCanonicalFile();
                if (!result.add(d)) {
                    System.err.println("duplicate path: " + d);
                }
            } catch (IOException e) {
                System.err.println("invalid path: " + path);
            }
        }
        printPathList(result);
        return 0;
    }

    private boolean containsExcludedSubpath(File path) {
        for (String ex : exclude) {
            final File p = new File(path, ex);
            if (p.exists()) return true;
        }
        return false;
    }

    private void printPathList(Collection<File> paths) {
        if (dirPerLine) {
            for (File path : paths) {
                System.out.println(path);
            }
        } else {
            final StringBuilder sb = new StringBuilder();
            for (File path : paths) {
                if (sb.length() != 0) {
                    sb.append(File.pathSeparatorChar);
                }
                sb.append(path);
            }
            System.out.println(sb);
        }
    }

    /**
     * Highest-possible level of invocation usable both from {@link #main} and from unit tests.
     *
     * @param args commandline arguments
     * @return true if successful
     * @throws Exception -
     */
    static int run(String... args) throws Exception {
        final AnnottationAwareSetup setup = new AnnottationAwareSetup("mpt");
        setup.setDefaultSubCommand(MyPathTool.class);
        setup.addSubCommand(DefaultHelpCommand.class);
        return BasicArgsParser.process(setup, args);
    }

    public static void main(String[] args) throws Exception {
        final int exitCode = run(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }
}