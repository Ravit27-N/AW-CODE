
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject, OnInit } from '@angular/core';



@Component({
  templateUrl: './dialogview.component.html',
  styleUrls: ['./dialogview.component.css']
})
export class DialogviewComponent implements OnInit {
  status: string;
  constructor(public dialogRef: MatDialogRef<DialogviewComponent>, @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
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
