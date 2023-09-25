import {
  ActivityService,
  MessageService,
  StatusCandidateService,
  CandidateModel,
  ActivityFormModel,
  ActivityModel,
} from '../../core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';
import { StatusCandidateModel } from 'src/app/core/model/statuscandidate';
import { IsLoadingService } from '@service-work/is-loading';
import { AwSnackbarService } from '../../shared';

export interface EditActivityDialogData {
  id?: number;
}
@Component({
  selector: 'app-edit-activity',
  templateUrl: './edit-activity.component.html',
  styleUrls: ['./edit-activity.component.css'],
})
export class EditActivityComponent implements OnInit {
  activity: ActivityFormModel;
  activityModel: ActivityModel;
  candidates: CandidateModel[];
  statuses: StatusCandidateModel[] = [];
  staticCandidateStatus: string;
  filterValue: string;
  pageIndex = 1;
  pageSize = 100;
  sortDirection = 'asc';
  sortByField = 'firstname';
  constructor(
    private message: MessageService,
    private dialogRef: MatDialogRef<EditActivityComponent>,
    @Inject(MAT_DIALOG_DATA) public dialogData: EditActivityDialogData,
    private statusService: StatusCandidateService,
    private activityService: ActivityService,
    private isLoadingService: IsLoadingService,
    private awSnackbarService: AwSnackbarService,
  ) {
    this.activity = {
      candidateId: 0,
      statusId: 0,
      title: '',
      description: '',
    };

    this.activityModel = {
      id: 0,
      title: '',
      description: '',
      candidate: {
        id: 0,
        fullname: '',
        status: { id: null, title: '', active: null },
      },
      author: '',
    };
  }

  ngOnInit(): void {
    this.loadStatus();
    this.getActivity();
  }

  getActivity(): void {
    this.activityService.getById(this.dialogData.id).subscribe((respone) => {
      this.activityModel = respone;
      this.activity.id = this.activityModel.id;
      this.activity.candidateId = this.activityModel?.candidate?.id;
      this.activity.status = this.activityModel?.candidate?.status?.title;
      this.staticCandidateStatus = this.activityModel?.candidate?.status?.title;
      this.activity.title = this.activityModel.title;
      this.activity.active = this.activityModel?.candidate?.status?.active;
      this.activity.description = this.activityModel.description;
    });
  }

  loadStatus(): void {
    this.statusService
      .getList(1, 100, '', 'title', 'asc')
      .subscribe((respone) => {
        this.statuses = respone.contents.filter((x) => x.active && !x.deleted);
      });
  }

  clearTitle(): void {
    this.activity.title = null;
  }

  clearSelectStatus(): void {
    this.activity.status = null;
  }

  onSubmit(): void {
    const subscription = this.activityService
      .update(this.activity, this.activity.id)
      .subscribe(
        () => {
          this.showSuccessMessage('Update activity log successfully');
          this.dialogRef.close();
        },
        (error) => {
          this.alertSnackbarMessage(
            `Something went wrong. Cannot communicate with the server ${error}.`,
          );
        },
      );
    this.isLoadingService.add(subscription, {
      key: 'EditActivityComponent',
      unique: 'EditActivityComponent',
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  onSelectStatusCandidate(title: any): void {
    if (title === this.staticCandidateStatus) {
      this.activity.title = null;
    } else {
      this.activity.title = this.staticCandidateStatus + ' -> ' + title;
    }
  }
}
