import { DialogViewJobComponent } from '../view-job';
import { JobModel, JobService } from '../../../core';
import { MatDialog } from '@angular/material/dialog';
import { ProjectCriteria } from 'src/app/core';
import { MatSort, Sort } from '@angular/material/sort';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AwConfirmMessageService, AwSnackbarService } from '../../../shared';

@Component({
  selector: 'app-feature-list-job',
  templateUrl: './feature-list-job.component.html',
  styleUrls: ['./feature-list-job.component.scss'],
})
export class FeatureListJobComponent implements OnInit {
  tableColumns: string[] = ['No', 'title', 'description', 'status', 'action'];
  filterListCriteria: ProjectCriteria = {
    defaultCriteria: {
      pageIndex: 1,
      pageSize: 10,
      sortByField: 'title',
      sortDirection: 'asc',
    },
    filter: '',
    startDate: '',
    endDate: '',
    status: [],
    option: 0,
  };
  totalJobs = 0;
  dataSource: MatTableDataSource<JobModel>;
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    public dialog: MatDialog,
    private jobService: JobService,
    private awSnackbarService: AwSnackbarService,
    private awConfirmMessageService: AwConfirmMessageService,
  ) {}

  ngOnInit(): void {
    this.fetchJobs();
  }

  fetchJobs(): void {
    this.jobService
      .getlist(
        this.filterListCriteria.defaultCriteria.pageIndex,
        this.filterListCriteria.defaultCriteria.pageSize,
        this.filterListCriteria.filter,
        this.filterListCriteria.defaultCriteria.sortDirection,
        this.filterListCriteria.defaultCriteria.sortByField,
      )
      .toPromise()
      .then((response) => {
        this.dataSource = new MatTableDataSource(response.contents);
        this.totalJobs = response.total;
      })
      .catch(() => {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      });
  }

  async deleteJob(row: JobModel): Promise<void> {
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
        await this.jobService.delete(row.id).toPromise();
        this.showSuccessMessage(
          'The project has been added to archive successfully.',
        );
      } catch (error) {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      } finally {
        this.fetchJobs();
      }
    }
  }

  openDialog(row: any): void {
    this.dialog.open(DialogViewJobComponent, {
      data: row,
      width: '40%',
    });
  }

  add(): void {
    console.log('add');
  }

  edit(row: JobModel): void {
    console.log({ row });
  }

  updateStatus(id: number, active: any): void {
    this.jobService
      .changeStatus(id, !active)
      .toPromise()
      .then(() => {
        this.showSuccessMessage(
          'The job status has been updated successfully.',
        );
      })
      .catch(() => {
        this.showErrorMessage(
          'Something went wrong. Cannot communicate with the server.',
        );
      }).finally(() => {
        this.fetchJobs();
    });
  }

  getFile(row) {
    this.jobService.fileView(row.id, row.filename).subscribe(
      (res) => {
        const blob = new Blob([res], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      () => {},
    );
  }

  searchFilterChange(filerString: string) {
    this.filterListCriteria.filter = filerString;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchJobs();
  }

  getJobRow(candidateField: any) {
    return { row: candidateField };
  }

  sortJobs(sort: Sort): void {
    this.filterListCriteria.defaultCriteria.sortDirection = sort.direction;
    this.filterListCriteria.defaultCriteria.sortByField = sort.active;
    this.filterListCriteria.defaultCriteria.pageIndex = 1;
    this.fetchJobs();
  }

  pageEvent(event: PageEvent): void {
    this.filterListCriteria.defaultCriteria.pageIndex = event.pageIndex;
    this.filterListCriteria.defaultCriteria.pageSize = event.pageSize;
    this.fetchJobs();
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
}
