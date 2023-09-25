import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";


interface ISelectionDisabled {
  ignoreSelection(data: any[]): any[];
}


export abstract class TableSelection implements ISelectionDisabled {


  selection = new SelectionModel<any>(true, [], true);

  isAllSelected(): boolean {
    const numSelected = this.selection.selected.length;
    const numRows = this.ignoreSelection(this.getDatasource().data).length;
    return numSelected === numRows && numSelected > 0;
  }

  masterToggle() {
    if(this.isAllSelected()) {
        this.selection.clear();
        return;
    }

    const ds = this.getDatasource();
    const data = this.ignoreSelection(ds.data)
    this.selection.select(...data);
  }

  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`;
  }

  abstract getDatasource(): MatTableDataSource<any>;


  ignoreSelection(data: any[]): any[] {
    return data;
  }

  isNoSelectable() {
    const ds = this.getDatasource();
    const data = this.ignoreSelection(ds.data);

    return data.length === 0;
  }

}
