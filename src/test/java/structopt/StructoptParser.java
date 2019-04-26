package structopt;

import java.util.Optional;

public class StructoptParser<C> {
    private final Class<C> definition;

    public StructoptParser(Class<C> definition) {
        this.definition = definition;
    }

    public C parse(String... args) throws Exception {
        throw new UnsupportedOperationException();
    }

    public Object subcommandParams(Enum subcommandConstant) {
        throw new UnsupportedOperationException();
    }
}
