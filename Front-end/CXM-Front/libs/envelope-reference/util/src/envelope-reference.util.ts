export function arraysOfObjectsHaveSameElements(array1: any[], array2: any[]): boolean {
  if (array1.length !== array2.length) {
    return false;
  }

  const sortedArray1 = [...array1].sort(compareObjects);
  const sortedArray2 = [...array2].sort(compareObjects);

  for (let i = 0; i < sortedArray1.length; i++) {
    if (!objectsAreEqual(sortedArray1[i], sortedArray2[i])) {
      return false;
    }
  }

  return true;
}

function compareObjects(a: any, b: any): number {
  return a.id - b.id;
}

function objectsAreEqual(a: any, b: any): boolean {
  return JSON.stringify(a) === JSON.stringify(b);
}

