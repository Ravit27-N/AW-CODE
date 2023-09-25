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
import { AddCandidateStepTwoValidator } from './add-candidate-step-two.validator';
import { Education } from './add-candidate-step-two.model';
import { universityGraduate } from './add-candidate-step-two.constant';
import { CandidateService, UniversityService } from '../../../core';
import { AwSnackbarService } from '../../../shared';
import { Subscription } from 'rxjs';
import { KeyValue } from '@angular/common';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-add-candidate-step-two',
  templateUrl: './add-candidate-step-two.component.html',
  styleUrls: ['./add-candidate-step-two.component.scss'],
})
export class AddCandidateStepTwoComponent implements OnInit, OnDestroy {
  @Input() candidateStep2: { education: Education[]; form: any } = {
    form: {},
    education: [],
  };
  @Output() goPreviousPageEvent = new EventEmitter<void>();
  @Output() goNextPageEvent = new EventEmitter<void>();
  @Output() validateStep = new EventEmitter<void>();

  formGroup: FormGroup;
  formEdit = !!this.activateRoute.snapshot.params.id;
  universityCriteria = [];
  degreeTyeCriteria: KeyValue<any, any>[] = [];
  universityGraduate = universityGraduate;
  educations: Education[] = [];
  shouldShowError = false;
  subscription: Subscription = new Subscription();

  constructor(
    private formBuilder: FormBuilder,
    private universityService: UniversityService,
    private candidateService: CandidateService,
    private awSnackbarService: AwSnackbarService,
    private activateRoute: ActivatedRoute,
  ) {}

  async ngOnInit(): Promise<void> {
    this.setupFormGroup();
    await this.fetchUniversitiesCriteria();
    await this.fetchDegreeType();
    this.listenFormChange();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private listenFormChange(): void {
    const subscription = this.formGroup.valueChanges.subscribe(() => {
      this.shouldShowError = false;
      this.setDataToLocalstorage();

      if (this.formEdit) {
        localStorage.setItem('candidate-form-has-change', 'true');
      }
    });
    this.subscription.add(subscription);
  }

  async fetchDegreeType(): Promise<void> {
    const degreeTypeResponse = await this.candidateService
      .getUniversityDegreeTypes()
      .toPromise();
    this.degreeTyeCriteria = degreeTypeResponse.map(degree => {
      return {
        key: degree,
        value: degree,
      };
    });
  }

  async fetchUniversitiesCriteria(): Promise<void> {
    const universities = await this.universityService
      .getList(1, 100, '', 'asc', 'name')
      .toPromise();

    this.universityCriteria = universities.contents.map((university) => ({
      key: university.id,
      value: university.name,
    }));
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

    if (this.educations.length === 0) {
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: 'The candidate needs at least one education.',
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

    const newEducation: Education = {
      id: 0,
      gpa: 1,
      universityId: 0,
      remarks: '',
      graduate: false,
      academicYearStart: null,
      academicYearEnd: null,
      major: '',
      degree: null,
    };
    this.educations.push(newEducation);
    this.patchFormValue(newEducation, this.educations.length - 1);
  }

  removeEducation(index: number): void {
    this.educations.splice(index, 1);
    this.resetFormGroup();
    this.validateStep.emit();
  }

  patchFormValue(education: Education, educationIndex: number): void {
    const {
      academicYearStart,
      academicYearEnd,
      graduate,
      gpa,
      remarks,
      universityId,
      major,
      degree,
    } = education;
    this.formGroup.patchValue(
      {
        order: educationIndex,
        universityId,
        academicYearStart,
        academicYearEnd,
        graduate,
        gpa,
        remarks,
        major,
        degree,
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

    this.educations = this.educations.map((education, educationIndex) => {
      if (data.order !== educationIndex) {
        return education;
      }

      return {
        id: data.order,
        remarks: data.remarks,
        gpa: data.gpa,
        graduate: Boolean(data.graduate),
        academicYearStart: data.academicYearStart,
        academicYearEnd: data.academicYearEnd,
        universityId: data.universityId,
        major: data.major,
        degree: data.degree,
      };
    });

    this.formGroup.reset();
  }

  editEducation(educationIndex: number): void {
    const currentIndex = this.formGroup.getRawValue().order;
    if (currentIndex === educationIndex) {
      return;
    }

    const education = this.educations[educationIndex];
    this.patchFormValue(education, educationIndex);
  }

  getUniversityName(id: number): string {
    return (
      this.universityCriteria.find((university) => university.key === id)
        ?.value || ''
    );
  }

  orderItem($event: any): void {
    const { previousIndex, currentIndex } = $event;

    const collections = this.educations;

    moveItemInArray(collections, previousIndex, currentIndex);
    this.educations = collections;
    this.formGroup.patchValue(this.formGroup.getRawValue());
  }

  private setupFormGroup(): void {
    this.educations = this.candidateStep2?.education || [];
    this.formGroup = this.formBuilder.group({
      order: new FormControl(),
      universityId: new FormControl(null, [
        AddCandidateStepTwoValidator.fieldUniversity(),
      ]),
      academicYearStart: new FormControl('', [
        AddCandidateStepTwoValidator.fieldAcademicYearStart(),
      ]),
      academicYearEnd: new FormControl('', [
        AddCandidateStepTwoValidator.fieldAcademicYearEnd(),
      ]),
      graduate: new FormControl('true', [
        AddCandidateStepTwoValidator.fieldGraduate(),
      ]),
      gpa: new FormControl(0, [AddCandidateStepTwoValidator.fieldGPA()]),
      remarks: new FormControl('', [
        AddCandidateStepTwoValidator.fieldRemarks(),
      ]),
      major: new FormControl('', [AddCandidateStepTwoValidator.fieldMajor()]),
      degree: new FormControl('', [AddCandidateStepTwoValidator.fieldDegree()]),
    });

    if (this.candidateStep2?.form) {
      this.formGroup.patchValue(this.candidateStep2.form, {
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
      major: '',
      degree: null,
    });
  }

  private setDataToLocalstorage(): void {
    localStorage.setItem(
      'candidate-form-step-2',
      JSON.stringify({
        education: this.educations,
        form: this.formGroup.getRawValue(),
      }),
    );
  }

  getIsActive(index: number) {
    return this.formGroup.getRawValue()?.order === index;
  }

  private validateDate(): boolean {
    const { academicYearStart, academicYearEnd } =
      this.formGroup.getRawValue();

    if (
      academicYearEnd &&
      new Date(academicYearStart) > new Date(academicYearEnd)
    ) {
      this.formGroup
        .get('academicYearStart')
        .setErrors({ message: 'The start date must be older than end date!' });
      this.shouldShowError = true;
      return false;
    } else {
      this.formGroup.get('academicYearStart').setErrors(null);
    }

    return true;
  }
}
