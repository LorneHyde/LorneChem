import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class SettingsTest extends AnyFlatSpec with should.Matchers {
  "sigFig" should "be between 1 and 4" in {
    Settings.sigFig should be >= (1)
    Settings.sigFig should be <= (4)
  }
}