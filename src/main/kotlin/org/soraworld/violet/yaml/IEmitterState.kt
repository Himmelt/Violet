package org.soraworld.violet.yaml

import java.io.IOException

interface IEmitterState {
    @Throws(IOException::class)
    fun expect()
}
