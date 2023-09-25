import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';
import { JobService } from '../../../../core/service/job.service';
@Component({
  selector: 'app-dialog-view-job',
  templateUrl: './dialog-view-job.component.html',
  styleUrls: ['./dialog-view-job.component.css']
})
export class DialogViewJobComponent implements OnInit {
  status: string = null;
  constructor(public dialogRef: MatDialogRef<DialogViewJobComponent>, @Inject(MAT_DIALOG_DATA) public data, private service: JobService) {
  }

  ngOnInit(): void {
    if (this.data.active === true) {
      this.status = 'Active';
    }
    else {
      this.status = 'Inactive';
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }


  getFile(row) {
    this.service.fileView(row.id, row.filename).subscribe(res => {
      const blob = new Blob([res], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url);
    }, () => {
    });
  }
  htmlToPlaintext(text: string): any {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }
}
