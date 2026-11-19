package io.github.linewelder.medus.ui.components

import android.graphics.*
import android.graphics.Matrix
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.tooling.preview.*
import androidx.core.graphics.*
import kotlinx.coroutines.*
import kotlin.random.*

private val PARTICLE_SIZE = Size(30f, 30f)
private val GRAVITY = Offset(0f, 400f)
private val MIN_LAUNCH_VELOCITY = Offset(-300f, -800f)
private val MAX_LAUNCH_VELOCITY = Offset(300f, -500f)
/**
 * How much confetti's initial velocities are pulled towards the center of the canvas.
 */
private const val LAUNCH_VELOCITY_CENTER_PULL = 0.5f
private const val DEFAULT_SPAWN_NUMBER = 50
private val PARTICLE_COLORS = listOf(
    Color.Red, Color.Blue, Color.Yellow, Color.Green, Color.Magenta,
)

/**
 * Adds a confetti overlay to the given content.
 * To spawn confetti, use [ConfettiState] via [LocalConfettiState] from inside the content.
 */
@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember { ConfettiState() }
    var frameVersion by remember { mutableIntStateOf(0) }

    LaunchedEffect(state) {
        var previousFrameNanos = withFrameNanos { it }

        while (isActive) {
            withFrameNanos { frameNanos ->
                val deltaSeconds = (frameNanos - previousFrameNanos) / 1_000_000_000f
                previousFrameNanos = frameNanos

                if (state.update(deltaSeconds)) {
                    frameVersion++
                }
            }
        }
    }

    Box(
        modifier
            .onGloballyPositioned { state.canvasCoordinates = it }
            .drawWithContent {
                // Required to redraw the screen when the frame version changes.
                @Suppress("unused", "UnusedVariable")
                val observedFrame = frameVersion

                drawContent()
                drawConfetti(state)
            }
    ) {
        CompositionLocalProvider(LocalConfettiState provides state) {
            content()
        }
    }
}

private fun ContentDrawScope.drawConfetti(state: ConfettiState) {
    drawIntoCanvas { canvas ->
        val nativeCanvas = canvas.nativeCanvas
        val camera = Camera()
        val projection = Matrix()

        for (particle in state.particles) {
            camera.save()
            camera.rotate(particle.rotationX, particle.rotationY, 0f)
            camera.getMatrix(projection)
            camera.restore()

            nativeCanvas.withTranslation(particle.position.x, particle.position.y) {
                concat(projection)
                drawRect(
                    color = particle.color,
                    topLeft = Offset(
                        -PARTICLE_SIZE.width / 2f,
                        -PARTICLE_SIZE.height / 2f,
                    ),
                    size = PARTICLE_SIZE,
                )
            }
        }
    }
}

class ConfettiState {
    internal val particles: MutableList<Particle> = mutableListOf()
    private val random: Random = Random(0)
    lateinit var canvasCoordinates: LayoutCoordinates

    /**
     * Spawn confetti particles.
     *
     * @param position Relative to the parent [ConfettiOverlay]
     */
    fun spawn(position: Offset, number: Int = DEFAULT_SPAWN_NUMBER) {
        repeat(number) {
            particles.add(createRandomParticle(position))
        }
    }

    internal fun update(deltaSeconds: Float): Boolean {
        if (particles.isEmpty()) return false

        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()

            particle.update(deltaSeconds)
            if (particle.position.y >= canvasCoordinates.size.height) {
                iterator.remove()
            }
        }

        return true
    }

    private fun createRandomParticle(position: Offset): Particle {
        val canvasCenter = Offset(
            canvasCoordinates.size.width.toFloat() / 2f,
            canvasCoordinates.size.height.toFloat() / 2f,
        )
        val offsetFromCenter = position - canvasCenter

        val initialVelocity = Offset(
            random.nextDouble(
                MIN_LAUNCH_VELOCITY.x.toDouble(),
                MAX_LAUNCH_VELOCITY.x.toDouble(),
            ).toFloat(),
            random.nextDouble(
                MIN_LAUNCH_VELOCITY.y.toDouble(),
                MAX_LAUNCH_VELOCITY.y.toDouble(),
            ).toFloat(),
        ) - offsetFromCenter * LAUNCH_VELOCITY_CENTER_PULL

        return Particle(
            color = PARTICLE_COLORS[random.nextInt(PARTICLE_COLORS.size)],
            spinX = random.nextFloat(),
            spinY = random.nextFloat(),

            position = position,
            velocity = initialVelocity,
            rotationX = 0f,
            rotationY = 0f,
        )
    }
}

/**
 * Provides
 */
val LocalConfettiState = staticCompositionLocalOf<ConfettiState> {
    error("ConfettiState has not been provided")
}

internal data class Particle(
    val color: Color,
    val spinX: Float,
    val spinY: Float,
    var position: Offset,
    var velocity: Offset,
    var rotationX: Float,
    var rotationY: Float,
) {
    fun update(deltaSeconds: Float) {
        rotationX += spinX * 360f * deltaSeconds
        rotationY += spinY * 360f * deltaSeconds
        position += velocity * deltaSeconds
        velocity += GRAVITY * deltaSeconds
    }
}

@Preview
@Composable
private fun ConfettiOverlayPreview() {
    ConfettiOverlay(Modifier.fillMaxSize()) {
        val state = LocalConfettiState.current
        Box(Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures { offset ->
                state.spawn(offset)
            }
        })
    }
}
