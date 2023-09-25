import {VALIDATION_MESSAGE} from '../../core/model/validationMessage';
import { Location } from '@angular/common';
import { ActivityService } from './../../core/service/activity.service';
import { StatusCandidateService } from './../../core/service/status-candidate.service';
import { CandidateService } from './../../core/service/candidate.service';
import { MessageService } from './../../core/service/message.service';
import { StatusCandidateModel } from 'src/app/core';
import { CandidateModel } from './../../core/model/candidate';
import { ActivityFormModel } from './../../core/model/Activity';
import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { IsLoadingService } from '@service-work/is-loading';
@Component({
  selector: 'app-add-activity',
  templateUrl: './add-activity.component.html',
  styleUrls: ['./add-activity.component.css']
})
export class AddActivityComponent implements OnInit {
  activity: ActivityFormModel;
  candidates: CandidateModel[];
  statuses: StatusCandidateModel[];
  pageIndex = 1;
  pageSize = 100;
  filterValue: string;
  sortDirection = 'asc';
  sortByField = 'firstname';
  candidateStatus: CandidateModel;
  disMatSelect = false;
  candidateTitle = 'Select Candidate';
  validationMessage = VALIDATION_MESSAGE;
  constructor(
    private message: MessageService,
    private candidateService: CandidateService,
    private statusService: StatusCandidateService,
    private activityService: ActivityService,
    private pageHistory: Location,
    private activateRoute: ActivatedRoute,
    private isloadingService: IsLoadingService
  ) {

    this.activity = {
      candidateId: null,
      statusId: null,
      title: null,
      description: null
    };

    this.candidateStatus = {
      id: null,
      firstname: null,
      lastname: null,
      salutation: null,
      gender: null,
      email: null,
      telephone: null,
      fullName: null,
      gpa: null,
      priority: null,
      candidateStatus: {
        id: null,
        title: null
      }
    };
  }

  ngOnInit(): void {
    this.loadActivity();
  }

  loadActivity(): void {
    this.loadCandidate();
    this.loadStatus();
    const candidateId = this.activateRoute.snapshot.params.id;
    if (candidateId === undefined) {
      this.disMatSelect = false;
      this.candidateTitle = 'Select Candidate';
    } else {
      this.disMatSelect = true;
      this.getCandidateById(candidateId);
    }
  }

  getCandidateById(id: number): void {
    this.candidateService.getCandidateDetailById(id).subscribe((respone) => {
      this.candidateTitle = respone.fullName;
      this.candidateStatus.candidateStatus.title = respone.candidateStatus.title;
      this.activity.candidateId = id;
      this.activity.status = respone.candidateStatus.title;
    });
  }

  applyFilter(): void {
    if (this.filterValue === '' || this.filterValue === null || this.filterValue === undefined) {
      this.loadCandidate();
    } else if (this.filterValue.length >= 3) {
      this.candidateService
      .getList(this.pageIndex, this.pageSize, this.filterValue, this.sortDirection, this.sortByField, undefined, undefined, false)
        .subscribe((respone) => {
          this.candidates = respone.contents;
        });
    }

  }

  loadCandidate(): void {
    this.candidateService.getList(1, 100, '', 'desc', 'createdAt', undefined, undefined, false)
      .subscribe((respone) => {
        this.candidates = respone.contents;
      });
  }

  loadStatus(): void {
    this.statusService.getList(1, 100, '', 'title', 'asc')
      .subscribe((respone) => {
        this.statuses = respone.contents.filter(x => x.active && !x.deleted);
      });
  }

  clearTitle(): void {
    this.activity.title = null;
  }

  clearSelectCandidate(): void {
    this.activity.candidateId = null;
  }

  clearSelectStatus(): void {
    this.activity.status = null;
  }

  onSelectStatusCandidate(title: any): void {
    if (title === this.candidateStatus.candidateStatus.title) {
      this.activity.title = null;
    } else {
      this.activity.title = this.candidateStatus.candidateStatus.title + ' -> ' + title;
    }
  }

  onSubmit(): void {
    const subscription = this.activityService.create(this.activity).subscribe(() => {
      this.message.showSuccess('Add new activity log successfully', 'Add');
      this.back();
    });
    this.isloadingService.add(subscription, { key: 'AddActivityComponent', unique: 'AddActivityComponent' });
  }

  back(): void {
    this.pageHistory.back();
  }

  onSelectCandidateChange(id: number): void {
    this.candidateService.getCandidateDetailById(id).subscribe((respone) => {
      this.candidateStatus = respone;
      this.activity.title = null;
      this.activity.status = this.candidateStatus.candidateStatus.title;
    });
  }

}
