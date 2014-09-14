package drafting;

/**
 * @author Petr Kozelka
 */
public class SampleMain {

    public static void main(String[] args) {
        final CliParser cliParser = new CliParser();
        final NgSubCommand cmd = new NgSubCommand();
        final NgStringOption rev = new NgStringOption("--rev");
        cmd.addOption(rev, NgCardinality.OPTIONAL);
        final NgStringOption username = new NgStringOption("--username");
        cmd.addOption(username, NgCardinality.REQUIRED);
        cliParser.addCommand(cmd);
    }

    static class CliParser {
        public void addCommand(NgSubCommand cmd) {
            //
        }

    }
    public static enum NgCardinality {
        OPTIONAL,
        REQUIRED,
        MULTI


    }

    public static class NgSubCommand {
        public void addOption(NgStringOption rev, NgCardinality required) {
            //
        }
    }

    public static class NgOption {
        private String longName;

        private NgOption(String longName) {
            this.longName = longName;
        }
    }

    public static class NgStringOption extends NgOption {

        private NgStringOption(String longName) {
            super(longName);
        }
    }
}
