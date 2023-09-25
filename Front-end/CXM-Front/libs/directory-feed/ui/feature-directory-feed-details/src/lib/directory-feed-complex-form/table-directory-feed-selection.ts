import { TableSelection } from '@cxm-smartflow/shared/common-typo';
import { DirectoryFeedValue } from '@cxm-smartflow/directory-feed/data-access';

export abstract class TableDirectoryFeedSelection extends TableSelection {
  isSelected(data: DirectoryFeedValue) {

    const mapped = data.values.map((cell) => {
      const value = cell.value;
      if (typeof value === 'boolean') {
        return { ...cell, value: cell.value ? 'TRUE' : 'FALSE' };
      }
      return cell;
    });
    const valueChange = { ...data, values: mapped };
    return this.selection.selected.some((item: DirectoryFeedValue) => {
      return JSON.stringify(item) === JSON.stringify(valueChange);
    });
  }

  isAllSelected(): boolean {
    const datasourceLine = this.ignoreSelection(
      this.getDatasource().data
    ).map((item) => Number(item.lineNumber));
    const selectedLine = this.selection.selected.map((cell) =>
      Number(cell.lineNumber)
    );
    return (
      this.getDatasource().data.length > 0 &&
      selectedLine.filter((line) => datasourceLine.includes(line)).length ===
        this.getDatasource().data.length
    );
  }

  masterToggle() {
    if (this.isAllSelected()) {
      const datasourceLine = this.ignoreSelection(
        this.getDatasource().data
      ).map((item) => Number(item.lineNumber));
      const keepSelected = this.selection.selected.filter((selected) =>
        datasourceLine.includes(selected.lineNumber)
      );
      this.selection.deselect(...keepSelected);
      return;
    }

    const ds = this.getDatasource();
    const data = this.ignoreSelection(ds.data);
    this.selection.select(...data);
  }

  singleToggle(row: any) {
    const existLines = this.selection.selected.filter(
      (item) => item.lineNumber === row?.lineNumber
    );
    if (existLines.length > 0) {
      this.selection.deselect(...existLines);
    } else {
      this.selection.toggle(row);
    }
  }

  toBoolean(value: any): boolean {
    if (typeof value === 'boolean') {
      return value;
    }
    return value?.toLowerCase() === 'true';
  }

  disableChecked(): boolean {
    return this.getDatasource().data.length === 0;
  }
}
