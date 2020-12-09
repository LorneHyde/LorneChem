# LorneChem
This is a desktop app for basic chemistry calculations. Currently, it features an equation balancer, a formula mass
calculator, and a percentage by mass calculator.

## Background
LorneChem started as my quarantine project during 2020. It started as a chance for me to practice writing GUIs 
and structuring my code, but it grew to be a larger application. My focus is to make it as user-friendly as possible, 
with explanations behind any user-input errors, to avoid confusing the user! I hope to develop this project even further
in the future!
## Installation
Prerequisites are:
- the **jvm** (https://www.java.com/en/download/)
- **Python** (https://www.python.org/downloads/). 
- **sympy** for Python, which you can get via ```pip install sympy``` on the command line, or by 
following the instructions **here** (https://github.com/sympy/sympy)

This app only works on Windows. The zip file containing the jar is too large to store on GitHub, so feel free to download it from my Google Drive instead (https://drive.google.com/file/d/1n1JwQ0dnVZ4UrL4J0Ni3En-97wnwyw6R/view?usp=sharing). Simply download it, **unzip the folder**, and double click on the jar to run!
## Screenshots
### Sample Error Messages
![A screenshot showing a popup error message, stating that "The products field is blank" and "Potato is not a valid chemical formula."](/screenshots/potato.PNG "Example of an error message")

---
![A screenshot showing a popup error message, stating that "The products are the same as the reactants, so no reaction actually occurs."](/screenshots/products_same_reactants.PNG "Example of an error message")

---
![A screenshot showing a popup error message, stating that "The equation given is mathematcially impossible, and cannot be balanced"](/screenshots/mathematically_impossible.PNG "Example of an error message")

---
![A screenshot showing a popup error message, stating that "Em is not a valid element of the periodic table" and "Ch is not a valid element of the periodic table."](/screenshots/invalid_elems.PNG "Example of an error message")

### Several Windows Can Be Open at Once
![The main menu and all 3 sub-applications are all open at once](/screenshots/multiple_guis.PNG "Multiple windows")

### Equation Being Balanced
(Note that my program is able to correctly parse formulae with brackets)
![A screenshot showing my program balancing the equation for the reaction of calcium hydroxide with hyrdrochloric acid"](/screenshots/calcium_hydroxide_reaction.PNG "Equation balancer")

### Formula Mass Calculator
(Note that this works for both molecular formulae and structural formulae, as shown)
![A screenshot showing my program correctly calculating the formula mass of propanoic acid as 74.1"](/screenshots/formula_mass.PNG "Formula mass calculator")

### Percentage by mass calculator

If the checkbox is ticked, the answer will be given alongside an explanation:
!["My program giving a detailed explaation of how to calculate the percentage by mass of hydrogen in ethanoic acid"](/screenshots/workings.PNG "Percentage by mass calculator with workings")
Otherwise the answer will be given by itself:
!["My program correctly giving a percentage by mass of hydrogen in ethanoic acid as 6.71%"](/screenshots/percentage_by_mass.PNG "Percentage by mass calculator without workings")

### Elements can be supplied via name or symbol
!["My program correctly giving a percentage by mass of hydrogen in sulfuric acid as 2.06%"](/screenshots/element_as_word.PNG "Element hydrogen is supplied by name")

### Settings
![A drop-down menu for the user to choose how many significant figures should be present in the output"](/screenshots/settings.PNG "Settings")

## Planned updates
The next feature I am planning to add will be a tool to convert mass to moles (and vice versa). This will then lead into
a tool that calculates percentage yields.
## License and Credits
LorneChem is free and open-source software, made by Lorne Hyde.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
