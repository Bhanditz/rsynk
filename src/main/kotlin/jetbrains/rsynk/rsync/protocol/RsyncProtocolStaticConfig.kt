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
package jetbrains.rsynk.rsync.protocol

import jetbrains.rsynk.rsync.flags.CompatFlags
import kotlin.experimental.or

object RsyncProtocolStaticConfig {
    val clientProtocolVersionMin = 31
    val serverProtocolVersion = 31
    val clientProtocolVersionMax = 31
    val serverCompatFlags: Byte = CompatFlags.SYMLINK_TIMES.mask or
            CompatFlags.SYMLINK_ICONV.mask or
            CompatFlags.SAFE_FILE_LIST.mask or
            CompatFlags.AVOID_FILE_ATTRS_OPTIMIZATION.mask
    val filesListPartitionLimit = 1024
    val chunkSize = 8 * 1024
}
