import {
  getMonthly,
  getToday,
  getWeekly,
} from '../../shared/utils/dateCustomFormat';
import { EditReminderComponent } from './../edit-reminder/edit-reminder.component';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { DeleteReminderComponent } from './../delete-reminder/delete-reminder.component';
import { ReminderDetailComponent } from './../reminder-detail/reminder-detail.component';
import { MatDialog } from '@angular/material/dialog';
import { MessageService } from './../../core/service/message.service';
import { ReminderService } from './../../core/service/reminder.service';
import { MatSort, Sort } from '@angular/material/sort';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ReminderModel } from './../../core/model/Reminder';
import { Component, OnInit, ViewChild } from '@angular/core';
import {
  DefaultCriteria,
  ProjectCriteria,
  PAGINATION_SIZE,
} from 'src/app/core';
import { IsLoadingService } from '@service-work/is-loading';
import { DatePipe, KeyValue } from '@angular/common';
import { CalendarOptionModel } from '../../candidate';
import { UrlUtil } from '../../shared/utils/url.util';
import { aWeekFrom, firstDayWeeks } from '../../shared';

@Component({
  selector: 'app-reminder-list',
  templateUrl: './reminder-list.component.html',
  styleUrls: ['./reminder-list.component.scss'],
})
export class ReminderListComponent implements OnInit {
  displayedColumns: string[] = [
    'No',
    'Title',
    'Description',
    'Candidate',
    'Date & time',
    'Status',
    'Action',
  ];
  dataSource: MatTableDataSource<ReminderModel> =
    new MatTableDataSource<ReminderModel>();
  reminderList: ReminderModel[];
  pageSize = 10;
  pageIndex = 1;
  length = 0;
  sortByField = 'createdAt';
  sortDirection = 'desc';
  filter = '';
  reminderTypes: Array<string> = [];
  startDate: Date | string;
  endDate: Date | string;
  chkNormal = false;
  chkSpecial = false;
  chkInterview = false;
  matButtonToggleGroup = '';
  statusToggle = true;
  dateFilter = UrlUtil.getParamsByKey('date') || '';
  filterGroup: Array<KeyValue<string, string>> = [
    { key: 'normal', value: 'Normal' },
    { key: 'special', value: 'Special' },
    { key: 'interview', value: 'Interview' },
  ];
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'dateTime',
    sortDirection: 'desc',
  };
  reminderListCriteria: ProjectCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    startDate: '',
    endDate: '',
    status: [],
    option: 0,
  };
  isInitialize = true;
  selectedCalendar = {
    startDate: new Date(),
    endDate: new Date(),
    option: 0,
  };

  filterCalendarSelectedChange: CalendarOptionModel;
  labelButton = 'Filter';
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  paginationSize = PAGINATION_SIZE;
  total = 0;
  pipe = new DatePipe('en-US');
  constructor(
    private reminderService: ReminderService,
    private message: MessageService,
    private dialog: MatDialog,
    private isloadingService: IsLoadingService,
  ) {
    this.reminderList = [
      {
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
      },
    ];
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadDataByFunction(): void {
    let from = '';
    let to = '';
    if (!this.isInitialize) {
      from = this.pipe.transform(
        this.filterCalendarSelectedChange.startDate,
        'dd-MM-yyyy',
        'short',
      );
      to = this.pipe.transform(
        this.filterCalendarSelectedChange.endDate,
        'dd-MM-yyyy',
        'short',
      );
    }

    const subscription = this.reminderService
      .getList(
        this.pageIndex,
        this.pageSize,
        this.reminderListCriteria.status,
        this.reminderListCriteria.filter,
        this.sortByField,
        this.sortDirection,
        from,
        to,
      )
      .subscribe((respone) => {
        this.reminderList = respone.contents;
        this.length = respone.total;
        this.total = respone.total;
        this.dataSource = new MatTableDataSource(this.reminderList);
      });
    this.isloadingService.add(subscription, {
      key: 'reminder',
      unique: 'reminder',
    });
  }

  loadData(): void {
    this.pageIndex = 1;
    this.pageSize = 10;
    this.reminderTypes = [''];
    this.filter = '';
    this.sortByField = 'createdAt';
    this.sortDirection = 'desc';

    if (this.dateFilter === 'this_week') {
      const monday = firstDayWeeks(new Date());
      this.filterCalendarSelectedChange = {
        startDate: monday,
        endDate: aWeekFrom(monday),
        option: 0,
      };
      this.selectedCalendar = this.filterCalendarSelectedChange;
      this.reminderListCriteria.startDate = this.pipe.transform(
        this.selectedCalendar.startDate,
        'dd-MM-yyyy',
        'short',
      );
      this.reminderListCriteria.endDate = this.pipe.transform(
        this.selectedCalendar.endDate,
        'dd-MM-yyyy',
        'short',
      );
      this.isInitialize = false;
    }

    this.loadDataByFunction();
    // this.paginator.pageIndex = 0;
  }

  filterByDate(event: any): void {
    switch (event.value) {
      case '':
        this.startDate = '';
        this.endDate = '';
        break;
      case 'today':
        this.startDate = getToday();
        this.endDate = getToday();
        break;
      case 'weekly':
        const { start, end } = getWeekly();
        this.startDate = start;
        this.endDate = end;
        break;
      case 'monthly':
        const { s, e } = getMonthly();
        this.startDate = s;
        this.endDate = e;
        break;
      default:
        this.startDate = '';
        this.endDate = '';
        break;
    }

    this.pageIndex = 1;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  checkStatusReminder(): void {
    if (
      this.chkNormal === true &&
      this.chkSpecial === true &&
      this.chkInterview === true
    ) {
      this.reminderTypes = ['NORMAL', 'SPECIAL', 'INTERVIEW'];
    } else if (
      this.chkNormal === true &&
      this.chkSpecial === true &&
      this.chkInterview === false
    ) {
      this.reminderTypes = ['NORMAL', 'SPECIAL'];
    } else if (
      this.chkNormal === true &&
      this.chkSpecial === false &&
      this.chkInterview === true
    ) {
      this.reminderTypes = ['NORMAL', 'INTERVIEW'];
      this.sortByField = 'createdAt';
    } else if (
      this.chkNormal === false &&
      this.chkSpecial === true &&
      this.chkInterview === true
    ) {
      this.reminderTypes = ['SPECIAL', 'INTERVIEW'];
    } else if (
      this.chkNormal === false &&
      this.chkSpecial === true &&
      this.chkInterview === false
    ) {
      this.reminderTypes = ['SPECIAL'];
    } else if (
      this.chkNormal === false &&
      this.chkSpecial === false &&
      this.chkInterview === true
    ) {
      this.reminderTypes = ['INTERVIEW'];
      this.sortByField = 'createdAt';
    } else if (
      this.chkNormal === true &&
      this.chkSpecial === false &&
      this.chkInterview === false
    ) {
      this.reminderTypes = ['NORMAL'];
      this.sortByField = 'createdAt';
    } else {
      this.reminderTypes = [''];
    }

    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  filterReminder(event: MatCheckboxChange): void {
    switch (event.source.value) {
      case 'normal':
        this.chkNormal = event.checked;
        this.checkStatusReminder();
        break;
      case 'special':
        this.chkSpecial = event.checked;
        this.checkStatusReminder();
        break;
      case 'interview':
        this.chkInterview = event.checked;
        this.checkStatusReminder();
        break;
      default:
        this.chkNormal = event.checked;
        break;
    }
  }

  applyFilter(): void {
    this.pageIndex = 1;
    if (this.filter.trim() === null || this.filter.trim() === '') {
      this.loadData();
    }
    if (this.filter.length >= 3) {
      this.loadDataByFunction();
      this.paginator.pageIndex = 0;
    }
  }

  sortData(sort: Sort): void {
    switch (sort.active) {
      case 'Title':
        this.sortByField = 'title';
        break;
      case 'Description':
        this.sortByField = 'description';
        break;
      case 'Candidate':
        if (
          JSON.stringify(this.reminderTypes) === JSON.stringify(['']) ||
          JSON.stringify(this.reminderTypes) === JSON.stringify(['SPECIAL']) ||
          JSON.stringify(this.reminderTypes) ===
            JSON.stringify(['NORMAL', 'SPECIAL', 'INTERVIEW']) ||
          JSON.stringify(this.reminderTypes) ===
            JSON.stringify(['NORMAL', 'SPECIAL']) ||
          JSON.stringify(this.reminderTypes) ===
            JSON.stringify(['SPECIAL', 'INTERVIEW'])
        ) {
          this.sortByField = 'candidate';
        }
        break;
      case 'Date & time':
        this.sortByField = 'dateReminder';
        break;
      default:
        this.sortByField = 'createdAt';
        break;
    }

    this.sortDirection = sort.direction;
    this.pageIndex = 1;
    this.loadDataByFunction();
    this.paginator.pageIndex = 0;
  }

  pageEvent(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;

    this.reminderListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.reminderListCriteria.defaultCriteria.pageSize = event.pageSize;

    this.loadDataByFunction();
  }

  checkStatus(rowId: string, active: boolean): void {
    let subscription = null;
    if (active === true) {
      subscription = this.reminderService
        .changeStatus(rowId, false)
        .subscribe(() => {
          this.message.showSuccess('Update reminder successfully', 'Update');
          this.loadDataByFunction();
        });
    } else {
      subscription = this.reminderService
        .changeStatus(rowId, true)
        .subscribe(() => {
          this.message.showSuccess('Update reminder successfully', 'Update');
          this.loadDataByFunction();
        });
    }
    this.isloadingService.add(subscription, {
      key: 'reminder',
      unique: 'reminder',
    });
  }

  edit(id: number): void {
    const dialogRef = this.dialog.open(EditReminderComponent, {
      maxWidth: '850px',
      data: {
        id,
      },
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.loadData();
    });
  }

  view(id: number): void {
    this.dialog.open(ReminderDetailComponent, {
      width: '700px',
      data: {
        id,
      },
      panelClass: 'overlay-scrollable',
    });
  }

  delete(id: number, title: string): void {
    title = 'Testing';
    const dialogRef = this.dialog.open(DeleteReminderComponent, {
      width: '500px',
      data: {
        id,
        title,
      },
      panelClass: 'overlay-scrollable',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const subscription = this.reminderService.delete(id).subscribe(() => {
          this.message.showSuccess(
            'Reminder was delete successfully.',
            'Delete Reminder',
          );
          this.loadData();
        });
        this.isloadingService.add(subscription, {
          key: 'reminder',
          unique: 'reminder',
        });
      }
    });
  }

  clearFilter(): void {
    this.filter = null;
    this.loadData();
  }

  getMultipleFilters(interviewFilterStatus: string[]): string[] {
    return this.filterGroup
      .filter((status) => interviewFilterStatus.includes(status.key))
      .map((status) => status.key)
      .map((key) => key);
  }

  filterChangeValue(event: any) {
    this.resetToFirstPaginator();
    this.reminderListCriteria.status = event?.filter;

    this.loadDataByFunction();
  }

  resetToFirstPaginator(): void {
    this.reminderListCriteria.defaultCriteria.pageIndex = 1;
    this.reminderListCriteria.defaultCriteria.pageSize = 10;
  }

  searchFilterChange(event: any) {
    this.pageIndex = 1;
    this.resetToFirstPaginator();
    this.reminderListCriteria.filter = event;
    this.loadDataByFunction();
  }

  calendarChange(event: CalendarOptionModel): void {
    if (event?.startDate && event?.endDate) {
      this.filterCalendarSelectedChange = event;
      this.isInitialize = false;
      this.loadDataByFunction();
    } else {
      this.isInitialize = true;
      this.loadDataByFunction();
    }
  }

  getRemainderRow(candidateField: any) {
    return { row: candidateField };
  }
}
