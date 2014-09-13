# New style


Current style of describing commandline structure in java is not satisfyingly convenient.

Let's come up with a better one.


* each option or argument is represented with an instance of class handling given type
* that instance contains everything except cardinality, as it can be different for subcommands
* so, the cardinality is specified when filling subcommand definition


Example:

```
    ...
    final FileOption bar = new FileOption("--bar")
    bar.setPresent(FileOption.MUST_EXIST);
    final SubCommand foo = new SubCommand("foo");
    foo.addOption(bar,Option.Cardinality.REQUIRED)
    ...
```
