import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UserProfileUtil, UserUtil } from '@cxm-smartflow/shared/data-access/services';
import { appRoute, DirectoryManagement } from '@cxm-smartflow/shared/data-access/model';
import { Store } from '@ngrx/store';
import {
  DefinitionDirectoryListType,
  DefinitionDirectoryResponseType,
  DefinitionDirectoryService,
  deleteDirectory,
  fetchDirectoryDefinition,
  selectDefinitionDirectoryTableCriteria,
  selectDefinitionDirectoryTableDatasource
} from '@cxm-smartflow/definition-directory/data-access';
import { Subscription } from 'rxjs';
import { MatSort, Sort } from '@angular/material/sort';
import { SortDirection } from '@angular/material/sort/sort-direction';
import { ConfirmationMessageService } from '@cxm-smartflow/shared/ui/comfirmation-message';
import { TranslateService } from '@ngx-translate/core';
import { BlobFile } from '@cxm-smartflow/shared/utils';
import { Router } from '@angular/router';

interface DefinitionTableDatasource {
  id: number;
  display_name: string;
  modifiedAt: Date;
  createdAt: Date;
  _modifiable: boolean;
  _downloadable: boolean;
  _deletable: boolean;
}

interface DefinitionTableCriteria {
  page: number;
  pageSize: number;
  total?: number;
  sortByField: 'name' | 'lastModified' | 'createdAt';
  sortDirection: SortDirection;
}

@Component({
  selector: 'cxm-smartflow-feature-list-directories',
  templateUrl: './feature-list-directories.component.html',
  styleUrls: ['./feature-list-directories.component.scss'],
})
export class FeatureListDirectoriesComponent implements OnInit, OnDestroy {
  definitionCreatable: boolean = UserProfileUtil.canAccess(
    DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
    DirectoryManagement.CREATE_DEFINITION_DIRECTORY,
    true
  );
  definitionTableColumns: string[] = [
    'name',
    'lastModified',
    'createdAt',
    'actions',
  ];
  definitionTableDatasource: DefinitionTableDatasource[] = [];
  definitionTableCriteria: DefinitionTableCriteria = {
    page: 1,
    pageSize: 10,
    total: 0,
    sortByField: 'lastModified',
    sortDirection: 'desc',
  };
  @ViewChild(MatSort, { static: true }) matSort: MatSort;

  #subscription: Subscription = new Subscription();

  constructor(private store$: Store,
              private translateService: TranslateService,
              private directoryService: DefinitionDirectoryService,
              private router: Router,
              private confirmMessageService: ConfirmationMessageService) {}

  ngOnInit(): void {
    this.subscribeDefinitionTableMatSort();
    this.subscribeDefinitionTableCriteria();
    this.subscribeDefinitionTableDatasource();
    this.fetchDirectory();
    localStorage.removeItem('definitionDirectoryForm');
    localStorage.removeItem('definitionDirectoryFormEditor');
  }

  ngOnDestroy(): void {
    this.#subscription.unsubscribe();
  }

  private subscribeDefinitionTableMatSort(): void {
    this.matSort.active = 'lastModified';
    this.matSort.direction = 'desc';

    const subscription: Subscription = this.matSort.sortChange.subscribe((sort: Sort): void => {
      const criteria: any = {
        ...this.definitionTableCriteria,
        sortByField: sort.active,
        sortDirection: sort.direction,
      };

      this.definitionTableCriteria = criteria;
      this.fetchDirectory();
    });
    this.#subscription.add(subscription);
  }

  private subscribeDefinitionTableCriteria(): void {
    const subscription: Subscription = this.store$
      .select(selectDefinitionDirectoryTableCriteria)
      .subscribe((tableCriteria): void => {
        this.definitionTableCriteria = {
          ...this.definitionTableCriteria,
          page: tableCriteria.page,
          pageSize: tableCriteria.pageSize,
          sortByField: tableCriteria.sortByField,
          sortDirection: tableCriteria.sortDirection,
        };
      });
    this.#subscription.add(subscription);
  }

  private subscribeDefinitionTableDatasource(): void {

    const UserProfileInstance = UserProfileUtil.getInstance();

    const subscription: Subscription = this.store$
      .select(selectDefinitionDirectoryTableDatasource)
      .subscribe((datasource: DefinitionDirectoryListType): void => {
        this.definitionTableDatasource = datasource.contents.map((row) => {
          const canModification = this.canModify(UserProfileInstance, row);
          return {
            id: row.id,
            display_name: row.displayName,
            createdAt: row.createdAt,
            modifiedAt: row.lastModified,
            _downloadable: UserProfileUtil.canAccess(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT, DirectoryManagement.LIST_DEFINITION_DIRECTORY, true),
            _modifiable: canModification,
            _deletable: this.canDelete(UserProfileInstance, row),
            _viewable: this.canView(UserProfileInstance, row, canModification)
          };
        });
        this.definitionTableCriteria = {
          ...this.definitionTableCriteria,
          page: datasource.page,
          pageSize: datasource.pageSize,
          total: datasource.total,
        };
      });
    this.#subscription.add(subscription);
  }

  canDelete(userProfile: UserProfileUtil, row: DefinitionDirectoryResponseType) {
    if (UserUtil.isAdmin()) {
      return true;
    } else {
      if (row.isDirectoryShared) {
        return false;
      } else {
        return userProfile.canModify({
          func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
          priv: DirectoryManagement.DELETE_DEFINITION_DIRECTORY,
          checkAdmin: false,
          ownerId: row.ownerId
        });
      }
    }
  }

  canModify(userProfile: UserProfileUtil, row: DefinitionDirectoryResponseType) {
    if (UserUtil.isAdmin()) {
      return true;
    } else {
      if (row.isDirectoryShared) {
        return false;
      } else {
        return userProfile.canModify({
          func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
          priv: DirectoryManagement.MODIFY_DEFINITION_DIRECTORY,
          checkAdmin: false,
          ownerId: row.ownerId
        });
      }
    }
  }

  canView(userProfile: UserProfileUtil, row: DefinitionDirectoryResponseType, canModify: boolean): boolean {
    if (UserUtil.isAdmin()) {
      return false;
    } else {
      if (row.isDirectoryShared) {
        return true;
      }
      const canEdit = userProfile.canVisibility({
        func: DirectoryManagement.CXM_DIRECTORY_MANAGEMENT,
        priv: DirectoryManagement.EDIT_DEFINITION_DIRECTORY,
        checkAdmin: false,
        ownerId: row.ownerId
      });
      if (canEdit && !canModify) {
        return true;
      }
      if (!canModify) {
        return UserProfileUtil.canAccess(DirectoryManagement.CXM_DIRECTORY_MANAGEMENT, DirectoryManagement.EDIT_DEFINITION_DIRECTORY, false);
      }
      return false;
    }
  }

  async downloadStructureFileEvent(id: number): Promise<void> {
    const blobFile = await this.directoryService.downloadStructureFile(id).toPromise();
    const file: File = BlobFile.blobToFile(blobFile.file, `${blobFile.filename}.csv`);
    BlobFile.saveFile(file);
  }

  async deleteDirectoryEvent(id: number): Promise<void> {
    const { title, message, cancelButton, confirmButton } = await this.translateService.get('directory.definition_direction_delete').toPromise();
    const confirmed = await this.confirmMessageService.showConfirmationPopup({
      type: 'Warning',
      title,
      message,
      cancelButton,
      confirmButton,
    }).toPromise();


    if (confirmed) {
      this.store$.dispatch(deleteDirectory({ id }));
    }
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
    }

    this.fetchDirectory();
  }

  /**
   * Fetch directory definition.
   *
   */
  private fetchDirectory(): void {
    this.store$.dispatch(
      fetchDirectoryDefinition({
        page: this.definitionTableCriteria.page,
        pageSize: this.definitionTableCriteria.pageSize,
        sortByField: this.definitionTableCriteria.sortByField,
        sortDirection: this.definitionTableCriteria.sortDirection,
      })
    );
  }

  modifyDirectory(id: number): void {
    this.router.navigate([appRoute.cxmDirectory.navigateToEditDefinitionDirectory], { queryParams: { id } });
  }

  viewDirectory(id: number): void {
    if(id){
      this.router.navigate([appRoute.cxmDirectory.navigateToViewDefinitionDirectory], { queryParams: { id } });
    }
  }
}
