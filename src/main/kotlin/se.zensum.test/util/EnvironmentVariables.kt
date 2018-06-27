package se.zensum.test.util

import org.junit.jupiter.api.extension.*
import java.util.*
import kotlin.collections.ArrayList


@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@ExtendWith(EnvironmentVariablesExtension::class)
annotation class EnvironmentVariable(val key: String, val value: String)

// Remove when https://youtrack.jetbrains.com/issue/KT-12794 is solved and use @Repeated annotation
@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@ExtendWith(EnvironmentVariablesExtension::class)
annotation class EnvironmentVariables(vararg val environmentVariables: EnvironmentVariable)

class EnvironmentVariablesExtension : AfterEachCallback, BeforeEachCallback, BeforeAllCallback, AfterAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        val annotations = context.testClass.get().annotations
        val environmentAnnotations: MutableList<EnvironmentVariable> = filterAnnotations(annotations)
        setEnv(environmentAnnotations.map { Pair(it.key, it.value) }.toMap())
    }

    override fun afterAll(context: ExtensionContext) {
        val annotations = context.testClass.get().annotations
        val environmentAnnotations: MutableList<EnvironmentVariable> = filterAnnotations(annotations)
        clearEnv(environmentAnnotations.map { Pair(it.key, it.value) }.toMap())
    }

    override fun afterEach(context: ExtensionContext) {
        var annotations = context.testMethod.get().annotations
        annotations += context.testClass.get().annotations
        val environmentAnnotations: MutableList<EnvironmentVariable> = filterAnnotations(annotations)
        clearEnv(environmentAnnotations.map { Pair(it.key, it.value) }.toMap())
    }


    override fun beforeEach(context: ExtensionContext) {
        var annotations = context.testMethod.get().annotations
        annotations += context.testClass.get().annotations
        val environmentAnnotations: MutableList<EnvironmentVariable> = filterAnnotations(annotations)
        setEnv(environmentAnnotations.map { Pair(it.key, it.value) }.toMap())
    }

    private fun filterAnnotations(annotations: Array<Annotation>): MutableList<EnvironmentVariable> {
        val environmentAnnotations: MutableList<EnvironmentVariable> = ArrayList()
        annotations.forEach {
            if (it is EnvironmentVariable) {
                environmentAnnotations.add(it)
            } else if (it is EnvironmentVariables) {
                environmentAnnotations.addAll(it.environmentVariables)
            }
        }
        return environmentAnnotations
    }
}


@Throws(Exception::class)
private fun clearEnv(environmentVariables: Map<String, String>) {
    try {
        val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
        val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
        theEnvironmentField.isAccessible = true
        val envFields = theEnvironmentField.get(null) as MutableMap<String, String>

        val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
        theCaseInsensitiveEnvironmentField.isAccessible = true
        val caseInsensitiveEnvFields = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
        environmentVariables.forEach {
            envFields.remove(it.key)
            caseInsensitiveEnvFields.remove(it.key)
        }
    } catch (e: NoSuchFieldException) {
        val classes = Collections::class.java.declaredClasses
        val env = System.getenv()
        for (klass in classes) {
            if ("java.util.Collections\$UnmodifiableMap" == klass.name) {
                val field = klass.getDeclaredField("m")
                field.setAccessible(true)
                val obj = field.get(env)
                val map = obj as MutableMap<String, String>
                map.clear()
                environmentVariables.forEach { map.remove(it.key) }
            }
        }
    }
}

@Throws(Exception::class)
private fun setEnv(environmentVariables: Map<String, String>) {
    try {
        val processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment")
        val theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment")
        theEnvironmentField.isAccessible = true
        val envFields = theEnvironmentField.get(null) as MutableMap<String, String>
        envFields.putAll(environmentVariables)
        val theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment")
        theCaseInsensitiveEnvironmentField.isAccessible = true
        val caseInsensitiveEnvFields = theCaseInsensitiveEnvironmentField.get(null) as MutableMap<String, String>
        caseInsensitiveEnvFields.putAll(environmentVariables)
    } catch (e: NoSuchFieldException) {
        val classes = Collections::class.java.declaredClasses
        val env = System.getenv()
        for (klass in classes) {
            if ("java.util.Collections\$UnmodifiableMap" == klass.name) {
                val field = klass.getDeclaredField("m")
                field.setAccessible(true)
                val obj = field.get(env)
                val map = obj as MutableMap<String, String>
                map.clear()
                map.putAll(environmentVariables)
            }
        }
    }

}