import {
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import {
  comparedDateNow,
  DemandModel,
  DemandService,
  IEnvelope,
  JobDescription,
  JobDescriptionService,
  ListProjectModel,
  ProjectService,
} from '../../../../core';
import { Subscription } from 'rxjs';
import { Location } from '@angular/common';
import { IsLoadingService } from '@service-work/is-loading';
import { AwSnackbarService } from '../../../../shared';
import {
  debounceTime,
  distinctUntilChanged,
  startWith,
  switchMap,
} from 'rxjs/operators';
import { ScopedModelDemandDetails } from '../../models/scoped-model-demand-details.model';

@Component({
  selector: 'app-scoped-component-demand-create-edit-form',
  templateUrl: './scoped-component-demand-create-edit-form.component.html',
  styleUrls: ['./scoped-component-demand-create-edit-form.component.scss'],
})
export class ScopedComponentDemandCreateEditFormComponent
  implements OnInit, OnDestroy, OnChanges
{
  form: FormGroup;
  project: DemandModel;
  demand = [
    'Position',
    'Project',
    'Amount of requirement',
    'Experience level',
    'Deadline',
    'Pin to dashboard',
  ];
  projects: ListProjectModel;
  jobDescriptions: IEnvelope<JobDescription>;
  enableNewProject: boolean;
  enableNewPosition: boolean;
  slideValue = 'Active';
  projectCtrl = new FormControl();
  titleDescriptionCtrl = new FormControl();
  projectSubscription: Subscription;
  positionSubscription: Subscription;

  @Input() editMode: boolean;
  @Input() editData: ScopedModelDemandDetails;

  constructor(
    private formBuilder: FormBuilder,
    private projectService: ProjectService,
    private jobDescriptionService: JobDescriptionService,
    private isLoadingService: IsLoadingService,
    private demandService: DemandService,
    private location: Location,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    this.fetchProject();
    this.fetchPosition();
    if (!this.editMode) {
      this.initFormData();
    }
  }

  ngOnDestroy(): void {
    this.projectSubscription.unsubscribe();
    this.positionSubscription.unsubscribe();
  }

  ngOnChanges(simpleChanges: SimpleChanges): void {
    if (simpleChanges?.editMode?.currentValue) {
      this.initEditFormData();
    }
  }

  initFormData(): void {
    this.form = this.formBuilder.group({
      jobDescriptionId: [null],
      projectId: [null],
      jobDescription: '',
      experienceLevel: '',
      deadLine: [null],
      active: true,
      status: true,
      nbRequired: [null],
    });
  }

  initEditFormData(): void {
    this.form = this.formBuilder.group({
      id: this.editData.id,
      projectId: [this.editData.project?.id],
      jobDescriptionId: [this.editData.jobDescription?.id],
      nbRequired: [this.editData.nbRequired],
      experienceLevel: [this.editData.experienceLevel],
      deadLine: [new Date(this.editData.deadLine)],
      createdAt: this.editData.createdAt,
      active: [true],
    });
  }

  fetchPosition(): void {
    this.positionSubscription = this.titleDescriptionCtrl.valueChanges
      .pipe(startWith(''), debounceTime(300))
      .pipe(
        distinctUntilChanged(),
        switchMap((search: string) =>
          this.jobDescriptionService.get(1, 100000, search),
        ),
      )
      .subscribe((data) => (this.jobDescriptions = data));
  }

  fetchProject(): void {
    this.projectSubscription = this.projectCtrl.valueChanges
      .pipe(startWith(''), debounceTime(300))
      .pipe(
        distinctUntilChanged(),
        switchMap((search: string) =>
          this.projectService.getList(1, 100000, search, false, 'asc', 'name'),
        ),
      )
      .subscribe((data) => (this.projects = data));
  }

  getDemand(item: string): void {
    if (this.demand[0] !== item && this.demand[1] !== item) {
      this.enableNewProject = false;
      this.enableNewPosition = false;
    }
  }

  newProject(): void {
    this.enableNewProject = true;
    this.enableNewPosition = false;
    this.hiddenInputSearchBox();
  }

  hiddenInputSearchBox(): void {
    const element = document.querySelector('.mat-select-search-panel');
    element.setAttribute('style', 'display: none !important;');
  }

  saveProject(event: any) {
    this.fetchProject();
    this.enableNewProject = !event;
  }

  cancelProject(event: any) {
    this.enableNewProject = !event;
  }

  newPosition(): void {
    this.enableNewPosition = true;
    this.enableNewProject = false;
    this.hiddenInputSearchBox();
  }

  savePosition(event: any) {
    this.fetchPosition();
    this.enableNewPosition = !event;
  }

  cancelPosition(event: any) {
    this.enableNewPosition = !event;
  }

  submitDemand(): void {
    if (this.editMode) {
      this.editDemand();
    } else {
      this.saveDemand();
    }
  }

  saveDemand(): void {
    if (this.validateFormData()) {
      this.setValidateFormError();
      return;
    }
    this.demandService.create(this.form.value).subscribe(
      () => {
        this.showSuccessMessage('Add Demand Success');
        this.back();
      },
      () => {
        const { nbRequired } = this.form.getRawValue();
        if (nbRequired < 1) {
          this.alertSnackbarMessage(
            'Amount of requirement cannot smaller than 0',
          );
        } else {
          this.alertSnackbarMessage(
            'Duplicate with projectName and Job DescriptionName',
          );
        }
      },
    );
  }

  validateFormData(): boolean {
    return (
      this.form.invalid ||
      !this.form.get('nbRequired').value ||
      this.form.get('nbRequired').value < 1 ||
      !this.form.get('jobDescriptionId').value ||
      !this.form.get('projectId').value ||
      !this.form.get('experienceLevel').value ||
      !this.form.get('deadLine').value ||
      !comparedDateNow(this.form.get('deadLine').value)
    );
  }

  setValidateFormError(): void {
    const incorrect = { incorrect: true };
    if (
      !this.form.get('deadLine').value ||
      !comparedDateNow(this.form.get('deadLine').value)
    ) {
      this.form.controls.deadLine.setErrors(incorrect);
    }
    if (
      !this.form.get('nbRequired').value ||
      this.form.get('nbRequired').value < 1
    ) {
      this.form.controls.nbRequired.setErrors(incorrect);
    }
    if (!this.form.get('jobDescriptionId').value) {
      this.form.controls.jobDescriptionId.setErrors(incorrect);
    }
    if (!this.form.get('projectId').value) {
      this.form.controls.projectId.setErrors(incorrect);
    }
    if (!this.form.get('experienceLevel').value) {
      this.form.controls.experienceLevel.setErrors(incorrect);
    }
  }

  editDemand(): void {
    if (this.validateFormData()) {
      this.setValidateFormError();
      return;
    }
    this.demandService
      .validateUpdateProjectJob(
        this.form.get('projectId').value,
        this.form.get('jobDescriptionId').value,
        this.editData.id,
      )
      .subscribe((response) => {
        if (response === 0) {
          const subscription = this.demandService
            .update(this.editData.id, this.form.value)
            .subscribe(() => {
              this.showSuccessMessage('Edit Demand Success');
            });
          this.isLoadingService.add(subscription, {
            key: 'EditDemandComponent',
            unique: 'EditDemandComponent',
          });
          this.back();
        } else {
          this.alertSnackbarMessage(
            'Duplicate with projectName and Job DescriptionName',
          );
        }
      });
  }

  private showSuccessMessage(message: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'success',
      icon: 'close',
      message,
    });
  }

  private alertSnackbarMessage(errorMessage: string): void {
    this.awSnackbarService.openCustomSnackbar({
      type: 'error',
      icon: 'close',
      message: errorMessage,
    });
  }

  back(): void {
    this.location.back();
  }

  discardFormAndNavigateToList(): void {
    if (this.editMode) {
      this.initEditFormData();
    } else {
      this.initFormData();
    }
    this.back();
  }

  pinOrUnpinDemandToDashboard(): void {
    const pinToDashboard = this.form.get('active').value === false;
    this.slideValue = pinToDashboard ? 'Active' : 'Inactive';
  }

  getDemandTitle(): string {
    return this.editMode ? 'Edit Demand' : 'Add Demand';
  }
}
