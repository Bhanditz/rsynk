/**
 * Copyright 2016 - 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.rsynk.server.integration

import jetbrains.rsynk.rsync.files.RsynkFile
import jetbrains.rsynk.rsync.files.RsynkFileBoundaries
import jetbrains.rsynk.server.util.IntegrationTools
import jetbrains.rsynk.server.util.Rsync
import jetbrains.rsynk.server.util.RsyncIntegrationRule
import jetbrains.rsynk.server.util.RsynkResource
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger

class FileBoundariesIntegrationTest {

    val rsynk = rsynkResource.rsynk

    @Before
    fun clearTrackingFiles() {
        rsynk.untrackAll()
    }

    @Test
    fun set_left_file_bound_test() {
        val dataDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val sourceFile = File(dataDirectory, "from.txt")
        sourceFile.writeText(IntegrationTools.loremIpsum)

        val destinationDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val destinationFile = File(destinationDirectory, "to.txt")
        Assert.assertTrue("Cannot create new file", destinationFile.createNewFile())

        rsynk.track(RsynkFile(sourceFile.absolutePath) {
            RsynkFileBoundaries(10, IntegrationTools.loremIpsum.length.toLong() - 10)
        })

        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(10), destinationFile.readText())
    }

    @Test
    fun set_right_file_bound_test() {
        val dataDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val sourceFile = File(dataDirectory, "from.txt")
        sourceFile.writeText(IntegrationTools.loremIpsum)

        val destinationDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val destinationFile = File(destinationDirectory, "to.txt")
        Assert.assertTrue("Cannot create new file", destinationFile.createNewFile())

        rsynk.track(RsynkFile(sourceFile.absolutePath) {
            RsynkFileBoundaries(0, 20)
        })

        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(0, 20), destinationFile.readText())
    }

    @Test
    fun set_left_and_right_bounds_test() {
        val dataDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val sourceFile = File(dataDirectory, "from.txt")
        sourceFile.writeText(IntegrationTools.loremIpsum)

        val destinationDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val destinationFile = File(destinationDirectory, "to.txt")
        Assert.assertTrue("Cannot create new file", destinationFile.createNewFile())

        rsynk.track(RsynkFile(sourceFile.absolutePath) {
            RsynkFileBoundaries(10, 30)
        })

        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(10, 40), destinationFile.readText())
    }

    @Test
    fun can_change_an_offset_dynamically_test() {
        val dataDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val sourceFile = File(dataDirectory, "from.txt")
        sourceFile.writeText(IntegrationTools.loremIpsum)

        val destinationDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val destinationFile = File(destinationDirectory, "to.txt")
        Assert.assertTrue("Cannot create new file", destinationFile.createNewFile())

        var offset = 0L
        rsynk.track(RsynkFile(sourceFile.absolutePath) {
            RsynkFileBoundaries(offset, 10)
        })

        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(0, 10), destinationFile.readText())

        offset = 10L
        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(offset.toInt(), offset.toInt() + 10), destinationFile.readText())
    }

    @Test
    fun can_change_a_length_dynamically_test() {
        val dataDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val sourceFile = File(dataDirectory, "from.txt")
        sourceFile.writeText(IntegrationTools.loremIpsum)

        val destinationDirectory = Files.createTempDirectory("data-${id.incrementAndGet()}").toFile()
        val destinationFile = File(destinationDirectory, "to.txt")
        Assert.assertTrue("Cannot create new file", destinationFile.createNewFile())

        var length = 10L
        rsynk.track(RsynkFile(sourceFile.absolutePath) {
            RsynkFileBoundaries(10, length)
        })

        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(10, 10 + 10), destinationFile.readText())

        length = 20L
        Rsync.execute("localhost:${sourceFile.absolutePath}", destinationFile.absolutePath, rsynkResource.port, 10, "v")
        Assert.assertEquals(IntegrationTools.loremIpsum.substring(10, 10 + length.toInt()), destinationFile.readText())
    }

    companion object {
        val id = AtomicInteger(0)

        @ClassRule
        @JvmField
        val rsyncRule = RsyncIntegrationRule()


        @ClassRule
        @JvmField
        val rsynkResource = RsynkResource()
    }
}
