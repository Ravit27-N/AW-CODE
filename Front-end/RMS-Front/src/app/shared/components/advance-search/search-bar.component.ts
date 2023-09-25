import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AdvanceSearchDialogComponent } from './advance-search-dialog.component';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent {

  constructor(private dialog: MatDialog) { }

  activate(): void {
      this.dialog.open(AdvanceSearchDialogComponent, {
      data: { },
      panelClass: 'advance-dialog',
      backdropClass: 'advance-dialog-backdrop',
      width: '95vw', hasBackdrop: true,
      height: 'calc(100vh - 40px)',
      maxWidth: '100vw'
    });
  }

}
