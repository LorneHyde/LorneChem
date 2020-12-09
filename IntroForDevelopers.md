# **Introduction**

LorneChem has 3 main features: An equation balancer, a formula mass calculator, and a percentage by mass calculator. More features are to be added in the future, and this guide is written for any developer who wishes to learn more about the app in order to add new features.

## **Contents**
- [Setting up](#setting-up)
- [Front End](#front-end)
- [User Input Parsing](#user-input-parsing)
- [Dealing with User Mistakes](#dealing-with-user-mistakes)
- [Making Chemical Calculations](#making-chemical-calculations)
- [Displaying Numerical Answers](#displaying-numerical-answers)

# **Setting up**

Prerequisites for running the app are:
- the **jvm** (https://www.java.com/en/download/)
- **Python** (https://www.python.org/downloads/). 
- **sympy** for Python, obtained from typing ```pip install sympy``` on the command line, or by 
following [these instructions](https://github.com/sympy/sympy)

To make changes, you should fork the project's [GitHub repository](https://github.com/LorneHyde/LorneChem). You can test that everything works correctly by running [these unit tests](https://github.com/LorneHyde/LorneChem/tree/master/src/test/scala). 

# **Front End**

## **ChemAppFrame Trait**
The front end of the program is the object `ChemGUI` which is a member of the `ChemGUIClass`, and it uses the Scala Swing Library to provide the GUI. 

To provide a consistent interface for the user, all GUI windows **must** implement the trait `ChemAppFrame` (found within the `ChemGUIClass`). This trait provides a default menu bar and a standard quitting dialogue. `ChemAppFrame` also provides the following traits which may be implemented by certain components (eg: labels and buttons) within a given appframe:

- **`SafeLabel`:** A label which checks for errors before updating. If there are user input errors than an error dialogue is shown rather than updating the label (cf: dealing with user input mistakes)
- **`SafeLabelFromButton`:** A SafeLabel which attempts to update whenever a given button is pressed.
- **`SafeLabelOptWorkings`:** A SafeLabel with the additional option of showing how the answer was achieved, if a given box is ticked.
- **`SafeLabelFromButtonOptWorkings`:** A SafeLabelFromButton with the additional option of showing how the answer was achieved, if a given box is ticked.

## **Implemented AppFrames**
Currently, the application has 5 implemented AppFrames. The `top` frame is the main menu that starts when the app is first launched, and the Swing Library **prevents this name from being changed**. 

The `SettingsGui` is accessible from the menu bar, and it has options to change the number of significant figures presented in the labels of any other GUI (see [displaying numerical answers](#displaying-numerical-answers)). 

The remaining AppFrames are EquationBalanceGui, FormulaMassGui, and PbmGui, which correspond to the Equation Balancer, Formula Mass Calculator, and Percentage by Mass calculators respectively.

# **User Input Parsing**
Context free grammars are not needed for parsing chemical formulae, as the set of valid chemical formulae is a regular language. This might be surprising, since brackets are allowed in formulae such as Ca(OH)<sub>2</sub>, but brackets may not be nested, which solves the problem. This means that only regular expressions are required.

## **Parser Objects for Molecular Formulae**
Both objects `ParseToHTML` and `ParseToMap` are used for parsing molecular formulae.

`ParseToHTML` subscripts the numbers in chemical formulae (for example, "H2O" would be converted "H\<sub>2\</sub>O"). Note that the parsed formula is not yet wrapped in \<HTML>\</HTML> tags  - this is so that multiple formulae can be placed together in the same string later on. Just before displaying the string, ChemDisplayFuncs.wrapInHTML should be called to add these tags. 

ParseToMap converts the input into a Map of type Map[ChemElement, Int], to record how many elements there are of each type in the formula. The output map represents the underlying molecular formula even when the input was given as a structural formula: for instance,  "CH3COOH" parses to `Map(ChemElement("C") -> 2, ChemElement("H") -> 4, ChemElement("O") -> 2)`.

Both of these parsers inherit from the traits TotalParser and ChemSymbolParser. TotalParser extends the RegexParsers trait from scala.util.parsing.combinator, and provides an additional function to parse the entire input string and return the fully parsed output as a non-parser type. ChemSymbolParser provides simple regex parsers for elements and numbers which may be found within a formula.

 ## **Other Parsers**

The only other parser-like object in the program is ParseFormulaList, which splits the input string by commas or plus signs, and returns a list of chemical formulae. This is used in the equation balancer.

# **Dealing with User Mistakes**
The `ErrorSet` object keeps a list of all non-recoverable user input mistakes. It has methods to log each possible type of mistake, and also to create a user-friendly string that lists the mistakes. The `reset()` function should only be called when a new user input is given, such that mistakes in the previous input then become irrelevant. 

## **Tracking Two Sets of Mistakes**

The functions `removeUnnecessaryWarnings()` and `mergePotentialMistakes()` are a potential source of confusion. To understand them better, take the following example:

Imagine that the parser is asked to parse the formula "Abracadabra". Initially, it sees that "Abr" matches the element regex, but that "Abr" is not on the periodic table, so it calls `ErrorSet.addPotentialInvalidElem("Abr")`. The parser then sees that "Abracadabra" does not match the regex for chemical formulae, so it becomes clear that the only error that needs to be reported back to the user is that "Abracadabra is not a valid chemical formula." The fact that "Abr" isn't a valid element becomes irrelevant, since the user most likely didn't intend for "Abr" to be parsed as an element!

To deal with the fact that certain invalid-element type mistakes may become redundant, ErorSet's implementation actually keeps track of two separate private sets. The `PotentialMistakes` set contains possible instances of "Invalid Chemical Element" type mistakes to report back to the user, which may or may not be explainable by future "Invalid Chemical Formula" type mistakes to be found, whereas `MistakesSoFar` keeps track of all unexplanable mistakes (of any origin). If all `PotentialMistakes` are found to be explainable by members of `MistakesSoFar` then the `removeUnnecessaryWarnings()` function should be called. However, if the entire contents of `PotentialMistakes` is unexplainable, then the `mergePotentialMistakes()` function should be called, to add the contents of  `PotentialMistakes` into `MistakesSoFar`. 

The `ParseToMap` object shows this in use (cf: expression parsing):

```
override def parseInput(input: String): Map[ChemElement, Int] = {
    try {
      val answer = super.parseInput(input)
      ErrorSet.mergePotentialMistakes()
      answer
    }
    catch {
      case re: RuntimeException if re.getMessage == "No result when parsing failed" =>
        ErrorSet.addInvalidFormula(input)
        Map(ChemElement.placeHolderElement -> 1)
    }
    finally {
      ErrorSet.removeUnnecessaryWarnings()
    }
  }
```

This function attempts to parse the user's input. If no exception is thrown, this implies that the given formula matches the standard pattern for a formula, so any invalid elements can't be explained by an invalid formula, and the set of potential mistakes should be merged. 

On the other hand, if the exception shown is thrown, then the parsing of the whole formula has failed, and any invalid chemical elements within this formula need not be reported back to the user.
# **Making Chemical Calculations**

## **Storing Properties of Chemical Elements**

Information about each of the chemical elements is stored in ElementData.json , which was taken from https://github.com/Bowserinator/Periodic-Table-JSON and modified to remove some unnecessary information. Each `ChemElement` object has a corresponding (private) `ChemElementWithData` object, which stores a variety of properties of each element. Currently, the only properties made public are `name`, `symbol`, `mass`, and `atomic number`, because no other properties are currently used. Contributers may wish to add public getter methods for the other properties should they choose to add features which depend on them. For instance, one may wish to add a new feature to the app that predicts the products when certain chemicals react - this would make use of the `shells` property.

The `ChemElement` constructor will reject any 'element' which does not appear on the periodic table, with the one exception of a fake element, `ChemElement.placeHolderElement`. This placeholder element is useful for error recovery when parsing - even after finding an invalid element in a formula, the parser simply replaces it with the placeholder element and continues to search for further user mistakes later in the string. 

## **Chemical Compound Calculations**
The case class `ChemFormula` is used to keep track of a given chemical compound. It can access any public property of its constituent elements, which is used to calculate the formula mass or the percentage by mass of a given element. 

## **Equation Balancer**
If you are unfamiliar with the principle of balanced chemical symbol equations, please visit https://www.bbc.co.uk/bitesize/guides/zg2h4qt/revision/6 before continuing.

The `balance()` function in the `EquationBalancer` object does the majority of the work for the equation balancer section of the app. Although it is currently only used for the equation balancer window of the program, if the program is ever extended with new types of calculation that require a balanced equation then the `balance()` function should be be used for those calculations too. 

The `balance()` function calls a python program `nullspace.py` to find the nullspace of the matrix of element counts. This decision was made for simplicity, because the Sympy library has functions to find the nullspace of a matrix, and no free Scala or Java library has the same function. However, it may be beneficial to eventually write this function manually in Scala, to speed up the program. 

# **Displaying Numerical Answers**

## **Significant figures**
The user is able to change the number of default significant figures given in numerical answers, via the settings GUI accessed from the menu bar. Any new features involving calculations **must** take this into account, and call `ChemDisplayFuncs.roundSF` before displaying a numerical answer. The settings are saved to the file `settings.txt` when the program is closed.

## **Workings**
This software is educational, so any GUI for a feature that displays a numerical answer should include a checkbox for displaying workings, such that the user can understand how the calculation was carried out. The `Workings` singleton object should then contain a corresponding function which returns the string of workings for a calculation, and the corresponding GUI should contain a `SafeLabelOptWorkings` to display the answer. See [ChemAppFrame Trait](#chemappframe-trait) to learn about `SafeLabelOptWorkings`.