package se.zensum.test.util

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.util.*


@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@ExtendWith(EnvironmentVariableExtension::class)
 annotation class EnvironmentVariable(val key: String, val value: String)

// Remove when https://youtrack.jetbrains.com/issue/KT-12794 is solved and use @Repeated annotation
@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@ExtendWith(EnvironmentVariablesExtension::class)
annotation class EnvironmentVariables(vararg val environmentVariables: EnvironmentVariable)

class EnvironmentVariablesExtension : AfterEachCallback, BeforeEachCallback {
    override fun afterEach(context: ExtensionContext) {
        val annotations = context.testMethod.get().getAnnotation(EnvironmentVariables::class.java)
        val annotationMap = annotations.environmentVariables.map { Pair(it.key, it.value) }.toMap()
        clearEnv(annotationMap)
    }

    override fun beforeEach(context: ExtensionContext) {
        val annotations = context.testMethod.get().getAnnotation(EnvironmentVariables::class.java)
        val annotationMap = annotations.environmentVariables.map { Pair(it.key, it.value) }.toMap()
        setEnv(annotationMap)
    }
}

class EnvironmentVariableExtension : AfterEachCallback, BeforeEachCallback {
    override fun afterEach(context: ExtensionContext) {
        val annotation = context.testMethod.get().getAnnotation(EnvironmentVariable::class.java)
        clearEnv(mapOf(annotation.key to annotation.value))
    }

    override fun beforeEach(context: ExtensionContext) {
        val annotation = context.testMethod.get().getAnnotation(EnvironmentVariable::class.java)
        setEnv(mapOf(annotation.key to annotation.value))
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