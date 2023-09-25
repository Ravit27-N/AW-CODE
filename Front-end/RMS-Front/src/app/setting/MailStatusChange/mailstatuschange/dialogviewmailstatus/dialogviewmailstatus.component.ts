import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-dialogviewmailstatus',
  templateUrl: './dialogviewmailstatus.component.html',
  styleUrls: ['./dialogviewmailstatus.component.scss']
})
export class DialogviewmailstatusComponent {
  status: string;
  constructor(public dialogRef: MatDialogRef<DialogviewmailstatusComponent>, @Inject(MAT_DIALOG_DATA) public data) {
    if (this.data.active) {
      this.status = 'Active';
    }
    else {
      this.status = 'Inactive';
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
