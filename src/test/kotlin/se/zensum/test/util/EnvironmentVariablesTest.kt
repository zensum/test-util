package se.zensum.test.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

@EnvironmentVariable("ClassTest", "BestClass")
class EnvironmentVariablesTest {
    @Test
    fun createClassEnironmentVariable() {
        assertThat(System.getenv("ClassTest"), equalTo("BestClass"))
    }

    @Test
    @EnvironmentVariable("Testing", "Testing1234")
    fun createEnvironmentVariable() {
        assertThat(System.getenv("Testing"), equalTo("Testing1234"))
    }

    @Test
    @EnvironmentVariables(
            EnvironmentVariable("MTesting", "Testing1234"),
            EnvironmentVariable("MTesting2", "Taco")
    )
    fun createMultipleVariables() {
        assertThat(System.getenv("MTesting"), equalTo("Testing1234"))
        assertThat(System.getenv("MTesting2"), equalTo("Taco"))
    }

}