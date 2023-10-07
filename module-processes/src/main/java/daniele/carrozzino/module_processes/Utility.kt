package daniele.carrozzino.module_processes

import java.io.IOException
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.StandardLocation

sealed class Utility{

    companion object {

        /**
         * Get the main information from every methods
         * to populate the {@link ProcessManager#methodMap}
         * */
        fun selectMethod(element: Element) {
            val signature = getSignature(element)
            val enclosingClass = getEnclosingClass(element as ExecutableElement)
            val className = getClassName(enclosingClass ?: element as TypeElement)

            addMethod(className, signature)
            log(element, signature, className)
        }

        /**
         * Connect every method to its class owner
         * through the main map of ProcessManager
         * */
        private fun addMethod(key: String, value: String) {
            if (ProcessManager.methodMap.containsKey(key)) {
                ProcessManager.methodMap[key]?.add(value)
            } else {
                ProcessManager.methodMap[key] = arrayListOf(value)
            }
        }

        /**
         * If a class has only the ReflectedClass annotation
         * I take everything otherwise I got already all
         * what I needed before
         * */
        fun addClass(key: String) {
            if (!ProcessManager.methodMap.containsKey(key)) {
                ProcessManager.methodMap.put(key, arrayListOf())
            }
        }

        private fun log(element: Element, signature: String, className: String) {
            ProcessManager.log +=
                "kind      - ${element.kind.name}\n" +
                        "signature - $signature\n" +
                        "className - $className\n" +
                        "name      - ${element.simpleName}\n------------------\n"
        }

        /**
         * Create the final file as result
         *  - create a fake proguard-rules.pro
         *  right here YourAppName\app\build\generated\source\kapt\variant
         * */
        fun write(processingEnv : ProcessingEnvironment) {
            // Generate a file based on the annotated method
            val fileName = "proguard-rules.pro"
            var sourcePath: Path? = null
            var destinationPath: Path? = null
            try {
                val fileObject2 =
                    processingEnv.filer.createResource(StandardLocation.SOURCE_OUTPUT, "", fileName)

                val resourceUri = fileObject2.toUri()
                sourcePath = Paths.get(resourceUri)

                var subPath = ""
                var numOfSubFolder = 0
                while(!(subPath.startsWith("build\\generated") || subPath.startsWith("build/generated"))){
                    subPath = sourcePath.subpath(numOfSubFolder, sourcePath.nameCount).toString()
                    numOfSubFolder++
                }
                destinationPath = Paths.get(
                    sourcePath.toString().replace(
                        subPath,
                        fileName
                    )
                )

                println(" >> write result")
                fileObject2.openWriter().use { writer ->
                    writer.write(ProcessManager.getTextFromMap())
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                if (sourcePath != null && destinationPath != null) {
                    println(" >> move result")
                    Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /**
         * getSignature
         *
         * Create the signature of the method from the ExecutableElement
         * properties.
         *
         * val methodName = methodElement.simpleName
         * val parameters = methodElement.parameters
         * val returnType = methodElement.returnType
         *
         * @param element annotated method find from the annotationProcess
         * */
        private fun getSignature(element: Element): String {
            val methodElement = element as ExecutableElement

            // Access method signature components
            /*val methodName = methodElement.simpleName.toString()
        val parameters = methodElement.parameters
        val returnType = methodElement.returnType*/

            return methodSignature(methodElement)
        }

        /**
         * methodSignature
         *
         * convert an ExecutableElement to a string formatted like:
         * public/private
         * returnType(as void, int ...)
         * simpleName(method's name)
         * parameters(parameters' type)
         *
         * example:
         * private void printSomething(int, java.lang.String)
         *
         * @param methodElement annotated method casted to ExecutableElement
         * @return signature by string
         * */
        private fun methodSignature(methodElement: ExecutableElement): String {
            val signature = StringBuilder()

            val isPublic = methodElement.modifiers.contains(Modifier.PUBLIC)
            val isPrivate = methodElement.modifiers.contains(Modifier.PRIVATE)
            if (isPublic) signature.append("public ") else if (isPrivate) signature.append("private ")

            signature.append(methodElement.returnType).append(" ")
            signature.append(methodElement.simpleName).append("(")
            val parameters: List<VariableElement> = methodElement.parameters
            for ((i, param) in parameters.withIndex()) {
                signature.append(param.asType().toString().split("<")[0])
                if (i < parameters.size - 1) {
                    signature.append(", ")
                }
            }
            signature.append(");")
            return signature.toString()
        }

        /**
         * getClassName
         *
         * @param element class object
         * @return class full name
         * */
        fun getClassName(element: TypeElement): String {
            return element.qualifiedName.toString()
        }

        /**
         * get the parent class of a method
         *
         * @param methodElement method object
         * @return class object as TypeElement
         * */
        private fun getEnclosingClass(methodElement: ExecutableElement): TypeElement? {
            var currentElement: Element? = methodElement
            while (currentElement != null && currentElement.kind != ElementKind.CLASS) {
                currentElement = currentElement.enclosingElement
            }
            return currentElement as? TypeElement
        }
    }
}
