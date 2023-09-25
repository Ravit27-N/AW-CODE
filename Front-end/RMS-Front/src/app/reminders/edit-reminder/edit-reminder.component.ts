import { CandidateService } from './../../core/service/candidate.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReminderService } from './../../core/service/reminder.service';
import { ReminderFormModel, ReminderModel } from './../../core/model/Reminder';
import { Location } from '@angular/common';
import { Component, OnInit, Inject } from '@angular/core';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { CandidateModel, Interview, InterviewService } from 'src/app/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { MessageService } from 'src/app/core/service/message.service';
import { IsLoadingService } from '@service-work/is-loading';
import { formatDate, formatDate24H } from '../../shared';
export interface EditDialogData {
  id?: number;
}
@Component({
  selector: 'app-edit-reminder',
  templateUrl: './edit-reminder.component.html',
  styleUrls: ['./edit-reminder.component.css'],
})
export class EditReminderComponent implements OnInit {
  reminderForm: ReminderFormModel;
  reminderModel: ReminderModel;
  statusTitle = 'active';
  statusToggle = true;
  isAbleSelectCandidate = false;
  selectCandidateTitle = 'Candidate';
  isAbleSelectInterview = false;
  selectInterviewTitle = 'Interview';
  filterCandidateValue = '';
  filterInterviewValue = '';
  candidates: CandidateModel[];
  interviews: Interview[];
  radioGroup = 'NORMAL';
  isDateTimeSelected = false;
  dateReminderBackUp: any;
  constructor(
    private pageHistory: Location,
    private reminderService: ReminderService,
    private candidateService: CandidateService,
    private interviewService: InterviewService,
    private message: MessageService,
    @Inject(MAT_DIALOG_DATA) public dialogData: EditDialogData,
    private isloadingService: IsLoadingService,
  ) {
    this.reminderForm = {
      userId: null,
      candidateId: null,
      interviewId: null,
      reminderType: null,
      title: null,
      description: null,
      dateReminder: null,
      active: null,
    };

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
    this.reminderService
      .getReminderById(this.dialogData.id)
      .subscribe((respone) => {
        this.reminderModel = respone;
        this.checkIsAbleSelectedCandidate(
          this.reminderModel.reminderType,
          this.reminderModel?.candidate?.fullName,
          this.reminderModel?.interview?.title,
        );
        this.setDataToReminderForm(this.reminderModel);
      });
  }

  setDataToReminderForm(data: any): void {
    this.reminderForm.id = data?.id;
    this.reminderForm.candidateId = data?.candidate?.id;
    this.reminderForm.interviewId = data?.interview?.id;
    this.reminderForm.reminderType = data?.reminderType;
    this.reminderForm.title = data?.title;
    this.reminderForm.dateReminder = formatDate(data?.dateReminder);
    this.dateReminderBackUp = formatDate24H(data?.dateReminder);
    this.reminderForm.description = data?.description;
    this.reminderForm.active = data?.active;
  }

  checkIsAbleSelectedCandidate(
    type?: string,
    candidateName?: string,
    interviewTitle?: string,
  ): void {
    switch (type) {
      case 'NORMAL':
        this.radioGroup = type;
        this.loadInterview();
        this.loadCandidate();
        break;
      case 'SPECIAL':
        this.radioGroup = type;
        this.isAbleSelectCandidate = true;
        this.selectCandidateTitle = candidateName;
        this.loadInterview();
        break;
      case 'INTERVIEW':
        this.radioGroup = type;
        this.isAbleSelectInterview = true;
        this.selectInterviewTitle = interviewTitle;
        this.loadCandidate();
        break;
      default:
        this.radioGroup = 'NORMAL';
        this.loadInterview();
        this.loadCandidate();
        break;
    }
  }

  onFormatDate(): void {
    this.isDateTimeSelected = true;
  }

  onSubmit(): void {
    if (this.isDateTimeSelected) {
      this.reminderForm.dateReminder = formatDate24H(
        this.reminderForm.dateReminder,
      );
    } else {
      this.reminderForm.dateReminder = this.dateReminderBackUp;
    }
    this.reminderForm.reminderType = this.radioGroup;
    const subscription = this.reminderService
      .updateReminder(this.reminderForm.id, this.reminderForm)
      .subscribe(() => {
        this.message.showSuccess('Update reminder successfully!', 'Update');
      });
    this.isloadingService.add(subscription, {
      key: 'EditReminderComponent',
      unique: 'EditReminderComponent',
    });
  }

  tabChanged(tabChangeEvent?: MatTabChangeEvent): void {
    this.reminderForm.reminderType = tabChangeEvent.tab.textLabel.toUpperCase();
  }

  filterCandidate(): void {
    if (
      this.filterCandidateValue === undefined ||
      this.filterCandidateValue === '' ||
      this.filterCandidateValue === null
    ) {
      this.loadCandidate();
    } else if (this.filterCandidateValue.length >= 3) {
      this.candidateService
        .getList(
          1,
          100,
          this.filterCandidateValue,
          'desc',
          'createdAt',
          undefined,
          undefined,
          false,
        )
        .subscribe((respone) => {
          this.candidates = respone.contents;
        });
    }
  }

  loadCandidate(): void {
    this.candidateService
      .getList(1, 100, '', 'desc', 'createdAt', undefined, undefined, false)
      .subscribe((respone) => {
        this.candidates = respone.contents;
      });
  }

  filterInterview(): void {
    if (
      this.filterInterviewValue === undefined ||
      this.filterInterviewValue === '' ||
      this.filterInterviewValue === null
    ) {
      this.loadInterview();
    } else if (this.filterInterviewValue.length >= 3) {
      this.interviewService
        .getInterviewList(1, 100, this.filterInterviewValue)
        .subscribe((respone) => {
          this.interviews = respone.contents;
        });
    }
  }

  loadInterview(): void {
    this.interviewService.getInterviewList(1, 100).subscribe((respone) => {
      this.interviews = respone.contents;
    });
  }

  checkStatus(event: MatSlideToggleChange): void {
    if (event.checked === false) {
      this.statusTitle = 'Inactive';
      this.reminderForm.status = 'Inactive';
    } else {
      this.statusTitle = 'Active';
      this.reminderForm.status = 'Active';
    }
  }

  back(): void {
    this.pageHistory.back();
  }

  clearTitle(): void {
    this.reminderForm.title = null;
  }
}
