import { MessageService } from './../../core/service/message.service';
import { ReminderService } from './../../core/service/reminder.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, Inject } from '@angular/core';

export interface DialogData {
  id: number;
  title: string;
}
@Component({
  selector: 'app-delete-reminder',
  templateUrl: './delete-reminder.component.html',
  styleUrls: ['./delete-reminder.component.css']
})
export class DeleteReminderComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public reminder: DialogData,
    private reminderService: ReminderService,
    private message: MessageService
  ) { }
}
