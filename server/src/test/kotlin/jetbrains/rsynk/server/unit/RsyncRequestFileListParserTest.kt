/**
 * Copyright 2016 - 2017 JetBrains s.r.o.
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
package jetbrains.rsynk.server.unit

import jetbrains.rsynk.exitvalues.ArgsParingException
import jetbrains.rsynk.request.RsyncServerSendRequestParser
import org.junit.Assert
import org.junit.Test

class RsyncRequestFileListParserTest {

    @Test
    fun no_files_test() {
        val data = RsyncServerSendRequestParser.parse(listOf("rsync", "--server", "--sender", "-LCd", "."))
        Assert.assertTrue(data.files.isEmpty())
    }

    @Test
    fun parse_files_test() {
        val data = RsyncServerSendRequestParser.parse(listOf("rsync", "--server", "--sender", "-Ld", ".", "/path/to/first/file", "path/to/second-file"))
        Assert.assertEquals(data.files.joinToString(), 2, data.files.size)
        Assert.assertTrue(data.files.contains("/path/to/first/file"))
        Assert.assertTrue(data.files.contains("path/to/second-file"))
    }

    @Test
    fun parse_pre_release_info_test() {
        val data = RsyncServerSendRequestParser.parse(listOf("rsync", "--server", "--sender", "-Le31.100002C", ".", "/path/to/first/file"))
        Assert.assertEquals(data.files.joinToString(), 1, data.files.size)
    }

    @Test(expected = ArgsParingException::class)
    fun parse_non_rsync_command_test() {
        RsyncServerSendRequestParser.parse(listOf("--server", "-e.sd", "."))
    }
}