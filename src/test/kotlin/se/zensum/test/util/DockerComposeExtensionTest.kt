package se.zensum.test.util

import com.palantir.docker.compose.DockerComposeRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

@DockerCompose(services = ["redis"])
class DockerComposeExtensionTest {

    @Test
    fun test(dockerCompose: DockerComposeRule) {
        assertThat(dockerCompose.containers().allContainers().size, equalTo(1))
    }
}