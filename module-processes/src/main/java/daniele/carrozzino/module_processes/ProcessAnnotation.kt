package daniele.carrozzino.module_processes

import daniele.carrozzino.module_annotations.ReflectedClass
import daniele.carrozzino.module_annotations.ReflectedMethod
import java.io.IOException
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.tools.StandardLocation

@SupportedSourceVersion(SourceVersion.RELEASE_8)
class ProcessAnnotation : AbstractProcessor() {

    override fun getSupportedAnnotationTypes() =
        mutableSetOf(ReflectedMethod::class.java.canonicalName)

    override fun process(annotations: MutableSet<out TypeElement>?,
                         roundEnv: RoundEnvironment
    ): Boolean {

        println("********************************")
        println(" - Started ProcessAnnotation")
        var numOfMethodFind = 0
        for (element in roundEnv.getElementsAnnotatedWith(ReflectedMethod::class.java)) {
            Utility.selectMethod(element)
            numOfMethodFind ++
        }
        println(" > Found $numOfMethodFind method")

        var numOfClassFind = 0
        for (element in roundEnv.getElementsAnnotatedWith(ReflectedClass::class.java)) {
            Utility.addClass(Utility.getClassName(element as TypeElement))
            numOfClassFind ++
        }
        println(" > Found $numOfClassFind class")

        if(numOfMethodFind > 0 || numOfClassFind > 0)
            Utility.write(processingEnv)
        println(" - Ended  ProcessAnnotation")
        println("********************************")

        return true
    }
}