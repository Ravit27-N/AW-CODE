import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSort, Sort } from '@angular/material/sort';
import { Subscription } from 'rxjs';
import {
  DirectoryFeedModel,
  DirectoryFeedService,
  getDirectoryFeedList,
  selectDirectoryFeedTables,
  unloadManageDirectoryState,
} from '@cxm-smartflow/directory-feed/data-access';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { appRoute } from '@cxm-smartflow/shared/data-access/model';
import { BlobFile } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-feature-list-directory-feeds',
  templateUrl: './feature-list-directory-feeds.component.html',
  styleUrls: ['./feature-list-directory-feeds.component.scss'],
})
export class FeatureListDirectoryFeedsComponent implements OnInit, OnDestroy {
  definitionTableColumns: string[] = [
    'displayName',
    'lastModified',
    'createdAt',
    'feedingDate',
    'actions',
  ];
  definitionTableDatasource: DirectoryFeedModel[] = [];
  definitionTableCriteria = {
    page: 1,
    pageSize: 10,
    total: 0,
    sortByField: 'lastModified',
    sortDirection: 'desc',
  };
  @ViewChild(MatSort, { static: true }) matSort: MatSort;

  #subscription: Subscription = new Subscription();

  constructor(
    private store$: Store,
    private router: Router,
    private directoryFeedService: DirectoryFeedService
  ) {}

  ngOnInit(): void {
    this.subscribeDirectoryTableMatSort();
    this.subscribeDirectoryTableCriteria();
    this.subscribeDirectoryTableDatasource();
    this.fetchDirectory();
  }

  ngOnDestroy() {
    this.#subscription.unsubscribe();
    this.store$.dispatch(unloadManageDirectoryState());
  }

  private subscribeDirectoryTableMatSort(): void {
    this.matSort.active = 'lastModified';
    this.matSort.direction = 'desc';

    const subscription: Subscription = this.matSort.sortChange.subscribe(
      (sort: Sort): void => {
        const criteria: any = {
          ...this.definitionTableCriteria,
          sortByField: sort.active,
          sortDirection: sort.direction,
        };

        this.definitionTableCriteria = criteria;
        this.fetchDirectory();
      }
    );
    this.#subscription.add(subscription);
  }

  private subscribeDirectoryTableCriteria(): void {
    const subscription: Subscription = this.store$
      .select(selectDirectoryFeedTables)
      .subscribe((tableCriteria): void => {
        this.definitionTableCriteria = {
          ...this.definitionTableCriteria,
          page: tableCriteria.page,
          pageSize: tableCriteria.pageSize,
          total: tableCriteria.total,
        };

        this.definitionTableDatasource = tableCriteria.contents;
      });
    this.#subscription.add(subscription);
  }

  private subscribeDirectoryTableDatasource(): void {
    const subscription: Subscription = this.store$
      .select(selectDirectoryFeedTables)
      .subscribe((datasource): void => {
        this.definitionTableCriteria = {
          ...this.definitionTableCriteria,
          page: datasource.page,
          pageSize: datasource.pageSize,
          total: datasource.total,
        };
      });
    this.#subscription.add(subscription);
  }

  paginationUpdateEvent($event: {
    pageSize: number;
    pageIndex: number;
    length: number;
  }): void {
    this.definitionTableCriteria = {
      ...this.definitionTableCriteria,
      page: $event.pageIndex,
      pageSize: $event.pageSize,
    };

    this.fetchDirectory();
  }

  private fetchDirectory(): void {
    this.store$.dispatch(
      getDirectoryFeedList({
        page: this.definitionTableCriteria.page,
        pageSize: this.definitionTableCriteria.pageSize,
        sortByField: this.definitionTableCriteria.sortByField,
        sortDirection: this.definitionTableCriteria.sortDirection,
      })
    );
  }

  async downloadStructureFileEvent(id: number, fileName: string): Promise<void> {
    const blobFile = await this.directoryFeedService
      .downloadDirectoryFeedCsv(id)
      .toPromise();
    const file: File = BlobFile.blobToFile(
      blobFile.file,
      `${fileName}.csv`
    );
    BlobFile.saveFile(file);
  }

  consulDirectory(id: number, name: string, ownerId: number): void {
    this.router.navigate(
      [appRoute.cxmDirectory.navigateToViewDirectoryFeedDetail],
      { queryParams: { id, name, ownerId } }
    );
  }
}
