import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MessageService} from '../../../core/service/message.service';
import {ProjectService} from '../../../core/service/project.service';

export interface ProjectDialogData {
  id: number;
  projectName: string;
  isDelete: boolean;
  type?: string;
}

@Component({
  selector: 'app-archive-project-dialog',
  templateUrl: './archive-project-dialog.component.html',
  styleUrls: ['./archive-project-dialog.component.css']
})
export class ArchiveProjectDialogComponent implements OnInit {
  title: string;
  content: string;

  constructor(
    private dialogRef: MatDialogRef<ArchiveProjectDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: ProjectDialogData,
    private message: MessageService,
    private projectService: ProjectService
  ) {
  }

  ngOnInit(): void {
    this.setTitleAndContent();
  }

  setTitleAndContent(): void {
    if (this.data.type === 'restore') {
      this.title = 'Restore project';
      this.content = 'Are you sure to restore this project? If Okay, this will also restore records' +
        ' (Demand, All Candidate in Demand) that are related to this project.';
    } else {
      this.title = 'Permanently delete project';
      this.content = 'Are you sure to permanently delete this project? If Okay, this will also permanently delete records' +
        ' (Demand, All Candidate in Demand) that are related to this project.';
    }
  }

  ok(): void {
    if (this.data.type === 'restore') {
      this.projectService.restore(this.data?.id, false).subscribe(() => {
        this.message.showSuccess('Restore project successfully ', 'Restore Project');
        this.dialogRef.close(true);
      });
    } else if (this.data.type === 'permanentlyDelete') {
      this.projectService.hardDelete(this.data?.id).subscribe(() => {
        this.message.showSuccess('Success','Permanently remove project');
        this.dialogRef.close(true);
      },() => {
          this.message.showError('Can not delete because have in demand', 'Delete Project');
          this.dialogRef.close(true);
      });
    }
  }

}
