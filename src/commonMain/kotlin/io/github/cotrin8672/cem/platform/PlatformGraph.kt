package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateRegistrate
import dev.zacsweers.metro.DependencyGraph

object PlatformScope

@DependencyGraph(scope = PlatformScope::class)
interface PlatformGraph {
    val registrate: CreateRegistrate
}
