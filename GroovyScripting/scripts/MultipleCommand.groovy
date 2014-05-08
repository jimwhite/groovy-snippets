import com.beust.jcommander.*

import groovy.transform.BaseScript
import groovy.transform.Field

@BaseScript groovyx.cli.JCommanderScript thisScript
//@BaseScript groovyx.cli.JCommanderScript thisScript

@Parameter(names = ["-log", "-verbose" ], description = "Level of verbosity")
@Field Integer verbose = 1;

@Parameters(commandNames = ["commit"], commandDescription = "Record changes to the repository")
class CommandCommit implements Runnable {
    @Parameter(description = "The list of files to commit")
    private List<String> files;

    @Parameter(names = "--amend", description = "Amend")
    private Boolean amend = false;

    @Parameter(names = "--author")
    private String author;

    @Override
    void run() {
        println "$author committed $files ${amend ? "using" : "not using"} amend."
    }
}

@Parameters(separators = "=", commandNames = ["add", "addFiles"],  commandDescription = "Add file contents to the index")
public class CommandAdd {
    @Parameter(description = "File patterns to add to the index")
    private List<String> patterns;

    @Parameter(names = "-i")
    private Boolean interactive = false;
}

JCommander createScriptJCommander() {
    JCommander jc = super.createScriptJCommander()

    addCommand = new CommandAdd()
    jc.addCommand(addCommand)

    commitCommand = new CommandCommit()
    jc.addCommand commitCommand

    jc
}


println verbose
println scriptJCommander.parsedCommand

switch (scriptJCommander.parsedCommand) {
    case "add" :
        if (addCommand.interactive) {
            println "Adding ${addCommand.patterns} interactively."
        } else {
            println "Adding ${addCommand.patterns} in batch mode."
        }
}
