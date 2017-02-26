package jetbrains.rsynk

import jetbrains.rsynk.flags.CompatFlag
import jetbrains.rsynk.flags.decodeCompatFlags
import jetbrains.rsynk.flags.encode
import org.junit.Assert
import org.junit.Test

class CompatFlagsTest {
  @Test
  fun all_values_are_powers_of_two_test() {
    getAllFlags().forEach { flag ->
      Assert.assertTrue(flag.value.isPowerOfTwo())
    }
  }

  @Test
  fun have_all_required_powers_of_two_test() {
    val availablePowersOfTwo: Set<Int> = getAllFlags().map { it.value }.toSet()
    arrayOf(1, 2, 4, 8, 16, 32).forEach { value ->
      Assert.assertTrue("$value is not found among compat-flags", availablePowersOfTwo.contains(value))
    }
  }

  @Test
  fun encode_empty_set_to_zero_test() {
    Assert.assertEquals(0.toByte(), emptySet<CompatFlag>().encode())
  }

  @Test
  fun encode_full_values_to_63_test() {
    Assert.assertEquals(63.toByte(), getAllFlags().encode())
  }

  @Test
  fun decode_zero_to_empty_set_test() {
    Assert.assertEquals(emptySet<CompatFlag>(), 0.toByte().decodeCompatFlags())
  }

  @Test
  fun decode_63_to_full_values_set_test() {
    Assert.assertEquals(getAllFlags(), 63.toByte().decodeCompatFlags())
  }

  private fun getAllFlags(): Set<CompatFlag> {
    val allFlags = setOf(CompatFlag.IncRecurse,
            CompatFlag.SymlincTimes,
            CompatFlag.SymlinkIconv,
            CompatFlag.SafeFileList,
            CompatFlag.AvoidXattrOptimization,
            CompatFlag.FixChecksumSeed)
    val allFlagsText = allFlags.map { flag -> flag.javaClass.name }
    CompatFlag::class.nestedClasses.forEach { nestedClass ->
      Assert.assertTrue("Did you added new compat-flag? You have add that to tests!",
              allFlagsText.contains(nestedClass.java.name))
    }
    return allFlags
  }

  private fun Int.isPowerOfTwo(): Boolean = this.and(this - 1) == 0
}
