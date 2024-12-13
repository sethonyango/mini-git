import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import kotlin.system.exitProcess

@Command(
    name = "mini-git",
    mixinStandardHelpOptions = true,
    version = ["mini-git 1.0"],
    description = ["A simple distributed source control system."]
)
class MiniGitCLI : Runnable {
    override fun run() {
        println("Welcome to mini-git! Use --help for available commands.")
    }

    @Command(name = "init", description = ["Initialize a repository in the current directory."])
    fun initRepo() {
        Manager.init(".")
    }

    @Command(name = "add", description = ["Stage files for commit."])
    fun addFiles(@Parameters(paramLabel = "<files>", description = ["Files to stage."]) files: List<String>) {
        files.forEach { Manager.add(it) }
    }

    @Command(name = "commit", description = ["Commit staged files."])
    fun commit(
        @Parameters(paramLabel = "<message>", description = ["Commit message."]) message: String,
        @CommandLine.Option(
            names = ["-a", "--author"],
            required = true,
            description = ["Author of the commit."]
        ) author: String
    ) {
        Manager.commit(message, author)
    }

    @Command(name = "log", description = ["View commit history."])
    fun viewLog() {
        Manager.log()
    }

    @Command(name = "branch", description = ["Manage branches."])
    fun manageBranches(
        @CommandLine.Option(names = ["-c", "--create"], description = ["Create a new branch."]) create: String?,
        @CommandLine.Option(names = ["-l", "--list"], description = ["List all branches."]) list: Boolean = false,
        @CommandLine.Option(names = ["-s", "--switch"], description = ["Switch to a branch."]) switch: String?
    ) {
        when {
            create != null -> Manager.createBranch(create)
            list -> Manager.listBranches()
            switch != null -> Manager.switchBranch(switch)
            else -> println("No action specified. Use --help for branch options.")
        }
    }

    @Command(name = "status", description = ["Show the repository status."])
    fun status() {
        Manager.status()
    }
}

fun main(args: Array<String>) {
    val commandLine = CommandLine(MiniGitCLI())
    val exitCode = commandLine.execute(*args)
    exitProcess(exitCode)
}