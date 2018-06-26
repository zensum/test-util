# Test-util



## Annotations
#### EnvironmentVariables
Make it possible to annotate functions and test classes with `@EnvironmentVariable("key","value")`. Annotation uses reflections to set environment variables in the running jvm instance.
```
@Test
@EnvironmentVariable("key","value")
fun test() {
    asserts...
}

-----

@EnvironmentVariables {
    EnvironmentVariable("key","value"),
    EnvironmentVariable("key2","value2")
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
