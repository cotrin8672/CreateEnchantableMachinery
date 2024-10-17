import org.gradle.api.attributes.Attribute
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope

val patchedFMLModType = Attribute.of("patchedFMLModType", Boolean::class.javaObjectType)

fun DependencyHandlerScope.kotlinForgeRuntimeLibrary(dependency: Provider<*>) {
    "localRuntime"(dependency) {
        isTransitive = false
        attributes {
            attribute(patchedFMLModType, true)
        }
    }
}
