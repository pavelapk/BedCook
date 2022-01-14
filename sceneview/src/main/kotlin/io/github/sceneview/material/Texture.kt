package io.github.sceneview.material

import com.google.android.filament.Skybox
import com.google.android.filament.Texture
import com.google.android.filament.TextureSampler
import io.github.sceneview.Filament

class TextureSampler2D : TextureSampler(
    MinFilter.LINEAR_MIPMAP_LINEAR,
    MagFilter.LINEAR,
    WrapMode.REPEAT
)

class TextureSamplerExternal : TextureSampler(
    MinFilter.LINEAR,
    MagFilter.LINEAR,
    WrapMode.CLAMP_TO_EDGE
)

fun <R> Texture.use(block: (Texture) -> R): R = block(this).also { destroy() }

/**
 * @see Skybox.Builder.build
 */
fun Texture.Builder.build(): Texture = build(Filament.engine)

/**
 * Destroys a Texture and frees all its associated resources.
 */
fun Texture.destroy() {
    Filament.engine.destroyTexture(this)
}