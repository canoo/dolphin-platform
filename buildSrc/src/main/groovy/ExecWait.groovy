import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * ExecWait task to wait for a certain log output of a command
 *
 * adopted from https://fbflex.wordpress.com/2013/03/14/gradle-madness-execwait-a-task-that-waits-for-commandline-calls-to-be-ready/
 **/
class ExecWait extends DefaultTask {
    String command
    String ready
    String directory

    @TaskAction
    def spawnProcess() {

        ProcessBuilder builder = new ProcessBuilder(command.split(' '))
        builder.redirectErrorStream(true)
        builder.directory(new File(directory))
        Process process = builder.start()

        InputStream stdout = process.getInputStream()
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout))

        def line
        while ((line = reader.readLine()) != null) {
            println line
            if (line.contains(ready)) {
                println "$command is ready"
                break;
            }
        }
    }
}