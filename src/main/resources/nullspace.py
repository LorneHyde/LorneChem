import sys
import json
from sympy import Matrix, lcm

jsonMatrix = sys.argv[1]

intMatrix = Matrix(json.loads(jsonMatrix))
nullSpace = intMatrix.nullspace()

if nullSpace:
    intSolList = []
    fractionalSolMatrix = nullSpace[0]
    fractionalSolList = [i[0] for i in fractionalSolMatrix.tolist()]
    denominators = []
    for i in fractionalSolList:
        denominators.append(i.q)
    multiplier = lcm(denominators)
    for (num, frac) in enumerate(fractionalSolList):
        intSolList.append(frac * multiplier)
else:  # If the nullspace is empty, we set it to include the zero vector
    intSolList = [0] * len(intMatrix.row(0))

print(intSolList)
