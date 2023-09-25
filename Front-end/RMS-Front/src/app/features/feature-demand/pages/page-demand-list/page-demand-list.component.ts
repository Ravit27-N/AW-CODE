import {
  Component,
  HostListener,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CandidateService, days, isDeadline } from '../../../../core';
import {
  appRoute,
  AwAddResourceToDemandPopupFragmentService,
  AwConfirmMessageService,
  AwPaginationModel,
  AwSnackbarService,
  EntityResponseHandler,
} from '../../../../shared';
import { MatSort, Sort } from '@angular/material/sort';
import { Subject, Subscription } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { KeyValue } from '@angular/common';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';
import { ScopedDemandListFilterCriteria } from '../../models/scoped-model-list-filter-criteria.model';
import { sharedDefaultListCriteriaDataConstant } from '../../../../shared/constants/shared-default-list-criteria-data.constant';
import { ScopedServiceDemandService } from '../../services/scoped-service-demand.service';
import { ScopedDemandListTable } from '../../models/scoped-model-demand-list-table.model';
import { ScopedDemandListItem } from '../../models/scoped-model-demand-list.model';

@Component({
  selector: 'app-feature-demand-list',
  templateUrl: './page-demand-list.component.html',
  styleUrls: ['./page-demand-list.component.scss'],
})
export class PageDemandListComponent implements OnInit, OnDestroy {
  demandTableDatasource: ScopedDemandListTable[] = [];
  demandTableColumns: string[] = [
    'no',
    'project',
    'jobDescription',
    'qty',
    'experienceLevel',
    'deadLine',
    'resources',
    'status',
    'active',
    'createdAt',
    'actions',
  ];
  demandListCriteria: ScopedDemandListFilterCriteria = {
    ...sharedDefaultListCriteriaDataConstant,
    isDeleted: false,
    sortByField: 'deadLine',
  };
  totalDemands = 0;

  @ViewChild(MatSort, { static: true }) matSort: MatSort;
  switchPinningDemand$ = new Subject<{ id: number; checked: boolean }>();
  #subscription = new Subscription();

  constructor(
    private scopedDemandService: ScopedServiceDemandService,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
    private awAddResourceToDemandPopupFragmentService: AwAddResourceToDemandPopupFragmentService,
    private candidateService: CandidateService,
    private router: Router,
  ) {}

  // -----------------------------------------------------------------------------------------------------
  // @ Life cycle hooks
  // -----------------------------------------------------------------------------------------------------

  ngOnInit(): void {
    if (localStorage.getItem('demand-list')) {
      this.demandListCriteria = JSON.parse(localStorage.getItem('demand-list'));
    }

    this.subscribeDemandTableMatSort();
    this.subscribeSwitchPinningDemand();
    this.fetchDemands().then();
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Fetching APIs
  // -----------------------------------------------------------------------------------------------------
  async fetchDemands(): Promise<void> {
    localStorage.setItem(
      'demand-list',
      JSON.stringify(this.demandListCriteria),
    );

    try {
      // Fetch demands using the demand service
      let httpParams = new HttpParams();
      if (this.demandListCriteria) {
        Object.keys(this.demandListCriteria).forEach(
          (filterKey) =>
            (httpParams = httpParams.set(
              filterKey,
              this.demandListCriteria[filterKey],
            )),
        );
      }
      const demandListResponse = await this.scopedDemandService
        .getAllDemands(httpParams)
        .toPromise();

      // Map and update demands for the table datasource
      this.demandTableDatasource = demandListResponse.contents.map((item, itemIndex) =>
        this.mapToDemandTableListByAddNoField(item, demandListResponse, itemIndex),
      );
      this.totalDemands = demandListResponse.total;
    } catch (error) {
      this.showErrorMessage(
        'Something went wrong. Cannot communicate with the server.',
      );
    }
  }

  async requestPinDemandToDashboard(
    id: number,
    checked: boolean,
  ): Promise<void> {
    try {
      await this.scopedDemandService.pinOrUnpinDemandOnDashboard(id, checked);
      this.showSuccessMessage('Demand status was updated successfully.');
      await this.fetchDemands();
    } catch (error) {
      this.showErrorMessage(
        'Something went wrong. Cannot communicate with the server.',
      );
    }
  }

  async fetchDemandByIsDeleteHasFalse(): Promise<void> {
    this.demandListCriteria.isDeleted = !this.demandListCriteria.isDeleted;
    this.demandListCriteria.page = 1;
    await this.fetchDemands();
  }

  async moveDemandFromListToArchive(rowId: number): Promise<void> {
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
        await this.scopedDemandService.moveDemandFromListToArchive(rowId);
        this.showSuccessMessage(
          'The demands has been added to archive successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

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
        await this.scopedDemandService.deleteDemandPermanent(id);
        this.showSuccessMessage('The demands has been delete successfully.');
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  async pageChangeEvent(page: AwPaginationModel) {
    this.demandListCriteria.page = page.pageIndex;
    await this.fetchDemands();
  }

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
        await this.scopedDemandService.restoreDemandFromArchiveToList(
          id,
          false,
        );
        this.showSuccessMessage('The demands has been restored successfully.');
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
  }

  async fetchDemandByFilter(filter: string): Promise<void> {
    this.demandListCriteria = {
      ...this.demandListCriteria,
      filter,
    };
    await this.fetchDemands();
  }

  async addOrRemoveResourceFromProject(
    nbCandidates: number[],
    demandId: number,
    requiredDemand: number,
  ): Promise<void> {
    const httpParams = new HttpParams()
      .set('page', '1')
      .set('pageSize', '0')
      .set('sortByField', 'firstname')
      .set('sortDirection', 'asc');
    const resources = await this.candidateService
      .getAvailableResource(httpParams, demandId);
    const resourceDatasource: KeyValue<string, string>[] =
      resources.contents.map((resource) => ({
        key: `${resource.id}`,
        value: `${resource.firstname} ${resource.lastname.toUpperCase()}`,
      }));

    const selectedSources = nbCandidates.map((candidate) =>
      candidate.toString(),
    );

    const candidateIds = await this.awAddResourceToDemandPopupFragmentService
      .chooseResource(resourceDatasource, selectedSources, requiredDemand)
      .toPromise();

    if (candidateIds) {
      try {
        await this.scopedDemandService
          .addResourceToDemand({
            candidateIds,
            demandId,
          })
          .toPromise();
        this.showSuccessMessage(
          'The resource has been added to project successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchDemands();
      }
    }
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

  // -----------------------------------------------------------------------------------------------------
  // @ Table adjustment
  // -----------------------------------------------------------------------------------------------------
  getWarningDate(
    deadline: number,
    status: boolean,
  ): 'primary' | 'danger' | 'warning' | 'success' {
    const daysToDeadline = -days(deadline);
    const overdueDeadline = !isDeadline(deadline);

    if (status !== undefined && !status) {
      return 'success';
    }
    if (overdueDeadline && daysToDeadline !== 0) {
      return 'danger';
    } else if (daysToDeadline <= 5 || daysToDeadline === 0) {
      return 'warning';
    } else {
      return 'primary';
    }
  }

  navigateToPageCreateDemand(): void {
    this.router.navigateByUrl(appRoute.awDemands.create).then();
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Navigations
  // -----------------------------------------------------------------------------------------------------
  navigatePageDemandDetails(projectId: number): void {
    this.router.navigateByUrl(`${appRoute.awDemands.view}/${projectId}`).then();
  }

  navigateToViewDemandPage(row: ScopedDemandListTable): void {
    this.router.navigateByUrl(`${appRoute.awDemands.view}/${row.id}`).then();
  }

  navigateToEditDemandPage(row: ScopedDemandListTable): void {
    this.router.navigateByUrl(`${appRoute.awDemands.edit}/${row.id}`).then();
  }



  // -----------------------------------------------------------------------------------------------------
  // @ Utility methods
  // -----------------------------------------------------------------------------------------------------
  private mapToDemandTableListByAddNoField(
    item: ScopedDemandListItem,
    demands: EntityResponseHandler<ScopedDemandListItem>,
    itemIndex: number,
  ): ScopedDemandListTable {
    const no =
      demands.page * demands.pageSize + itemIndex + 1 - demands.pageSize;
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
  // -----------------------------------------------------------------------------------------------------
  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private subscribeDemandTableMatSort(): void {
    // Set initial sorting criteria
    this.matSort.active = this.demandListCriteria.sortByField || 'project';
    this.matSort.direction = this.demandListCriteria.sortDirection || 'desc';

    // Subscribe to sort changes
    const subscription: Subscription = this.matSort.sortChange
      .pipe(debounceTime(300))
      .subscribe((sort: Sort) => {
        // Update sorting criteria
        this.demandListCriteria = {
          ...this.demandListCriteria,
          sortByField: sort.active,
          sortDirection: sort.direction,
        };
        // Fetch demands with updated sort criteria
        this.fetchDemands().then();
      });
    this.#subscription.add(subscription);
  }

  private subscribeSwitchPinningDemand(): void {
    const subscription: Subscription = this.switchPinningDemand$
      .pipe(debounceTime(200))
      .subscribe((response): void => {
        this.requestPinDemandToDashboard(response.id, response.checked).then();
      });
    this.#subscription.add(subscription);
  }

  // -----------------------------------------------------------------------------------------------------
  // @ Listeners
  // -----------------------------------------------------------------------------------------------------
  @HostListener('window:beforeunload', ['$event'])
  onPageRefresh(): void {
    localStorage.removeItem('demand-list');
  }
}
