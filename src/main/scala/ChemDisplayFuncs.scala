object ChemDisplayFuncs {
  /** Wraps the given input string, with </html> tags at the beginning and end.
   * Any other tags required should already be present in the input string.*/
  def wrapInHTML(input: String): String = {
    "<html>" + input + "</html>"
  }

  /** Rounds the given Double number to sig_fig significant figures.
   * Adds extra zeroes to the end if sig_fig is greater than the actual precision of number.*/
  def roundSF(number: Double, sig_fig: Int): String = {
    val format_instructs = s"%${sig_fig}.${sig_fig}g"
    val rounded = number.formatted(format_instructs)
    var answer = rounded.replace("e+0", " * 10<sup>")
    if (answer.contains("<sup")) {
      answer += "</sup>"
    }
    answer
  }
}
