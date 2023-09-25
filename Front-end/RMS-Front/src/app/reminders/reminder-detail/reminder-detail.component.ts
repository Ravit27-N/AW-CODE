import { ReminderService } from '../../core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReminderModel } from '../../core';
import { Component, OnInit, Inject } from '@angular/core';
export interface DetailDialogData {
  id?: number;
}
@Component({
  selector: 'app-reminder-detail',
  templateUrl: './reminder-detail.component.html',
  styleUrls: ['./reminder-detail.component.css']
})
export class ReminderDetailComponent implements OnInit {
  reminderModel: ReminderModel ;
  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: DetailDialogData,
    private reminderService: ReminderService,
  ) {
    this.reminderModel = {
      id: null,
      candidate: { id: null, fullName: null },
      reminderType: null,
      title: null,
      description: null,
      dateReminder: null,
      createdAt: null,
      updatedAt: null,
      active: null,
      status: null,
    };
  }

  ngOnInit(): void {
     this.loadData();
  }

  loadData(): void {
    this.reminderService.getReminderById(this.dialogData.id).subscribe((respone) => {
      this.reminderModel = respone;
    });
  }

  htmlToPlaintext(text: string): any {
    return text ? String(text).replace(/<[^>]+>/gm, '') : '';
  }

}
