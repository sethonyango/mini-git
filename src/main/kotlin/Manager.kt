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

    /**
     * Shows the commit logs.
     */
    fun log() {
        val branch = getCurrentBranch()
        var commitId = getBranchHead(branch)
        while (commitId != null) {
            val commitDir = File(COMMITS_DIR, commitId)
            println("Commit: $commitId")
            println(commitDir.resolve("metadata.txt").readText())
            println("----")
            commitId = commitDir.resolve("parent").takeIf { it.exists() }?.readText()
        }
    }

    /**
     * Generate a hashed commit ID
     */
    private fun generateCommitId(): String {
        val bytes = Instant.now().toString().toByteArray()
        return MessageDigest.getInstance("SHA-1").digest(bytes).joinToString("") { "%02x".format(it) }
    }

    /**
     * Determine if a file has been ignored
     */
    private fun isIgnored(file: File): Boolean {
        val ignoreFile = File(IGNORE_FILE)
        return ignoreFile.exists() && ignoreFile.readLines().any { file.name.matches(it.toRegex()) }
    }

    /**
     * Delete all staged files
     */
    private fun clearStagingArea() {
        File(STAGING_AREA).listFiles()?.forEach { it.delete() }
    }

    /***/
    private fun getCurrentBranch(): String = File(HEAD).readText()

    /***/
    private fun updateBranch(branch: String, commitId: String) {
        val branchFile = File(BRANCHES_DIR, branch)
        branchFile.writeText(commitId)
    }

    /***/
    private fun getBranchHead(branch: String): String? = File(BRANCHES_DIR, branch).takeIf { it.exists() }?.readText()

    /***/
    fun createBranch(name: String) {
        File(BRANCHES_DIR, name).writeText("")
    }

    /***/
    fun listBranches() {
        val branches = File(BRANCHES_DIR).listFiles()?.map { it.name } ?: emptyList()
        println("Branches:")
        branches.forEach { println("  $it") }
    }

    /***/
    fun switchBranch(branch: String) {
        val branchFile = File(BRANCHES_DIR, branch)
        if (!branchFile.exists()) throw IllegalArgumentException("Branch $branch does not exist.")
        File(HEAD).writeText(branch)
        println("Switched to branch $branch.")
    }

    /***/
    fun status() {
        val branch = getCurrentBranch()
        println("On branch: $branch")
        val stagedFiles = File(STAGING_AREA).listFiles()?.map { it.name } ?: emptyList()
        if (stagedFiles.isEmpty()) {
            println("No files staged for commit.")
        } else {
            println("Staged files:")
            stagedFiles.forEach { println("  $it") }
        }
    }
}