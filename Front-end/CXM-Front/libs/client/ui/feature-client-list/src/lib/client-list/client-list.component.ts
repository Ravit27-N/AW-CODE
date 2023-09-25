import { Store } from '@ngrx/store';
import {
  filterHistory,
  fromClientActions,
  fromClientSelector,
} from '@cxm-smartflow/client/data-access';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { MatSort } from '@angular/material/sort';
import { Router } from '@angular/router';
import {
  appLocalStorageConstant,
  appRoute,
} from '@cxm-smartflow/shared/data-access/model';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss'],
})
export class ClientListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns: string[] = ['name', 'lastModified', 'createdAt', 'actions'];
  clients$ = new MatTableDataSource();
  pagination$: Observable<any>;
  destroy$ = new Subject<boolean>();
  isAdmin = UserUtil.isAdmin();
  modifiable = UserUtil.modifyClient();
  filteredHistory: {
    page?: number;
    pageSize?: number;
    sortByField?: string;
    sortDirection: 'asc' | 'desc' | string;
  };

  @ViewChild(MatSort, { static: false }) matSort: MatSort;

  handleConsult(row: any) {
    this.router
      .navigateByUrl(`${appRoute.cxmClient.navigateToModifyClient}/${row.id}`)
      .then();
  }

  handleDelete(id: number) {
    this.store.dispatch(fromClientActions.attempToDeleteClient({ id }));
  }

  doLoadFilter(filter: {
    page?: number;
    pageSize?: number;
    sortByField?: string;
    sortDirection: 'asc' | 'desc' | string;
  }) {
    localStorage.setItem(filterHistory, JSON.stringify(filter));
    this.store.dispatch(fromClientActions.filterChanged({ ...filter }));
  }

  ngAfterViewInit(): void {
    this.store
      .select(fromClientSelector.selectFilters)
      .pipe(takeUntil(this.destroy$))
      .subscribe((filters: any) => {
        const history = JSON.parse(<string>localStorage.getItem(filterHistory));
        this.matSort.active = history?.sortByField || filters?.sortByField;
        this.matSort.direction =
          history?.sortDirection || filters?.sortDirection;
      });

    this.matSort.sortChange.pipe(takeUntil(this.destroy$)).subscribe((sort) => {
      const history = JSON.parse(<string>localStorage.getItem(filterHistory));
      this.filteredHistory = {
        pageSize: history?.pageSize || this.filteredHistory?.pageSize,
        page: history?.page || this.filteredHistory?.page,
        sortDirection: sort.direction,
        sortByField: sort.active,
      };
      this.doLoadFilter(this.filteredHistory);
    });
  }

  ngOnInit(): void {
    this.store
      .select(fromClientSelector.selectClientsList)
      .pipe(takeUntil(this.destroy$))
      .subscribe((clients) => {
        this.clients$ = new MatTableDataSource(clients);
      });

    this.pagination$ = this.store.select(fromClientSelector.selectPagination);

    const previousURL: string[] =
      JSON.parse(<string>localStorage.getItem('previousURL')) || [];
    const keepRoutes = [
      appRoute.cxmClient.navigateToCreateClient,
      appRoute.cxmClient.navigateToModifyClient,
      appRoute.cxmClient.navigateToListClient,
    ];
    if (
      !keepRoutes.some((r) => previousURL[previousURL.length - 1]?.includes(r))
    ) {
      localStorage.removeItem(filterHistory);
    }
    const history = JSON.parse(<string>localStorage.getItem(filterHistory));
    this.store.dispatch(
      fromClientActions.loadClientList({
        filters: {
          page: history?.page || 1,
          pageSize: history?.pageSize || 10,
          sortByField: history?.sortByField || 'lastModified',
          sortDirection: history?.sortDirection || 'desc',
        },
      })
    );
  }

  constructor(
    private store: Store,
    private translate: TranslateService,
    private router: Router
  ) {
    this.translate.use(
      localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) ||
        appLocalStorageConstant.Common.Locale.Fr
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next(true);
    this.store.dispatch(fromClientActions.destroyClientModule());
  }

  paginationUpdate(page: {
    pageSize: number;
    pageIndex: number;
    length: number;
  }) {
    const history = JSON.parse(<string>localStorage.getItem(filterHistory));
    this.filteredHistory = {
      sortDirection: history?.sortDirection || this.filteredHistory?.sortDirection,
      sortByField: history?.sortByField || this.filteredHistory?.sortByField,
      pageSize: page.pageSize,
      page: page.pageIndex,
    };
    this.doLoadFilter(this.filteredHistory);
  }
}
