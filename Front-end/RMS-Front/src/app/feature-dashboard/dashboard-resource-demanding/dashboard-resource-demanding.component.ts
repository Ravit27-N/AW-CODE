import { Component, Input, OnInit, ViewChild } from '@angular/core';
import {
  CandidateService,
  days,
  DemandModel,
  DemandService,
  isDeadline,
  ListDemandModel,
} from '../../core';
import { MatSort, Sort, SortDirection } from '@angular/material/sort';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import {
  AwAddResourceToDemandPopupFragmentService,
  AwConfirmMessageService,
  AwPaginationModel,
  AwSnackbarService,
} from '../../shared';
import { KeyValue } from '@angular/common';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';

interface ParamsDemandList {
  pageIndex: number;
  pageSize: number;
  deleted: boolean;
  filterOption?: {
    filter?: string;
    sortDirection?: SortDirection;
    sortByField?: string;
    startDate?: string;
    endDate?: string;
    status?: string[];
  };
}

export interface DemandTableList extends DemandModel {
  no: number;
}

@Component({
  selector: 'app-dashboard-resource-demanding',
  templateUrl: './dashboard-resource-demanding.component.html',
  styleUrls: ['./dashboard-resource-demanding.component.scss'],
})
export class DashboardResourceDemandingComponent implements OnInit {
  @Input() demandTableDatasource: DemandTableList[] = [];
  demandTableColumns: string[] = [
    'no',
    'project',
    'jobDescription',
    'qty',
    'experienceLevel',
    'deadLine',
    'resources',
    'status',
  ];

  requestListCriteria: ParamsDemandList = {
    pageIndex: 1,
    pageSize: 4,
    deleted: false,
    filterOption: {
      sortByField: 'deadLine',
      sortDirection: 'asc',
    },
  };
  totalDemands = 0;
  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  switchPinningDemand = new Subject<{ id: number; checked: boolean }>();
  isArchive = false;
  #subscription = new Subscription();

  constructor(
    private demandService: DemandService,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    private demandListIncreaseResourcePopupService: AwAddResourceToDemandPopupFragmentService,
    private candidateService: CandidateService,
    private router: Router,
  ) {}

  async ngOnInit(): Promise<void> {
    await this.subscribeDemandTableMatSort();
    this.subscribeSwitchPinningDemand();
    await this.fetchDemands();
  }

  /**
   * Fetches demands from the server and updates the datasource.
   */
  async fetchDemands(): Promise<void> {
    try {
      // Fetch demands using the demand service
      const { pageIndex, pageSize, deleted, filterOption } =
        this.requestListCriteria;
      let httpParams = new HttpParams()
        .set('page', pageIndex.toString())
        .set('pageSize', pageSize.toString())
        .set('active', 'true')
        .set('isDeleted', `${deleted}`);
      if (filterOption) {
        Object.keys(filterOption).forEach(
          (k) => (httpParams = httpParams.set(k, filterOption[k])),
        );
      }
      const demands = await this.demandService.getList(httpParams).toPromise();

      // Map and update demands for the table datasource
      this.demandTableDatasource = demands.contents.map((item, itemIndex) =>
        this.mapToDemandTableList(item, demands, itemIndex),
      );
      this.totalDemands = demands.total;
    } catch (error) {
      this.alertSnackbarMessage(
        'Something went wrong. Cannot communicate with the server.',
      );
    }
  }

  async handlePinToDashboard(id: number, checked: boolean): Promise<void> {
    try {
      await this.demandService.updateStatus(id, checked).toPromise();
      this.showSuccessMessage('Demand status was updated successfully.');
    } catch (error) {
      this.alertSnackbarMessage(
        'Something went wrong. Cannot communicate with the server.',
      );
    } finally {
      await this.fetchDemands();
    }
  }

  /**
   * Fetches archived demands or active demands based on the current archive state.
   */
  async fetchArchive(): Promise<void> {
    this.isArchive = !this.isArchive;
    this.requestListCriteria.deleted = this.isArchive;
    this.requestListCriteria.pageIndex = 1;
    await this.fetchDemands();
  }

  /**
   * Moves a demand to the archive (soft delete).
   *
   * @param rowId - The ID of the demand to be archived.
   */
  async addToArchive(rowId: number): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Archive demand',
        message: 'Are you sure that you want to add this demand to archive?',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.demandService.softDelete(rowId).toPromise();
        this.showSuccessMessage(
          'The demands has been added to archive successfully.',
        );
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  /**
   * Permanently deletes a demand (hard delete).
   *
   * @param id - The ID of the demand to be deleted.
   */
  async deleteDemandPermanent(id: number) {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove demand?',
        message:
          'Are you sure you want to remove this demand?\nThis action is irreversible.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.demandService.hardDelete(id).toPromise();
        this.showSuccessMessage('The demands has been delete successfully.');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  /**
   * Handles pagination change event and fetches demands for the selected page.
   *
   * @param page - The new pagination information.
   */
  async pageChangeEvent(page: AwPaginationModel) {
    this.requestListCriteria.pageIndex = page.pageIndex;
    await this.fetchDemands();
  }

  /**
   * Restores a demand from the archive.
   *
   * @param id - The ID of the demand to be restored.
   */
  async restoreFromArchive(id: number) {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Restore Demand',
        message:
          'Are you sure to restore this demand?\nIf Okay, this will also restore records that are related to this.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.demandService.restore(id, false).toPromise();
        this.showSuccessMessage('The demands has been restored successfully.');
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  /**
   * Searches demands based on the provided filter text.
   *
   * @param filter - The filter text to be applied.
   */
  async searchDemand(filter: string) {
    this.requestListCriteria = {
      ...this.requestListCriteria,
      filterOption: {
        ...this.requestListCriteria.filterOption,
        filter,
      },
    };
    await this.fetchDemands();
  }

  async increaseResource(nbCandidates: number[], demandId: number, requiredDemand: number) {
    const httpParams = new HttpParams()
      .set('page', '1')
      .set('pageSize', '0')
      .set('sortByField', 'firstname')
      .set('sortDirection', 'asc');
    const resources = await this.candidateService
      .getAvailableResource(httpParams, demandId)
    const resourceDatasource: KeyValue<string, string>[] =
      resources.contents.map((resource) => {
        return {
          key: `${resource.id}`,
          value: `${resource.firstname} ${resource.lastname.toUpperCase()}`,
        };
      });

    const selectedSources = nbCandidates.map((candidate) =>
      candidate.toString(),
    );

    const candidateIds = await this.demandListIncreaseResourcePopupService
      .chooseResource(resourceDatasource, selectedSources, requiredDemand)
      .toPromise();

    if (candidateIds) {
      try {
        await this.demandService
          .addResourceToDemand({
            candidateIds,
            demandId,
          })
          .toPromise();
        this.showSuccessMessage(
          'The resource has been added to project successfully.',
        );
      } catch (error) {
        this.alertSnackbarMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  addDemand(): void {
    this.router.navigateByUrl('/admin/demand/add-list');
  }

  getRemandDeadlineMessage(deadline: number): string {
    let daysToDeadline = days(deadline);
    daysToDeadline = daysToDeadline !== 0 ? -daysToDeadline : 0;
    const overdueDeadline = !isDeadline(deadline);

    if (daysToDeadline === 1 || daysToDeadline === -1) {
      return `Overdue by ${daysToDeadline} day`;
    } else if (daysToDeadline === 0) {
      return `Today is the deadline`;
    } else if (overdueDeadline && daysToDeadline !== 0) {
      return `Overdue by ${daysToDeadline} days`;
    } else if (daysToDeadline <= 5) {
      return `${daysToDeadline} days to deadline`;
    } else {
      return `On track, ${daysToDeadline} days to deadline`;
    }
  }

  getWarningDate(deadline: number): 'primary' | 'danger' | 'warning' {
    const daysToDeadline = -days(deadline);
    const overdueDeadline = !isDeadline(deadline);

    if (overdueDeadline && daysToDeadline !== 0) {
      return 'danger';
    } else if (daysToDeadline <= 5 || daysToDeadline === 0) {
      return 'warning';
    } else {
      return 'primary';
    }
  }

  /**
   * Maps a demand item to the demand table list structure.
   *
   * @param item - The demand item to be mapped.
   * @param demands - The demand list containing paging information.
   * @param itemIndex - Index of the item in the demand list.
   * @returns The mapped demand item for the table.
   */
  private mapToDemandTableList(
    item: DemandModel,
    demands: ListDemandModel,
    itemIndex: number,
  ): DemandTableList {
    const no =
      demands.page * demands.pageSize + itemIndex + 1 - demands.pageSize;
    return { ...item, no };
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  /**
   * Subscribes to MatSort changes and updates sorting criteria.
   * Fetches demands after sort criteria are updated.
   */
  private async subscribeDemandTableMatSort() {
    // Set initial sorting criteria
    this.matSort.active =
      this.requestListCriteria?.filterOption?.sortByField || 'project';
    this.matSort.direction =
      this.requestListCriteria?.filterOption?.sortDirection || 'desc';

    // Subscribe to sort changes
    const subscription: Subscription = this.matSort.sortChange
      .pipe(debounceTime(300))
      .subscribe((sort: Sort) => {
        // Update sorting criteria
        this.requestListCriteria = {
          ...this.requestListCriteria,
          filterOption: {
            ...this.requestListCriteria.filterOption,
            sortByField: sort.active,
            sortDirection: sort.direction,
          },
        };
        // Fetch demands with updated sort criteria
        this.fetchDemands();
      });
    this.#subscription.add(subscription);
  }

  /**
   * Subscribes to the switchPinningDemand Subject and handles pinning/unpinning demands.
   */
  private subscribeSwitchPinningDemand(): void {
    const subscription: Subscription = this.switchPinningDemand
      .pipe(debounceTime(200))
      .subscribe((response) => {
        this.handlePinToDashboard(response.id, response.checked);
      });
    this.#subscription.add(subscription);
  }
}
