import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ChemElementTest extends AnyFlatSpec with should.Matchers {
  "A non-existent element" should "throw an exception" in {
    intercept[RuntimeException] {ChemElement("Blah blah blah")}
  }
  "A valid symbol" should "give the appropriate chemical element" in {
    ChemElement("H").atomicNumber should be (1)
    ChemElement("He").atomicNumber should be (2)
    ChemElement("Uue").atomicNumber should be (119)
  }
  "A valid element name" should "give the appropriate chemical element" in {
    ChemElement("Hydrogen").atomicNumber should be (1)
    ChemElement("Helium").atomicNumber should be (2)
    ChemElement("Ununennium").atomicNumber should be (119)
  }
  "A valid lowercase element name" should "give the appropriate chemical element" in {
    ChemElement("hydrogen").atomicNumber should be (1)
    ChemElement("helium").atomicNumber should be (2)
    ChemElement("ununennium").atomicNumber should be (119)
  }
  "The placeholder element" should "have atomic number of 0" in {
    ChemElement.placeHolderElement.atomicNumber should be (0)
  }
  "All 3 ways of representing the same element" should "compare equal" in {
    ChemElement("hydrogen") should equal (ChemElement("Hydrogen"))
    ChemElement("hydrogen") should equal (ChemElement("H"))
    ChemElement("H") should equal (ChemElement("Hydrogen"))
  }
}