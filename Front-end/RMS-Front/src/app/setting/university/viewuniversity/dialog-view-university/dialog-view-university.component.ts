import { UniversityModel } from 'src/app/core/model/university';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';

@Component({
  selector: 'app-dialog-view-university',
  templateUrl: './dialog-view-university.component.html',
  styleUrls: ['./dialog-view-university.component.css']
})
export class DialogViewUniversityComponent {

  constructor(
    public dialogRef: MatDialogRef<DialogViewUniversityComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: UniversityModel) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
