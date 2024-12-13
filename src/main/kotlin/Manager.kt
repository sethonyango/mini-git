import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
import java.time.Instant

/**
 *
 */
object Manager {
    private const val REPO_DIR = ".mini"
    private const val STAGING_AREA = "${REPO_DIR}/staging"
    private const val COMMITS_DIR = "${REPO_DIR}/commits"
    private const val BRANCHES_DIR = "$REPO_DIR/branches"
    private const val HEAD = "$REPO_DIR/HEAD"
    private const val IGNORE_FILE = ".miniignore"

    /**
     * Initialize the repository.
     * This will create the .mini, .mini/staging, .mini/commits, .mini/branches folder
     * Similar to `git init`
     * @param directory the project directory
     */
    fun init(directory: String) {
        val repoDir = File(directory, REPO_DIR)
        if (repoDir.exists()) throw IllegalStateException("Repository already initialized in ${repoDir.absolutePath}")

        listOf(repoDir, File(STAGING_AREA), File(COMMITS_DIR), File(BRANCHES_DIR)).forEach { it.mkdirs() }
        File(HEAD).writeText("main")
        createBranch("main")
        println("Initialized empty repository in ${repoDir.absolutePath}")
    }

    /**
     * Add file changes to staging area.
     * Similar to git add
     *
     * @param filePath the file to add to staging
     */
    fun add(filePath: String) {
        val file = File(filePath)
        if (!file.exists()) throw IllegalArgumentException("File does not exist: $filePath")
        if (isIgnored(file)) return
        val destination = File(STAGING_AREA, file.name)
        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
        println("Added ${file.name} to staging area.")
    }

    /**
     * Create a new commit containing the current contents of the index and the given log message describing the changes.
     * The new commit is a direct child of HEAD, usually the tip of the current branch,
     * and the branch is updated to point to it (unless no branch is associated with the working tree
     *
     * @param message Commit message
     * @param author The author of the commit
     */
    fun commit(message: String, author: String) {
        val commitId = generateCommitId()
        val branch = getCurrentBranch()
        val commitDir = File(COMMITS_DIR, commitId)
        commitDir.mkdir()
        File(STAGING_AREA).listFiles()?.forEach { file ->
            Files.copy(file.toPath(), File(commitDir, file.name).toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        File(commitDir, "metadata.txt").writeText("Author: $author\nMessage: $message\nTimestamp: ${Instant.now()}\n")
        updateBranch(branch, commitId)
        println("Committed as $commitId.")
        clearStagingArea()
    }

