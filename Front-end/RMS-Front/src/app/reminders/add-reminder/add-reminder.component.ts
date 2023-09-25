import { MessageService } from '../../core';
import { ReminderService } from '../../core';
import { ActivatedRoute } from '@angular/router';
import { Interview } from '../../core';
import { InterviewService } from '../../core';
import { CandidateModel } from '../../core';
import { CandidateService } from '../../core';
import { Location } from '@angular/common';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { ReminderFormModel, ReminderModel } from '../../core';
import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { IsLoadingService } from '@service-work/is-loading';
import { formatDate24H } from '../../shared';
@Component({
  selector: 'app-add-reminder',
  templateUrl: './add-reminder.component.html',
  styleUrls: ['./add-reminder.component.css'],
})
export class AddReminderComponent implements OnInit {
  reminder: ReminderFormModel;
  reminderModel: ReminderModel;
  statusTitle = 'active';
  statusToggle = true;
  candidates: CandidateModel[];
  interviews: Interview[];
  filterCandidateValue = '';
  filterInterviewValue = '';
  isAbleSelectCandidate = false;
  selectCandidateTitle = 'Candidate';
  isAbleSelectInterview = false;
  selectInterviewTitle = 'Interview';
  radioGroup = 'NORMAL';
  constructor(
    private pageHistory: Location,
    private candidateService: CandidateService,
    private interviewService: InterviewService,
    private activateRoute: ActivatedRoute,
    private reminderService: ReminderService,
    private message: MessageService,
    private isloadingService: IsLoadingService,
  ) {
    this.reminder = {
      userId: null,
      candidateId: null,
      interviewId: null,
      reminderType: 'NORMAL',
      title: null,
      description: null,
      dateReminder: null,
      active: true,
    };
  }

  ngOnInit(): void {
    this.loadReminder();
  }

  loadReminder(): void {
    const id = this.activateRoute.snapshot.params.id;
    const type = this.activateRoute.snapshot.params.type;
    switch (type) {
      case 'SPECIAL':
        this.radioGroup = 'SPECIAL';
        if (id === undefined) {
          this.isAbleSelectCandidate = false;
          this.selectCandidateTitle = 'Candidate';
          this.loadCandidate();
        } else {
          this.isAbleSelectCandidate = true;
          this.getCandidateById(id);
          this.loadInterview();
        }
        break;
      case 'INTERVIEW':
        this.radioGroup = 'INTERVIEW';
        if (id === undefined) {
          this.isAbleSelectInterview = false;
          this.selectInterviewTitle = 'Interview';
          this.loadCandidate();
        } else {
          this.isAbleSelectInterview = true;
          this.getInterviewById(id);
          this.loadCandidate();
        }
        break;
      default:
        this.radioGroup = 'NORMAL';
        this.loadCandidate();
        this.loadInterview();
        break;
    }
  }

  getCandidateById(id: number): void {
    this.candidateService.getCandidateDetailById(id).subscribe((respone) => {
      this.selectCandidateTitle = respone.fullName;
      this.reminder.candidateId = respone.id;
    });
  }

  loadCandidate(): void {
    this.candidateService
      .getList(1, 100, '', 'desc', 'createdAt', undefined, undefined, false)
      .subscribe((respone) => {
        this.candidates = respone.contents;
      });
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

  getInterviewById(id: number): void {
    this.interviewService.getById(id).subscribe((respone) => {
      this.selectInterviewTitle =
        respone.title + ' | ' + respone.candidate.fullName;
      this.reminder.interviewId = respone.id;
    });
  }

  loadInterview(): void {
    this.interviewService.getInterviewList(1, 100).subscribe((respone) => {
      this.interviews = respone.contents;
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

  clearTitle(): void {
    this.reminder.title = null;
  }

  onSubmit(): void {
    this.reminder.dateReminder = formatDate24H(this.reminder.dateReminder);
    this.reminder.reminderType = this.radioGroup.toUpperCase();
    const subscription = this.reminderService
      .create(this.reminder)
      .subscribe(() => {
        this.message.showSuccess('Successfully', 'Add');
        this.back();
      });
    this.isloadingService.add(subscription, {
      key: 'AddReminderComponent',
      unique: 'AddReminderComponent',
    });
  }

  checkStatus(event: MatSlideToggleChange): void {
    if (event.checked === false) {
      this.statusTitle = 'Inactive';
      this.reminder.status = 'Inactive';
      this.reminder.active = false;
    } else {
      this.statusTitle = 'Active';
      this.reminder.status = 'Active';
      this.reminder.active = true;
    }
  }

  back(): void {
    this.pageHistory.back();
  }

  clearCandidate(): void {
    this.reminder.candidateId = null;
  }

  clearInterview(): void {
    this.reminder.interviewId = null;
  }

  tabChanged(tabChangeEvent?: MatTabChangeEvent): void {
    this.reminder.reminderType = tabChangeEvent.tab.textLabel.toUpperCase();
  }
}
