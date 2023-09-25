import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { sharedDefaultListCriteriaDataConstant } from '../../../../shared/constants/shared-default-list-criteria-data.constant';
import { MatSort, Sort } from '@angular/material/sort';
import { Subscription } from 'rxjs';
import {
  appRoute,
  AwConfirmMessageService,
  AwPaginationModel,
  AwSnackbarService,
  EntityResponseHandler,
} from '../../../../shared';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { ScopedModelGroupListTable } from '../../models/scoped-model-group-list-table.model';
import { ScopedServiceGroupService } from '../../services/scoped-service-group.service';
import { ScopedModelGroupListItem } from '../../models/scoped-model-group-list.model';
import { ScopedModelGroupListFilterCriteria } from '../../models/scoped-model-group-list-filter-criteria.model';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-page-group-list',
  templateUrl: './page-group-list.component.html',
  styleUrls: ['./page-group-list.component.scss'],
})
export class PageGroupListComponent implements OnInit, OnDestroy {
  groupTableDatasource: ScopedModelGroupListTable[] = [];
  groupTableColumns: string[] = ['no', 'name', 'actions'];
  groupListCriteria: ScopedModelGroupListFilterCriteria = {
    ...sharedDefaultListCriteriaDataConstant,
    sortByField: 'name',
  };
  totalGroups = 0;

  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  #subscription = new Subscription();

  constructor(
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    private scopedServiceGroupService: ScopedServiceGroupService,
    private router: Router,
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Life cycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    if (localStorage.getItem('group-list')) {
      this.groupListCriteria = JSON.parse(localStorage.getItem('group-list'));
    }

    this.subscribeGroupTableMatSort();
    this.fetchGroups().then();
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Fetching APIs
  // -----------------------------------------------------------------------------------------------------
  async fetchGroups(): Promise<void> {
    localStorage.setItem('group-list', JSON.stringify(this.groupListCriteria));

    try {
      // Fetch groups using the group service
      let httpParams = new HttpParams();
      if (this.groupListCriteria) {
        Object.keys(this.groupListCriteria).forEach(
          (filterKey) =>
            (httpParams = httpParams.set(
              filterKey,
              this.groupListCriteria[filterKey],
            )),
        );
      }
      const groupListResponse = await this.scopedServiceGroupService
        .getAllGroups(httpParams)
        .toPromise();

      // Map and update groups for the table datasource
      this.groupTableDatasource = groupListResponse.contents.map(
        (item, itemIndex) =>
          this.mapToGroupTableListByAddNoField(
            item,
            groupListResponse,
            itemIndex,
          ),
      );
      this.totalGroups = groupListResponse.total;
    } catch (error) {
      this.showErrorMessage(
        'Something went wrong. Cannot communicate with the server.',
      );
    }
  }

  async deleteGroupPermanent(id: number) {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove group?',
        message:
          'Are you sure you want to remove this group?\nThis action is irreversible.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        this.scopedServiceGroupService.deleteGroupById(id).toPromise().then();
        this.showSuccessMessage('The group has been delete successfully.');
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchGroups();
      }
    }
  }

  async pageChangeEvent(page: AwPaginationModel) {
    this.groupListCriteria.page = page.pageIndex;
    await this.fetchGroups();
  }

  async fetchGroupByFilter(filter: string): Promise<void> {
    this.groupListCriteria = {
      ...this.groupListCriteria,
      filter,
    };
    await this.fetchGroups();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Navigations
  // -----------------------------------------------------------------------------------------------------
  navigateToPageCreateGroup(): void {
    this.router.navigateByUrl(appRoute.group.create).then();
  }

  navigateToEditGroupPage(row: ScopedModelGroupListItem): void {
    this.router.navigateByUrl(`${appRoute.group.edit}/${row.id}`).then();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Utility methods
  // -----------------------------------------------------------------------------------------------------
  @HostListener('window:beforeunload', ['$event'])
  onPageRefresh(): void {
    localStorage.removeItem('group-list');
  }

  // -----------------------------------------------------------------------------------------------------
  private mapToGroupTableListByAddNoField(
    item: ScopedModelGroupListItem,
    groups: EntityResponseHandler<ScopedModelGroupListItem>,
    itemIndex: number,
  ): ScopedModelGroupListTable {
    const no = groups.page * groups.pageSize + itemIndex + 1 - groups.pageSize;
    return { ...item, no };
  }

  private showErrorMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message,
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Subscriptions

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Listeners

  // -----------------------------------------------------------------------------------------------------
  private subscribeGroupTableMatSort(): void {
    // Set initial sorting criteria
    this.matSort.active = this.groupListCriteria.sortByField || 'project';
    this.matSort.direction = this.groupListCriteria.sortDirection || 'desc';

    // Subscribe to sort changes
    const subscription: Subscription = this.matSort.sortChange
      .pipe(debounceTime(300))
      .subscribe((sort: Sort) => {
        // Update sorting criteria
        this.groupListCriteria = {
          ...this.groupListCriteria,
          sortByField: sort.active,
          sortDirection: sort.direction,
        };
        // Fetch groups with updated sort criteria
        this.fetchGroups().then();
      });
    this.#subscription.add(subscription);
  }
}
