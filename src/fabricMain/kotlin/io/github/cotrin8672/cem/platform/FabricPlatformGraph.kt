package io.github.cotrin8672.cem.platform

import com.simibubi.create.foundation.data.CreateRegistrate
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.cotrin8672.cem.FabricCem

@ContributesBinding(PlatformScope::class)
@Inject
class FabricPlatformGraph : PlatformGraph {
    override val registrate: CreateRegistrate
        get() = FabricCem.registrate()
}
