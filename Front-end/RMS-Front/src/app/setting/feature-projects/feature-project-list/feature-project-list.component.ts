import { Component, OnInit } from '@angular/core';
import { ProjectCriteria, ProjectModel, ProjectService } from '../../../core';
import { MatTableDataSource } from '@angular/material/table';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';
import { PageEvent } from '@angular/material/paginator';
import { Sort } from '@angular/material/sort';

@Component({
  selector: 'app-feature-project-list',
  templateUrl: './feature-project-list.component.html',
  styleUrls: ['./feature-project-list.component.scss'],
})
export class FeatureProjectListComponent implements OnInit {
  displayedColumns: string[] = [
    'no',
    'name',
    'description',
    'createdAt',
    'action',
  ];

  filterListCriteria: ProjectCriteria = {
    defaultCriteria: {
      pageIndex: 1,
      pageSize: 10,
      sortByField: 'createdAt',
      sortDirection: 'desc',
    },
    filter: '',
    isArchive: false,
  };

  dataSource = new MatTableDataSource<ProjectModel>();
  totalProjects = 0;

  constructor(
    private projectService: ProjectService,
    private awConfirmMessageService: AwConfirmMessageService,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    this.fetchProjects();
  }

  async deletePermanent(data: ProjectModel): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Remove project',
        message:
          'Are you sure you want to remove this project?\nThis action is irreversible.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.projectService.hardDelete(Number(data.id)).toPromise();
        this.showSuccessMessage(
          'The project has been added to archive successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchProjects();
      }
    }
  }

  async addToArchive(rowId: number): Promise<void> {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Archive project',
        message: 'Are you sure that you want to add this project to archive?',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.projectService.softDelete(rowId).toPromise();
        this.showSuccessMessage(
          'The project has been added to archive successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchProjects();
      }
    }
  }

  fetchArchiveProject(): void {
    this.filterListCriteria.isArchive = !this.filterListCriteria.isArchive;
    this.fetchProjects();
  }

  fetchProjects() {
    console.log(this.filterListCriteria);
    this.projectService
      .getList(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.filterListCriteria.filter,
        this.filterListCriteria.isArchive,
        this.filterListCriteria.sortDirection,
        this.filterListCriteria.sortByField,
      )
      .toPromise()
      .then((response) => {
        this.dataSource = new MatTableDataSource(response.contents);
        this.totalProjects = response.total;
      });
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.fetchProjects();
  }

  getRowData(candidateField: any) {
    return { row: candidateField };
  }

  searchFilterChange(filter: string): void {
    this.filterListCriteria.filter = filter;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchProjects();
  }

  sortByProjectList(sort: Sort): void {
    this.filterListCriteria.sortDirection = sort.direction;
    this.filterListCriteria.sortByField = sort.active;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchProjects();
  }

  addProject(): void {
    console.log('add project');
  }

  async restoreProject(row: ProjectModel) {
    const confirmed = await this.awConfirmMessageService
      .showConfirmationPopup({
        type: 'Warning',
        icon: 'close',
        title: 'Restore project',
        message:
          'Are you sure to restore this project?\nIf Okay, this will also restore records that are related to this.',
        cancelButton: 'Cancel',
        confirmButton: 'Ok',
      })
      .toPromise();

    if (confirmed) {
      try {
        await this.projectService.restore(row.id, false).toPromise();
        this.showSuccessMessage('The project has been restored successfully.');
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        await this.fetchProjects();
      }
    }
  }

  editProject(row: ProjectModel): void {
    console.log({ row });
  }

  private showErrorMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message,
    });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  getPagination(index: number) {
    return (
      (this.filterListCriteria.defaultCriteria.pageIndex - 1) *
        this.filterListCriteria.defaultCriteria.pageSize +
      (index + 1)
    );
  }

  getDateFormat(createdAt: string) {
    return new Date(createdAt);
  }
}
