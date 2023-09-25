import {G_USER_ICON, USER_ICON} from '../../core';
import { ActivityService } from '../../core';
import { ActivityModel, CandidateService } from 'src/app/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';

export interface DialogData {
  id?: number;
}

@Component({
  selector: 'app-activity-detail',
  templateUrl: './activity-detail.component.html',
  styleUrls: ['./activity-detail.component.css']
})
export class ActivityDetailComponent implements OnInit {
  activity: ActivityModel;
  data: any;
  bIcon = USER_ICON;
  gIcon = G_USER_ICON;
  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: DialogData,
    private activityService: ActivityService,
    private candidateService: CandidateService,
  ) {
    this.activity = {
      id: 0,
      title: '',
      description: '',
      createdAt: new Date(),
      updatedAt: new Date(),
      candidate: { id: 0, fullname: '', status: { id: null, title: '', active: null } },
      author: '',
      links: {
        getActivity: { rel: '', href: '' },
        update: { rel: '', href: '' },
        delete: { rel: '', href: '' }
      }
    };
  }

  ngOnInit(): void {
    this.getActivity();
  }

  getActivity(): void {
    this.activityService.getById(this.dialogData.id).subscribe((respone) => {
      this.activity = respone;
      this.candidateService.getById(this.activity?.candidate?.id).subscribe((data) => {
        this.data = data;
      });
    });
  }

  getUrl(id: number, photoUrl: any): any {
    return `/candidate/${id}/view/${photoUrl}`;
  }


}
