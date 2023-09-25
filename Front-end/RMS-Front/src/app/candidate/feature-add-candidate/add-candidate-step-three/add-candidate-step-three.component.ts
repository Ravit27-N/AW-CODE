import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { Subscription } from 'rxjs';
import { Experience } from './add-candidate-step-three.model';
import { AwSnackbarService } from '../../../shared';
import { levelCriteria } from './add-candidate-step-three.constant';
import { AddCandidateStepThreeValidator } from './add-candidate-step-three.validator';
import { CandidateService } from '../../../core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-candidate-step-three',
  templateUrl: './add-candidate-step-three.component.html',
  styleUrls: ['./add-candidate-step-three.component.scss'],
})
export class AddCandidateStepThreeComponent implements OnInit, OnDestroy {
  @Input() candidateStep3: { experiences: Experience[]; form: any } = {
    form: {},
    experiences: [],
  };
  @Output() goPreviousPageEvent = new EventEmitter<void>();
  @Output() goNextPageEvent = new EventEmitter<void>();
  @Output() validateStep = new EventEmitter<void>();

  formGroup: FormGroup;
  levelCriteria = levelCriteria;
  experiences: Experience[] = [];
  shouldShowError = false;
  subscription: Subscription = new Subscription();
  formEdit = !!this.activateRoute.snapshot.params.id;

  constructor(
    private formBuilder: FormBuilder,
    private awSnackbarService: AwSnackbarService,
    private candidateService: CandidateService,
    private activateRoute: ActivatedRoute,
  ) {}

  async ngOnInit(): Promise<void> {
    this.setupFormGroup();
    await this.fetchExperienceLevel();
    this.listenFormChange();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  async fetchExperienceLevel(): Promise<void> {
    const experienceLevels = await this.candidateService
      .getUniversityExperienceLevels()
      .toPromise();
    this.levelCriteria = experienceLevels.map((level) => {
      return {
        key: level,
        value: level,
      };
    });
  }

  goPrevious(): void {
    if (this.formGroup.get('order')?.value !== null) {
      this.shouldShowError = true;
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: 'Please save your daft before back to another step!',
      });
      return;
    }

    this.goPreviousPageEvent.emit();
  }

  goNext(): void {
    if (this.formGroup.get('order')?.value !== null) {
      this.shouldShowError = true;
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: 'Please save your daft before back to another step!',
      });
      return;
    }
    this.goNextPageEvent.emit();
  }

  addMoreField(): void {
    if (this.formGroup.get('order').value !== null) {
      if (this.formGroup.invalid) {
        this.shouldShowError = true;
        return;
      }

      this.applyChange();
    }

    const newExperience: Experience = {
      id: 0,
      level: null,
      position: '',
      technology: '',
      projectType: '',
      companyName: '',
      experienceEndDate: null,
      experienceStartDate: null,
    };
    this.experiences.push(newExperience);
    this.patchFormValue(newExperience, this.experiences.length - 1);
  }

  removeEducation(index: number): void {
    this.experiences.splice(index, 1);
    this.resetFormGroup();
    this.validateStep.emit();
  }

  patchFormValue(experienceElement: Experience, experienceIndex: number): void {
    const {
      level,
      technology,
      position,
      companyName,
      experienceStartDate,
      experienceEndDate,
      projectType,
    } = experienceElement;
    this.formGroup.patchValue(
      {
        order: experienceIndex,
        level,
        technology,
        position,
        companyName,
        experienceStartDate,
        experienceEndDate,
        projectType,
      },
      {
        emitEvent: false,
        onlySelf: false,
      },
    );

    this.setDataToLocalstorage();
  }

  applyChange(): void {
    const data = this.formGroup.getRawValue();

    if (data.order === null || !this.validateDate()) {
      return;
    }

    if (this.formGroup.invalid) {
      this.shouldShowError = true;
      return;
    }

    this.experiences = this.experiences.map((experience, educationIndex) => {
      if (data.order !== educationIndex) {
        return experience;
      }

      return {
        id: data.order,
        ...data,
      };
    });

    this.formGroup.reset();
  }

  editExperience(educationIndex: number): void {
    const currentIndex = this.formGroup.getRawValue().order;
    if (currentIndex === educationIndex) {
      return;
    }

    const education = this.experiences[educationIndex];
    this.patchFormValue(education, educationIndex);
  }

  orderItem($event: any): void {
    const { previousIndex, currentIndex } = $event;

    const collections = this.experiences;

    moveItemInArray(collections, previousIndex, currentIndex);
    this.experiences = collections;
    this.formGroup.patchValue(this.formGroup.getRawValue());
  }

  private listenFormChange(): void {
    const subscription: Subscription = this.formGroup.valueChanges.subscribe(
      () => {
        this.shouldShowError = false;
        this.setDataToLocalstorage();

        if (this.formEdit) {
          localStorage.setItem('candidate-form-has-change', 'true');
        }
      },
    );
    this.subscription.add(subscription);
  }

  private setupFormGroup(): void {
    this.experiences = this.candidateStep3?.experiences || [];
    this.formGroup = this.formBuilder.group({
      order: new FormControl(null),
      companyName: new FormControl('', [
        AddCandidateStepThreeValidator.fieldCompanyName(),
      ]),
      position: new FormControl('', [
        AddCandidateStepThreeValidator.fieldPosition(),
      ]),
      experienceStartDate: new FormControl('', [
        AddCandidateStepThreeValidator.fieldExperienceStartDate(),
      ]),
      experienceEndDate: new FormControl('', [
        AddCandidateStepThreeValidator.fieldExperienceEndDate(),
      ]),
      level: new FormControl(null, [
        AddCandidateStepThreeValidator.fieldLevel(),
      ]),
      projectType: new FormControl('', [
        AddCandidateStepThreeValidator.fieldProjectType(),
      ]),
      technology: new FormControl('', [
        AddCandidateStepThreeValidator.fieldTechnology(),
      ]),
    });

    if (this.candidateStep3?.form) {
      this.formGroup.patchValue(this.candidateStep3.form, {
        emitEvent: false,
        onlySelf: false,
      });
    }
  }

  private resetFormGroup(): void {
    this.formGroup.reset({
      order: null,
      universityId: null,
      academicYear: 1,
      graduate: false,
      gpa: 0,
      remarks: '',
    });
  }

  private setDataToLocalstorage(): void {
    localStorage.setItem(
      'candidate-form-step-3',
      JSON.stringify({
        experiences: this.experiences,
        form: this.formGroup.getRawValue(),
      }),
    );
  }

  getIsActive(index: number) {
    return this.formGroup.getRawValue()?.order === index;
  }

  private validateDate(): boolean {
    const { experienceStartDate, experienceEndDate } =
      this.formGroup.getRawValue();

    if (
      experienceEndDate &&
      new Date(experienceStartDate) > new Date(experienceEndDate)
    ) {
      this.formGroup
        .get('experienceStartDate')
        .setErrors({ message: 'The start date must be older than end date!' });
      this.shouldShowError = true;
      return false;
    } else {
      this.formGroup.get('experienceStartDate').setErrors(null);
    }

    return true;
  }
}
