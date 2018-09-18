# Test-util



## Annotations
#### EnvironmentVariables
Make it possible to annotate functions and test classes with `@EnvironmentVariable("key","value")`. Annotation uses reflections to set environment variables in the running jvm instance.
```
@Test
@EnvironmentVariable(key="key",value="value")
fun test() {
    asserts...
}

-----

@EnvironmentVariables {
    EnvironmentVariable(key="key",value="value"),
    EnvironmentVariable(key="key2",value="value2")
}
class TestClass {

}


```

#### DockerCompose

```
@DockerCompose(filePath, logPath, varargs service)
class TestClass {
    @Test...
}
```

Starting a Docker compose file and a specific number of services. Service parameter should be a list of 
services you want to start from your docker-compose file.
