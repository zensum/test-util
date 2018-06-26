package se.zensum.test.util

import com.palantir.docker.compose.DockerComposeRule
import com.palantir.docker.compose.connection.waiting.HealthChecks.toHaveAllPortsOpen
import org.junit.jupiter.api.extension.*

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(DockerComposeExtension::class)
annotation class DockerCompose(val file: String = "src/test/resources/docker-compose.yml", val logPath: String = "target/test-docker-logs", vararg val services: String)

class DockerComposeExtension : BeforeAllCallback, AfterAllCallback, ParameterResolver {

    private lateinit var docker: DockerComposeRule

    override fun beforeAll(context: ExtensionContext) {
        val annotation = context.testClass.get().getAnnotation(DockerCompose::class.java)
        val builder: DockerComposeRule.Builder = DockerComposeRule.builder()
            .file(annotation.file)
            .saveLogsTo(annotation.logPath)

        annotation.services.forEach {
            builder.waitingForService(it, toHaveAllPortsOpen())
        }
        docker = builder.build()
        docker.before()
    }

    override fun afterAll(extensionContext: ExtensionContext) {
        docker.after()
    }

    @Throws(ParameterResolutionException::class)
    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == DockerComposeRule::class.java
    }

    @Throws(ParameterResolutionException::class)
    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return docker
    }

}