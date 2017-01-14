package jetbrains.rsynk

import jetbrains.rsynk.protocol.Option
import jetbrains.rsynk.protocol.RequestParser
import org.junit.Assert
import org.junit.Test

class RequestParserTest {
  @Test
  fun parse_single_file_request_default_options_test() {
    val parser = RequestParser("--server --sender -e.LsfxC .module/file")
    Assert.assertEquals(7, parser.options.size) // 2 full named + 5 short named
    Assert.assertTrue(parser.options.contains(Option.SERVER))
    Assert.assertTrue(parser.options.contains(Option.SENDER))
    Assert.assertTrue(parser.options.contains(Option.COPY_LINKS))
    Assert.assertTrue(parser.options.contains(Option.PROTECT_ARGS))
    Assert.assertTrue(parser.options.contains(Option.FILTER))
    Assert.assertTrue(parser.options.contains(Option.ONE_FILE_SYSTEM))
    Assert.assertTrue(parser.options.contains(Option.CVS_EXCLUDE))
    Assert.assertEquals(1, parser.files.size)
    Assert.assertEquals("module/file", parser.files[0])
  }
}