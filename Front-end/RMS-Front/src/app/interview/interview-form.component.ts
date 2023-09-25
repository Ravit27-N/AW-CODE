import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { IsLoadingService } from '@service-work/is-loading';
import { MatSelectSearchComponent } from 'ngx-mat-select-search';
import { ReplaySubject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  startWith,
  switchMap,
} from 'rxjs/operators';
import {
  CandidateModel,
  CandidateService,
  Interview,
  InterviewFormModel,
  InterviewService,
  JobDescriptionService,
  InterviewTemplateModel,
  InterviewTemplateService,
} from '../core';
import { JobModel } from '../core';
import { MessageService } from '../core';
import { AddJobComponent } from '../setting/job';
import { formatDate24H } from '../shared';

@Component({
  selector: 'app-interview-form',
  templateUrl: './interview-form.component.html',
})
export class InterviewFormComponent implements OnInit {
  @Input() interview?: Interview;
  @Input() editorMode?: boolean;
  @Input() preload?: any = null;

  @Output() oncancel = new EventEmitter();
  @Output() onupdateSuccess = new EventEmitter();

  model: InterviewFormModel;
  validateForm: FormGroup;

  candidates: CandidateModel[];
  candiatesSubject$ = new ReplaySubject<CandidateModel[]>(1);
  interviewStatus: InterviewTemplateModel[];

  titleDescriptions: string[];

  standaloneOption = { standalone: true };

  candidateFilterCtrl = new FormControl();
  titleDescriptionCtrl = new FormControl();

  @ViewChild(MatSelectSearchComponent) matSelection: MatSelectSearchComponent;

  constructor(
    private candidateService: CandidateService,
    private interviewService: InterviewService,
    private statusInterviewService: InterviewTemplateService,
    private messageService: MessageService,
    private jobDescriptionService: JobDescriptionService,
    private isloadingService: IsLoadingService,
    public dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    if (this.preload && this.preload.editorMode) {
      this.editorMode = this.preload.editorMode;
    }

    if (this.preload && this.preload.interview) {
      this.interview = this.preload.interview;
    }

    this.titleDescriptionCtrl.valueChanges
      .pipe(startWith(''), debounceTime(300))
      .pipe(
        distinctUntilChanged(),
        switchMap((search: string) =>
          this.jobDescriptionService.get(1, 10, search),
        ),
      )
      .subscribe(
        (data) =>
          (this.titleDescriptions = data.contents
            .filter((x) => x.active)
            .map((x) => x.title)),
      );

    if (!this.editorMode) {
      this.model = {
        candidateId: null,
        title: '',
        description: '',
        reminderTime: 10,
        sendInvite: false,
        setReminder: false,
        dateTime: new Date(),
      };
    } else if (this.interview) {
      this.model = {
        candidateId: this.interview.candidate.id.toString(),
        dateTime: new Date(this.interview.dateTime),
        description: this.interview.description,
        title: this.interview.title,
        statusId: this.interview.statusId,
        status: this.interview.status,
      };
    }

    this.candidateFilterCtrl.valueChanges
      .pipe(debounceTime(300))
      .pipe(distinctUntilChanged())
      .subscribe(() => {
        this.fetchCandidate(1, 15);
      });

    // TODO: Consider use cached strategy
    if (this.preload && this.preload.candidate) {
      this.model.candidateId = this.preload.candidate.id.toString();
      this.candiatesSubject$.next([this.preload.candidate]);
    } else {
      this.fetchCandidate(1, 15);
    }
    this.statusInterviewService
      .getList(null, null, 'name', 'asc')
      .subscribe(
        (data) =>
          (this.interviewStatus = data.contents.filter((x) => x.active)),
      );

    this.validateForm = new FormGroup({
      candidate: new FormControl(
        {
          value: this.model.candidateId,
          disabled: this.editorMode || this.preload.candidate,
        },
        [Validators.required],
      ),
      datetime: new FormControl({ value: this.model.dateTime }, [
        Validators.required,
      ]),
      title: new FormControl(this.model.title, [Validators.required]),
      statusId: new FormControl(this.model.statusId, [Validators.required]),
    });
  }

  save(): void {
    if (this.validateForm.invalid) {
      return;
    }

    if (this.editorMode) {
      this.model.statusId = this.interviewStatus.find(
        (x) => x.name === this.model.status,
      ).id;
      const subscription = this.interviewService
        .update(this.interview, {
          ...this.model,
          dateTime: formatDate24H(this.model.dateTime),
        })
        .subscribe(() => this.onupdateSuccess.emit());
      this.isloadingService.add(subscription, {
        key: 'InterviewFormComponent',
        unique: 'InterviewFormComponent',
      });
    } else {
      this.model.statusId = this.interviewStatus.find(
        (x) => x.name === this.model.status,
      ).id;
      const subscription = this.interviewService
        .create({
          ...this.model,
          dateTime: formatDate24H(this.model.dateTime),
        })
        .subscribe(() => {
          this.onupdateSuccess.emit();
          this.messageService.showSuccess('Success', 'Create candidate');
        });
      this.isloadingService.add(subscription, {
        key: 'InterviewFormComponent',
        unique: 'InterviewFormComponent',
      });
    }
  }

  cancel(): void {
    if (this.oncancel) {
      this.oncancel.emit();
    }
  }

  createTitleDescription(): void {
    // Popup add job description
    this.dialog
      .open(AddJobComponent, {
        width: '40%',
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result: JobModel) => {
        if (result.active) {
          this.titleDescriptions.unshift(result.title);
          this.validateForm.controls.title.setValue(result.title);
        }
      });
  }

  private fetchCandidate(page, size: number): void {
    if (this.candidateFilterCtrl.value) {
      // this.candidateService.getList(page, size, this.candidateFilterCtrl.value, 'asc', 'candidateFullName')
      //   .subscribe(data => this.candiatesSubject$.next(data.contents));
      this.candidateService
        .advanceSearch(
          {
            name: this.candidateFilterCtrl.value,
            sortDirection: 'asc',
            sortByField: 'candidateFullName',
          },
          size,
          page,
        )
        .subscribe((data) => this.candiatesSubject$.next(data.contents));
    } else {
      this.candidateService
        .getList(page, size)
        .subscribe((data) => this.candiatesSubject$.next(data.contents));
    }
  }
}
