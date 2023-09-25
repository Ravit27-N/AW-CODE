import { Router, ActivatedRoute } from '@angular/router';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import {
  FilterOptions,
  Interview,
  InterviewService,
  InterviewTemplateModel,
  InterviewTemplateService,
  PAGINATION_SIZE,
} from '../core';
import { MatDialog } from '@angular/material/dialog';
import {
  InterviewDialogComponent,
  InterviewResultDialogComponent,
  InterviewViewDialogComponent,
} from './dialog.component';
import { MatPaginator } from '@angular/material/paginator';
import {
  debounceTime,
  distinctUntilChanged,
  map,
  skip,
  switchMap,
} from 'rxjs/operators';
import { MatSort } from '@angular/material/sort';
import { BehaviorSubject, merge, Subject } from 'rxjs';
import {
  aWeekFrom,
  ComfirmDailogComponent,
  firstDayWeeks,
  formatDateWithoutTime,
  getMonthly,
} from '../shared';
import { MessageService } from '../core';
import { IsLoadingService } from '@service-work/is-loading';

@Component({
  selector: 'app-interview-list-view',
  templateUrl: './interview-list-view.component.html',
  styleUrls: ['interview-component.css'],
})
export class InterviewListViewComponent implements OnInit, AfterViewInit {
  interviews: Interview[] = [];
  interviewFilters: { title: string }[] = [
    { title: 'All' },
    { title: 'Today' },
    { title: 'Weekly' },
    { title: 'Monthly' },
  ];
  statusInterview: InterviewTemplateModel[] = [];
  resultLength = 0;
  paginationSize = PAGINATION_SIZE;
  displayedColumns: string[] = [
    'id',
    'title',
    'description',
    'candidate',
    'dateTime',
    'status',
    'action',
  ];

  filterByDate: { include: boolean; start?: Date; end?: Date } = {
    include: false,
  };
  filterByStatus: { status: InterviewTemplateModel; checked: boolean }[] = [];

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  private detectChange$ = new Subject<boolean>();
  searchTerm$ = new BehaviorSubject<string>('');

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private interviewService: InterviewService,
    private statuInterviewService: InterviewTemplateService,
    private messageService: MessageService,
    private isloadingService: IsLoadingService,
    private activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    const getmonth = this.activatedRoute.snapshot.paramMap.get('month');
    const getstatus = this.activatedRoute.snapshot.paramMap.get('status');

    if (getmonth) {
      const DATE = new Date();
      DATE.setMonth(Number(getmonth));
      const { start, end } = getMonthly(DATE);
      this.filterByDate = {
        include: true,
        start,
        end,
      };
    }

    this.statuInterviewService.getList().subscribe((data) => {
      this.statusInterview = data.contents.filter((x) => x.active);
      this.filterByStatus = this.statusInterview.map((x) => ({
        status: x,
        checked: getstatus ? x.name === getstatus : false,
      }));
      this.detectChange$.next(true);
    });
  }

  ngAfterViewInit(): void {
    const observeSearchTerm$ = this.searchTerm$
      .asObservable()
      .pipe(debounceTime(300))
      .pipe(distinctUntilChanged())
      .pipe(skip(1));

    // Update table on any change by sort,paging adn detectechange
    merge(
      this.sort.sortChange,
      this.paginator.page,
      this.detectChange$,
      observeSearchTerm$,
    )
      .pipe(
        switchMap(() => {
          const filter: FilterOptions = {
            sortByField: 'dateTime',
            sortDirection: 'desc',
          };
          if (this.sort.active) {
            filter.sortByField = this.sort.active;
            filter.sortDirection = this.sort.direction;
          }

          if (this.searchTerm$.value && this.searchTerm$.value !== '') {
            filter.filter = this.searchTerm$.value;
          }

          if (this.filterByDate.include) {
            filter.startDate = formatDateWithoutTime(this.filterByDate.start);
            filter.endDate = formatDateWithoutTime(this.filterByDate.end);
          }

          const hasFilterByStatus = this.filterByStatus
            .filter((x) => x.checked)
            .map((x) => x.status.name.toLocaleLowerCase());
          if (hasFilterByStatus && hasFilterByStatus.length > 0) {
            filter.status = hasFilterByStatus;
          }
          // (unique): if loading existed overwrite, we need one loading at the time
          this.isloadingService.add({ key: 'interview', unique: 'interview' });
          return this.interviewService.getList(
            this.paginator.pageSize,
            this.paginator.pageIndex + 1,
            filter,
          );
        }),
        map((data) => {
          this.isloadingService.remove({ key: 'interview' });
          this.resultLength = data.total;
          return data.contents;
        }),
      )
      .subscribe((data) => (this.interviews = data));
  }

  edit(interview: Interview): void {
    const dailogRef = this.dialog.open(InterviewDialogComponent, {
      data: interview,
      width: '800px',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });
    dailogRef.afterClosed().subscribe((result) => {
      this.reloadDataOnChange(result);
      if (result && result.changed) {
        this.messageService.showSuccess('Success', 'Update interview');
      }
    });
  }

  delete(interview: Interview): void {
    this.dialog
      .open(ComfirmDailogComponent, {
        data: { title: interview.title },
        width: '550px',
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          const subscription = this.interviewService
            .softDelete(interview, result)
            .subscribe(() => {
              this.detectChange$.next(true);
              this.messageService.showSuccess('Success', 'Delete interview');
            });
          this.isloadingService.add(subscription, {
            key: 'interview',
            unique: 'interview',
          });
        }
      });
  }

  view(interview: Interview): void {
    const dailogRef = this.dialog.open(InterviewViewDialogComponent, {
      data: interview,
      width: '800px',
      panelClass: 'overlay-scrollable',
    });
    dailogRef
      .afterClosed()
      .subscribe((result) => this.reloadDataOnChange(result));
  }

  seeResult(interview: Interview): void {
    const dailogRef = this.dialog.open(InterviewResultDialogComponent, {
      data: interview,
      width: '800px',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });

    dailogRef.afterClosed().subscribe((result) => {
      this.reloadDataOnChange(result);
      if (result && result.changed) {
        this.messageService.showSuccess('Success', 'Update interview result');
      }
    });
  }

  applyfilter(value: string): void {
    switch (value) {
      case 'Today':
        this.filterByDate = {
          include: true,
          start: new Date(),
          end: new Date(),
        };
        break;
      case 'Weekly':
        const monday = firstDayWeeks(new Date());
        this.filterByDate = {
          include: true,
          start: monday,
          end: aWeekFrom(monday),
        };
        break;
      case 'Monthly':
        const { start, end } = getMonthly(new Date());
        this.filterByDate = {
          include: true,
          start,
          end,
        };

        break;
      default:
        this.filterByDate = { include: false };
        break;
    }
    this.detectChange$.next(true);
  }

  doCheck(value: number, checked: boolean): void {
    this.filterByStatus.forEach((x) => {
      if (x.status.id === value) {
        x.checked = checked;
      }
    });
    this.detectChange$.next(true);
  }

  changeStatus(interview: Interview, status: InterviewTemplateModel): void {
    if (status.name === interview.status) {
      return;
    }

    const subscription = this.interviewService
      .changeStatus(interview, status.id)
      .subscribe(() => {
        this.detectChange$.next(true);
        this.messageService.showSuccess('Success', 'Change interview status');
      });
    this.isloadingService.add(subscription, {
      key: 'interview',
      unique: 'interview',
    });
  }

  private reloadDataOnChange(result: any): void {
    if (result && result.changed) {
      this.detectChange$.next(result.changed);
    }
  }

  setReminder(id: number): void {
    this.router.navigate(['/admin/reminders/add', id, 'INTERVIEW']);
  }
}
