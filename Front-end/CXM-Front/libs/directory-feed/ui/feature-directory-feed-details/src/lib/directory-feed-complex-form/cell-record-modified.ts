import {
  CellDetails,
  CellObject,
  DirectoryFeedValue,
  FieldDetail,
  UpdatedDirectoryFeed,
} from '@cxm-smartflow/directory-feed/data-access';

interface CellProperty {
  value: string;
  fieldOrder: number;
  id: number;
  directoryFieldId: number;
}

export interface DirectoryFeedCellName {
  generateCellName(lineNumber: number, column: number): string;
}

export class CellRecordModified implements DirectoryFeedCellName {
  private cellModified = new Map<string, CellProperty>();

  get size() {
    return this.cellModified.size;
  }

  set(cellName: string, cellValue: any) {
    this.cellModified.set(cellName, cellValue);
  }

  get(cellName: string) {
    return this.cellModified.get(cellName);
  }

  clear(): void {
    this.cellModified.clear();
  }

  delete(cellName: string) {
    this.cellModified.delete(cellName);
  }

  values() {
    return this.cellModified;
  }

  getCellDetails(): CellDetails[] {
    return [...this.values().entries()].map((item) => ({
      lineNumber: this.slitLineNumber(item[0]).toString(),
      data: { ...item[1], fieldOrder: Number(item[1].fieldOrder) },
    })) as CellDetails[];
  }

  slitLineNumber(cellChange: string) {
    return Number(cellChange.split('_')[0]);
  }

  generateCellName(lineNumber: number, column: number): string {
    return lineNumber + '_' + column;
  }

  isModified(lineNumber: number) {
    return this.getCellDetails().some(
      (cell) => Number(cell.lineNumber) === lineNumber
    );
  }

  has(cellName: string) {
    return this.cellModified.has(cellName);
  }

  disableChecked(): boolean {
    return this.cellModified.size > 0;
  }

  getLineNumberAndCellChange(
    selectedItem: DirectoryFeedValue[]
  ): UpdatedDirectoryFeed[] {
    const updated = this.getCellDetails();

    const merged = updated.reduce((acc: { [key: string]: any }, cur) => {
      const lineNumber = cur.lineNumber;
      const dataItem = cur.data;

      if (!acc[lineNumber]) {
        acc[lineNumber] = [];
      }
      acc[lineNumber].push(dataItem);
      return acc;
    }, {});

    return Object.entries(merged).map(([lineNumber, data]) => ({
      lineNumber: Number(lineNumber),
      data: this.fineLineValue(
        Number(lineNumber),
        data.map((item: CellObject) => ({
          id: item.id,
          fieldId: item.directoryFieldId,
          value: item.value,
        })) as FieldDetail[],
        selectedItem
      ),
    }));
  }

  fineLineValue(
    lineNumber: number,
    cellChanged: FieldDetail[],
    selectedItem: DirectoryFeedValue[]
  ) {
    const fieldIdChanged = cellChanged.map((cell) => cell.fieldId);
    const mapped = selectedItem
      .filter(
        (item: DirectoryFeedValue) => Number(item.lineNumber) === lineNumber
      )
      .map((item: DirectoryFeedValue) => item.values)[0]
      .map((cell) => ({
        id: cell.id,
        fieldId: cell.directoryFieldId,
        value: cell.value,
      }))
      .filter(
        (cell) => !fieldIdChanged.includes(cell.fieldId)
      ) as FieldDetail[];
    mapped.push(...cellChanged);
    return mapped;
  }

  isLineSelected(lineNumber: number) {
    return [...this.cellModified.keys()]
      .map((key) => this.getCellIndex(key))
      .some((item) => Number(item) === lineNumber);
  }

  private getCellIndex(cellName: string) {
    return cellName.split('_')[1];
  }
}
