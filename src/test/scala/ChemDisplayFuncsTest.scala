import ChemDisplayFuncs._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ChemDisplayFuncsTest extends AnyFlatSpec with should.Matchers {
  "A string" should "get correctly wrapped in HTML" in {
    wrapInHTML("Hello") should be("<html>Hello</html>")
  }
  "A Double" should "be correctly rounded DOWN after the decimal point to the specified number of significant figures" in {
    roundSF(3.123213, 3) should be("3.12")
    roundSF(36.123213, 4) should be("36.12")
  }
  "A Double" should "be correctly rounded UP after the decimal point to the specified number of significant figures" in {
    roundSF(3.99999, 3) should be("4.00")
    roundSF(3.9955, 4) should be("3.996")
  }
  "A large integer" should "be correctly rounded DOWN to the specified number of significant figures" in {
    roundSF(1234, 3) should be("1.23 * 10<sup>3</sup>")
  }
  "A large integer" should "be correctly rounded UP to the specified number of significant figures" in {
    roundSF(1239, 3) should be("1.24 * 10<sup>3</sup>")
  }
  "A number with low precision" should "gain extra precision" in {
    roundSF(1, 3) should be ("1.00")
  }
}


