# proguard-generator

## Description:
The Kotlin ProGuard Configuration Generator is a sophisticated module designed to enhance the security and performance of Android applications. It leverages Kotlin's metaprogramming capabilities to produce a ProGuard configuration file at compile time. This automation streamlines the process of specifying which classes and methods should not be obfuscated, ensuring that critical code remains intact while optimizing the final APK size.

## Key Features:
- __Compile-Time Generation:__ This module operates exclusively at compile time, seamlessly integrating the ProGuard configuration file into the Android application's build process. This approach eliminates the risk of runtime issues related to incorrect or missing ProGuard rules.

- __Tag-Based Annotation:__ The module utilizes specific tags as annotations within the Kotlin source code. These tags allow developers to annotate classes and methods that should be exempt from obfuscation. By designating certain code as non-obfuscatable, developers can safeguard critical functionality and data.

- __Customizable Configuration:__ Developers have the flexibility to configure the module to meet project requirements. They can define custom tags, specify output file locations, and tailor the ProGuard rules to suit the application's unique needs.

- __Integration with Android Gradle:__ The module seamlessly integrates with the Android Gradle build system. Developers can easily incorporate the ProGuard configuration generation process into their app's build.gradle file, ensuring a smooth and automated workflow.

- __Extensibility:__ The module's architecture supports extensibility, enabling developers to create custom tag processors or integrate additional sources of information to generate precise and customized ProGuard rules.

## Use Cases:

- __Security:__ The module provides a powerful means of protecting sensitive code and data by selectively avoiding obfuscation for designated classes and methods. This makes it significantly more challenging for potential attackers to reverse engineer the application.

- __Performance Optimization:__ Developers can use the module to ensure that critical classes and methods are preserved, optimizing the performance of key application components while reducing the APK size by obfuscating less critical code.

- __Custom Rules:__ The module empowers developers to define custom ProGuard rules, accommodating unique application requirements and maintaining compatibility with third-party libraries.

#### Getting Started
import this 2 process inside your gradle 
```gradle

    implementation project(':module-annotations')
    implementation project(':module-processes')
    kapt project(':module-processes')

```

remember to add the kapt command before because In a Gradle Android project that uses Kotlin for code, kapt is a command used in conjunction with the Kotlin Annotation Processor Tool (KAPT). It is used to invoke annotation processing for libraries that utilize annotation processors, typically for generating code or performing other tasks during the build process.

```gradle

    apply plugin: "kotlin-kapt"

```

You can add this 2 annotations @ReflectedMethod and @ReflectedClass. These annotations signal to the module-processes to keep these elements intact during the obfuscation process:

```java

# Java code

@ReflectedMethod
public void myMethod(int param1, String param2) {
    //Keep
}

@ReflectedClass
public class MyClass(){
    //keep
}
    
```

```kotlin

# Kotlin code

@ReflectedMethod
fun myMethod(param1 int, param2 string) {
    //Keep
}

@ReflectedClass
class MyClass(){
    //keep
}
    
```

#### Contributors
Daniele Carrozzino - carroch97@outlook.it