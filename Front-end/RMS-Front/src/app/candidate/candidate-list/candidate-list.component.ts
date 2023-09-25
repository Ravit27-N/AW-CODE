import { ArchiveDialogComponent } from './../archive-dialog/archive-dialog.component';
import { USER_ICON, G_USER_ICON } from '../../core/model/icon';
import { MessageService } from './../../core/service/message.service';
import { StatusCandidateService } from './../../core/service/status-candidate.service';
import {
  InterviewService,
  PAGINATION_SIZE,
  StatusCandidateModel,
} from 'src/app/core';
import { CandidateService } from './../../core/service/candidate.service';
import { CandidateModel } from './../../core/model/candidate';
import { DeleteCandidateComponent } from './../delete-candidate/delete-candidate.component';
import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router, ActivatedRoute } from '@angular/router';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { ComfirmDialogComponent } from '../comfirm-dialog/comfirm-dialog.component';
import { InterviewResultDialogComponent } from 'src/app/interview/dialog.component';
import { IsLoadingService } from '@service-work/is-loading';
import { catchError, filter } from 'rxjs/operators';
import { Subscription, throwError } from 'rxjs';
import { MatSelectChange } from '@angular/material/select';
import { getAge } from '../../shared';

@Component({
  selector: 'app-candidate-list',
  templateUrl: './candidate-list.component.html',
  styleUrls: ['./candidate-list.component.css'],
})
export class CandidateListComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = [
    '#',
    'Photo',
    'Full name',
    'age',
    'Phone',
    'From',
    'GPA',
    'yearOfExperience',
    'Priority',
    'Status',
    'Last Interview',
    'Created at',
    'Action',
  ];
  dataSource: MatTableDataSource<CandidateModel>;
  candidates: CandidateModel[];
  bIcon = USER_ICON;
  gIcon = G_USER_ICON;
  statusCandidates: StatusCandidateModel[] = [];
  pageSize = 10;
  pageIndex = 1;
  length = 0;
  sortByField = 'createdAt';
  sortDirection = 'desc';
  isDelete = false;
  filter = '';
  listUniversity: string[];
  chkInterview = false;
  chkReminder = false;
  filterByIntAndRem = '';
  selectStatusCandidate = '';
  btnAddResult = true;
  status = '';
  staticCandidateStatusId: number;
  paginationSize = PAGINATION_SIZE;
  archieveRouterSub: Subscription;
  routeActionSubscription: Subscription;
  btnArchiev = '';

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;
  constructor(
    private router: Router,
    private dialog: MatDialog,
    public candidateService: CandidateService,
    private statusCandidateService: StatusCandidateService,
    private message: MessageService,
    private interviewService: InterviewService,
    private isloadingService: IsLoadingService,
    private activatedRoute: ActivatedRoute,
  ) {
    const GENDERVALUE = this.activatedRoute.snapshot.paramMap.get('gender');
    const STATUSVALUE = this.activatedRoute.snapshot.paramMap.get('status');
    if (!!GENDERVALUE) {
      if (GENDERVALUE === ' ') {
        this.filter = '';
      } else {
        this.filter = this.convertEachWordToFirstUppercaseLetter(GENDERVALUE);
      }
    }
    if (!!STATUSVALUE) {
      if (STATUSVALUE === 'all' || STATUSVALUE === 'All') {
        this.selectStatusCandidate = '';
      } else {
        this.selectStatusCandidate =
          this.convertEachWordToFirstUppercaseLetter(STATUSVALUE);
      }
      this.status = this.selectStatusCandidate;
    }
  }

  ngOnDestroy(): void {
    this.archieveRouterSub.unsubscribe();
    this.routeActionSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.paginator.pageIndex = 0;
    this.paginator.pageSize = 10;

    this.loadStatusCandidate();

    this.archieveRouterSub = this.activatedRoute.queryParams
      .pipe(filter((param) => param.archive))
      .subscribe(() => this.getCandidateListByIsDeleted());

    this.routeActionSubscription = this.activatedRoute.queryParams.subscribe(
      (param) => {
        this.paginator.pageIndex = param.page ?? 0;
        this.paginator.pageSize = param.size ?? 10;
        this.loadDataByFunction();
      },
    );
  }

  loadData(): void {
    this.filter = '';
    this.sortDirection = 'desc';
    this.sortByField = 'createdAt';
    this.loadDataByFunction();
  }

  getCandidateListByIsDeleted(): void {
    if (this.isDelete === false) {
      this.isDelete = true;
      this.btnArchiev = 'warn';
    } else {
      this.isDelete = false;
      this.btnArchiev = '';
    }
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;

    // To clear archieve url
    this.router.navigate(['/admin/candidate']);
  }

  loadDataByFunction(): void {
    const subscription = this.candidateService
      .getList(
        this.paginator.pageIndex + 1,
        this.paginator.pageSize,
        this.filter,
        this.sortDirection,
        this.sortByField,
        this.filterByIntAndRem,
        this.status,
        this.isDelete,
      )
      .subscribe((respone) => {
        this.candidates = respone.contents;
        this.candidates.map((item) => {
          item.arrUniversities = item.universities
            .map((x) => x.name)
            .join(', ');
        });
        this.length = respone.total;
        this.dataSource = new MatTableDataSource(this.candidates);
      });
    this.isloadingService.add(subscription, {
      key: 'candidate',
      unique: 'candidate',
    });
  }

  changeStatus(candidateId: number | string, statusId: string | number): void {
    const subscription = this.candidateService
      .updateStatusCandidate(candidateId, statusId)
      .subscribe(() => {
        this.message.showSuccess(
          'Candidate status was update successfully',
          'Update',
        );
        this.loadDataByFunction();
      });
    this.isloadingService.add(subscription, {
      key: 'candidate',
      unique: 'candidate',
    });
  }

  loadStatusCandidate(): void {
    this.statusCandidateService
      .getList(1, 100, '', 'title', 'asc')
      .subscribe((respone) => {
        this.statusCandidates = respone.contents.filter(
          (x) => x.active && !x.deleted,
        );
      });
  }

  filterByStatus(event: any): void {
    this.status = event.value;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  applyFilter(): void {
    if (this.filter.trim() === null || this.filter.trim() === '') {
      this.loadData();
    }
    if (this.filter.length >= 3) {
      this.loadDataByFunction();
      this.paginator.pageIndex = 0;
    }
  }

  setReminder(id: number): void {
    this.router.navigate(['/admin/reminders/add', id, 'SPECIAL']);
  }

  setInterview(id: number): void {
    this.router.navigate(['/admin/interview/create', id]);
  }

  addActivity(id: number): void {
    this.router.navigate(['/admin/activities/add', id]);
  }

  add(): void {
    this.router.navigate(['/admin/candidate/createCandidate']);
  }

  edit(id: string | number): void {
    this.router.navigate(['/admin/candidate/editCandidate', id]);
  }

  view(id: string | number): void {
    this.router.navigate(['/admin/candidate/candidateDetail', id]);
  }

  deleteCandidate(id: number, fullName: string): void {
    const dialogRef = this.dialog.open(DeleteCandidateComponent, {
      width: '600px',
      data: {
        id,
        fullName,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const subscription = this.candidateService
          .delete(id, true)
          .subscribe(() => {
            this.message.showSuccess(
              'Candidate was delete successfully',
              'Delete Candidate',
            );
            this.loadData();
          });
        this.isloadingService.add(subscription, {
          key: 'candidate',
          unique: 'candidate',
        });
      }
    });
  }

  confirmDialog(e: MatSelectChange, row: CandidateModel): void {
    const dialogRef = this.dialog.open(ComfirmDialogComponent, {
      width: '510px',
      data: {
        candidateId: row.id,
        statusId: row.candidateStatus.id,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const subscription = this.candidateService
          .updateStatusCandidate(row.id, e.value)
          .pipe(
            catchError((err) => {
              e.source.writeValue(row.candidateStatus.id);
              return throwError(err);
            }),
          )
          .subscribe(() => {
            this.message.showSuccess(
              'Candidate status was update successfully',
              'Update',
            );
            this.loadData();
          });
        this.isloadingService.add(subscription, {
          key: 'candidate',
          unique: 'candidate',
        });
      } else {
        e.source.writeValue(row.candidateStatus.id);
      }
    });
  }

  restoreOrPermenantDelete(
    id: number,
    isDelete: boolean,
    type?: string,
    fullName?: string,
  ): void {
    const dialogRef = this.dialog.open(ArchiveDialogComponent, {
      width: '650px',
      data: {
        id,
        isDelete,
        type,
        fullName,
      },
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.loadData();
    });
  }

  pageEvent(event: PageEvent): void {
    this.router.navigate(['admin', 'candidate'], {
      queryParams: {
        page: event.pageIndex,
        size: event.pageSize,
      },
    });
  }

  sortData(sort: Sort): void {
    this.sortDirection = sort.direction;
    switch (sort.active) {
      case 'Full name':
        this.sortByField = 'firstname';
        break;
      case 'Phone':
        this.sortByField = 'telephone';
        break;
      case 'GPA':
        this.sortByField = 'gpa';
        break;
      case 'Priority':
        this.sortByField = 'priority';
        break;
      case 'Last Interview':
        this.sortByField = 'lastInterview';
        break;
      case 'Created at':
        this.sortByField = 'createdAt';
        break;
      default:
        this.sortByField = 'createdAt';
        break;
    }
    if (this.sortDirection === undefined) {
      this.sortDirection = 'asc';
    }
    // this.pageIndex = 1;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  filterInterview(event: MatCheckboxChange): void {
    this.chkInterview = event.checked;
    if (this.chkInterview === true && this.chkReminder === true) {
      this.filterByIntAndRem = 'both';
    } else if (this.chkInterview === false && this.chkReminder === true) {
      this.filterByIntAndRem = 'reminder';
    } else if (this.chkInterview === true && this.chkReminder === false) {
      this.filterByIntAndRem = 'interview';
    } else {
      this.filterByIntAndRem = '';
    }
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  filterReminder(event: MatCheckboxChange): void {
    this.chkReminder = event.checked;
    if (this.chkInterview === true && this.chkReminder === true) {
      this.filterByIntAndRem = 'both';
    } else if (this.chkInterview === false && this.chkReminder === true) {
      this.filterByIntAndRem = 'reminder';
    } else if (this.chkInterview === true && this.chkReminder === false) {
      this.filterByIntAndRem = 'interview';
    } else {
      this.filterByIntAndRem = '';
    }
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  setLastResult(candidate: CandidateModel): void {
    this.interviewService
      .getById(candidate.interviews.id)
      .subscribe((interview) => {
        interview.candidate.photoUrl = candidate.photoUrl;
        const dailogRef = this.dialog.open(InterviewResultDialogComponent, {
          data: interview,
          width: '800px',
          disableClose: true,
          panelClass: 'overlay-scrollable',
        });

        dailogRef.afterClosed().subscribe((result) => {
          if (result && result.changed) {
            this.loadDataByFunction();
            this.message.showSuccess('Success', 'Update interview result');
          }
        });
      });
  }

  clearFilter(): void {
    this.filter = '';
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  getUrl(row: CandidateModel) {
    return `/candidate/${row.id}/view/${row.photoUrl}`;
  }

  convertEachWordToFirstUppercaseLetter(word: string): any {
    const splitStr = word.toLowerCase().split(' ');
    for (let i = 0; i < splitStr.length; i++) {
      splitStr[i] = splitStr[i].charAt(0).toUpperCase() + splitStr[i].slice(1);
    }
    return splitStr.join(' ');
  }

  getAge(date: string) {
    return getAge(date);
  }
}
