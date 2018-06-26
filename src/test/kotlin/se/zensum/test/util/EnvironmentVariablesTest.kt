package se.zensum.test.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class EnvironmentVariablesTest {

    @Test
    @EnvironmentVariable("Testing", "Testing1234")
    fun createEnvironmentVariable() {
        assertThat(System.getenv("Testing"), equalTo("Testing1234"))
    }

    @Test
    @EnvironmentVariables(
            EnvironmentVariable("Testing", "Testing1234"),
            EnvironmentVariable("Testing2", "Taco")
    )
    fun createMultipleVariables() {
        assertThat(System.getenv("Testing"), equalTo("Testing1234"))
        assertThat(System.getenv("Testing2"), equalTo("Taco"))
    }

}