import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { IsLoadingService } from '@service-work/is-loading';
import { BehaviorSubject, Subject } from 'rxjs';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import {
  DefaultCriteria,
  FilterOptions,
  MailconfigService,
  ProjectCriteria,
} from '../../core';
import { SystemConfiguration } from '../../core/model/MailconfigFormModel';
import { ConfigurationFormDialogComponent } from '../mailconfig';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-feature-system-config',
  templateUrl: './feature-system-config.component.html',
  styleUrls: ['./feature-system-config.component.scss'],
})
export class FeatureSystemConfigComponent implements OnInit {
  list: SystemConfiguration[] = [];
  displayedColumns = ['No', 'key', 'value', 'description', 'Action'];

  detectChange$ = new Subject<boolean>();
  searchTerm$ = new BehaviorSubject<string>('');
  dataSource: MatTableDataSource<SystemConfiguration> =
    new MatTableDataSource<SystemConfiguration>();
  defaultCriteria: DefaultCriteria = {
    pageIndex: 1,
    pageSize: 10,
    sortByField: 'configKey',
    sortDirection: 'desc',
  };
  filterListCriteria: ProjectCriteria = {
    defaultCriteria: { ...this.defaultCriteria },
    filter: '',
    startDate: '',
    endDate: '',
    status: [],
    option: 0,
  };
  total = 0;
  constructor(
    private route: ActivatedRoute,
    private mailconfig: MailconfigService,
    public dialog: MatDialog,
    private isloadingService: IsLoadingService,
  ) {}

  ngOnInit() {
    this.setUp();
  }
  setUp() {
    this.loadDataByFunction();
  }

  add(): void {
    this.dialog
      .open(ConfigurationFormDialogComponent, {
        width: '800px',
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => this.detectChange$.next(result?.changed));
  }

  edit(config: SystemConfiguration): void {
    this.dialog
      .open(ConfigurationFormDialogComponent, {
        width: '800px',
        data: config,
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => this.detectChange$.next(result?.changed));
  }

  delete(row: SystemConfiguration): void {
    this.dialog
      .open(ComfirmDailogComponent, {
        width: '450px',
        data: { title: row.configKey },
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.mailconfig
            .deleteConfig(row.id)
            .subscribe(() => this.detectChange$.next(true));
        }
      });
  }

  loadDataByFunction(): void {
    const filterOption: FilterOptions = {
      filter: this.filterListCriteria.filter,
      sortDirection: this.filterListCriteria.defaultCriteria.sortDirection,
      sortByField: this.filterListCriteria.defaultCriteria.sortByField,
      startDate: this.filterListCriteria.startDate,
      endDate: this.filterListCriteria.endDate,
      status: this.filterListCriteria.status,
    };

    const subscription = this.mailconfig
      .getConfigList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        filterOption,
      )
      .subscribe((respone) => {
        this.total = respone.total;
        this.dataSource = new MatTableDataSource(respone.contents);
      });
    this.isloadingService.add(subscription, {
      key: 'reminder',
      unique: 'reminder',
    });
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.loadDataByFunction();
  }

  getMailRow(candidateField: any) {
    return { row: candidateField };
  }

  searchFilterChange(event: any) {
    this.filterListCriteria.filter = event;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.filterListCriteria.defaultCriteria.pageSize = 10;
    this.loadDataByFunction();
  }

  sortData(sort: Sort) {
    this.filterListCriteria.sortDirection = sort.direction;
    this.filterListCriteria.sortByField = sort.active;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.loadDataByFunction();
  }
}
