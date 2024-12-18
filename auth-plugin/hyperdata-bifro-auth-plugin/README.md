# BifroMQ Plugin Archetype

This Maven Archetype helps you quickly bootstrap a new BifroMQ plugin project with a pre-configured project structure and necessary dependencies.


Before you begin, ensure you have the following installed on your system:

- Java JDK 17 or higher
- Maven 3.6.3 or higher


To generate a new project using the BifroMQ Plugin Archetype, run the following command in your terminal:

```bash
mvn archetype:generate \
    -DarchetypeGroupId=com.baidu.bifromq \
    -DarchetypeArtifactId=bifromq-plugin-archetype \
    -DarchetypeVersion=3.3.3 \
    -DgroupId=com.yourcompany.newproject \
    -DartifactId=your-plugin-name \
    -Dversion=1.0.0-SNAPSHOT \
    -DpluginName=YourPluginClassName \
    -DpluginContextName=YourPluginContextClassName \
    -DbifromqVersion=BifroMQVersion
    -DinteractiveMode=false
```

Replace com.yourcompany.newproject, your-plugin-name, YourPluginClassName, YourPluginContextClassName, BifroMQVersion with your own values.


The generated project is a multi-module Maven project with the following structure:
```plaintext
your-plugin-name/
├── auth-provider/ <-- auth provider module as a reference for other bifromq plugin, you can remove it if not needed
│   └── src/
│       └── main/
│           └── java/
│               └── com.yourcompany.newproject/
│                   └── YourPluginClassNameAuthProvider.java
├── plugin-build/  <-- plugin-build module to build the plugin zip file
│   ├── assembly/
│   │   └── assembly-zip.xml
│   ├── conf/      <-- folder to contain plugin configuration files
│   │   ├── config.yaml <-- plugin configuration file
│   │   └── logback.xml <-- logback configuration file for the plugin
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └──com.yourcompany.newproject/
│   │               └── YourPluginClassName.java <-- Your plugin main class
│   └── target/
│       └── pom.xml
├── plugin-context/  <-- plugin-context module to define the plugin context
│   └── src/
│       └── main/
│           └── java/
│               └── ─com.yourcompany.newproject/
│                   └──YourPluginContextClassName.java
└── pom.xml
```


```bash
mvn clean package
```

The output plugin zip file will be generated in the target directory. Install the plugin by copying the zip file into the BifroMQ plugin folder. Ensure you verify the plugin is loaded correctly by checking the BifroMQ management console or
logs.

---bifrom mq start
For auth plugin to work, set environment variables for passing downstream service for auth
hard coded users are: saraheem, araheem, mqttrouter

Windows Command Prompt

$ set EXTRA_JVM_OPTS=-Dplugin.hdcauthprovider.url=http://localhost:5098
$ standalone.bat start