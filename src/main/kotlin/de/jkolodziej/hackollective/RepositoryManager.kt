package de.jkolodziej.hackollective

import io.ktor.application.Application
import io.ktor.application.ApplicationStopping
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.lib.PersonIdent
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class RepositoryManager(val localPath: Path, private val remoteURI: String?) : AutoCloseable {
    // TODO authentication against git repository

    private val log = LoggerFactory.getLogger(RepositoryManager::class.java)
    private val git: Git? = {
        if (remoteURI == null) {
            null
        } else {
            if (!Files.exists(localPath)) {
                Files.createDirectory(localPath)
            }
            if (!Files.exists(localPath.resolve(".git"))) {
                Git.cloneRepository().setURI(remoteURI).setDirectory(localPath.toFile()).call()
                        ?: throw IllegalArgumentException("Cloning repository returned null")
            } else {
                Git.open(localPath.toFile()) ?: throw IllegalArgumentException("Opening repository returned null")
            }
        }
    }.invoke()

    val hackerPath: Path
        get() = localPath.resolve("hacker")

    val entityPath: Path
        get() = localPath.resolve("entities")

    fun update() {
        val pullResult = git?.pull()?.setFastForward(MergeCommand.FastForwardMode.FF)?.call()
        if (pullResult != null && pullResult.isSuccessful) {
            val commits = pullResult.mergeResult.mergedCommits.size
            val changes = pullResult.fetchResult.advertisedRefs.size
            if (commits > 0) {
                log.info("Pulled {} changes in {} commits from upstream git repository.", changes, commits)
            }
        }
    }

    fun commit(message: String) {
        update()
        val commitResult = git?.commit()?.setMessage(message)?.setAll(true)?.setAuthor(PersonIdent("hackollective backend", "youcantmailme"))?.call()
        val pushResult = git?.push()?.call()
        // TODO verify commit/push
        if (commitResult != null && pushResult != null) {
            log.info("Commited local changes to upstream")
        }
    }

    override fun close() {
        git?.close()
    }
}

private var manager: RepositoryManager? = null

val Application.repositoryManager: RepositoryManager
    get() {
        return manager ?: {
            val localPath = Paths.get(environment.config.propertyOrNull("git.local.path")?.getString() ?: ".")
            val remotePath = environment.config.propertyOrNull("git.remote.path")?.getString()
            val result = RepositoryManager(localPath, remotePath)
            manager = result
            environment.monitor.subscribe(ApplicationStopping) {
                result.close()
            }
            result
        }.invoke()
    }

fun Application.startRepository() { // TODO: consider removing this Module to simplify the application

    launch {
        while (true) {
            this@startRepository.repositoryManager.update()
            delay(10000)
        }
    }
}
