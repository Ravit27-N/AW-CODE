import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Subscription } from 'rxjs';
import {
  genderCriteria,
  priorityCriteria,
  salutationCriteria,
} from './add-candidate-step-one.constant';
import { CandidateInformation } from './add-candidate-step-one.model';
import { AddCandidateStepOneValidator } from './add-candidate-step-one.validator';
import { KeyValue } from '@angular/common';
import {
  CandidateFormModel,
  CandidateService,
  StatusCandidateService,
  TemporaryFileService,
} from '../../../core';
import { AwSnackbarService, Base64Util, FileUtil } from '../../../shared';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-add-candidate-step-one',
  templateUrl: './add-candidate-step-one.component.html',
  styleUrls: ['./add-candidate-step-one.component.scss'],
})
export class AddCandidateStepOneComponent
  implements OnInit, OnChanges, OnDestroy
{
  @Input() candidateCriteria: CandidateInformation;
  @Input() maxFileSize: number;
  @Output() goNextPageEvent = new EventEmitter<CandidateInformation>();
  @Output() cancelFormEvent = new EventEmitter<void>();

  formGroup: FormGroup;
  salutationCriteria = salutationCriteria;
  genderCriteria = genderCriteria;
  statusCriteria: KeyValue<any, any>[] = [];
  priorityCriteria: KeyValue<any, any>[] = priorityCriteria;
  shouldShowError = false;
  maxFileSizeAmount = '';
  phoneNumberValidator = AddCandidateStepOneValidator.phoneNumberValidator;
  blobUploadProfileURL: any;
  formEdit = !!this.activateRoute.snapshot.params.id;
  @ViewChild('profileElement') uploadProfileInputElement: ElementRef;
  private subscriptions: Subscription = new Subscription();

  constructor(
    private formBuilder: FormBuilder,
    private statusCandidateService: StatusCandidateService,
    private temporaryFileService: TemporaryFileService,
    private candidateService: CandidateService,
    private activateRoute: ActivatedRoute,
    private awSnackbarService: AwSnackbarService,
  ) {}

  async ngOnInit(): Promise<void> {
    this.setupFormGroup();
    this.subscribeToFormChanges();
    await this.fetchStatusCriteria();
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  ngOnChanges(changes: SimpleChanges) {
    if (this.candidateCriteria) {
      this.formGroup?.patchValue(
        {
          ...this.candidateCriteria,
        },
        { onlySelf: false, emitEvent: false },
      );
    }

    if (this.maxFileSize) {
      this.maxFileSizeAmount = FileUtil.formatFileSize(this.maxFileSize || 0);
    }
  }

  async fetchStatusCriteria(): Promise<void> {
    const statusCandidateList = await this.statusCandidateService
      .getList(1, 100, '', 'title', 'asc')
      .toPromise();
    this.statusCriteria = statusCandidateList.contents.map((data) => {
      return {
        key: data.id,
        value: data.title,
      };
    });
  }

  async goNext() {
    if (this.formGroup.invalid) {
      this.shouldShowError = true;
      return;
    }

    if (
      (await this.checkDuplicatedEmail(this.formGroup.getRawValue().email)) > 0
    ) {
      this.formGroup
        .get('email')
        .setErrors({ message: 'This email address is currently in use.' });
      this.shouldShowError = true;
      return;
    }

    const formData = this.formGroup.getRawValue();
    this.goNextPageEvent.emit({
      id: this.candidateCriteria?.id || 0,
      ...formData,
    });
  }

  cancelForm(): void {
    this.cancelFormEvent.emit();
  }

  selectFile(): void {
    this.uploadProfileInputElement.nativeElement.click();
    if (this.uploadProfileInputElement) {
      this.uploadProfileInputElement.nativeElement.value = '';
    }
  }

  async uploadProfileURL($event: any): Promise<void> {
    const file = $event.target.files[0] as File;

    if (file.size > this.maxFileSize) {
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: `The profile picture size cannot be exceed ${this.maxFileSizeAmount}`,
      });

      return;
    }

    const profile = new FormData();
    profile.append('fileUpload', file);
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => {
      this.blobUploadProfileURL = reader.result;
    };

    try {
      const response = await this.temporaryFileService
        .fileUpload(profile, 'image')
        .toPromise();
      this.formGroup.patchValue({
        profileFileId: response?.fileId,
        profileFileBase64: response?.resourceBase64,
        profileFileExtension: FileUtil.getFileExtension(
          response?.originalFilename,
        ),
      });
    } catch (e) {
      const { statusCode } = e?.apierror;
      switch (statusCode) {
        case 5001: {
          this.awSnackbarService.openCustomSnackbar({
            type: 'error',
            icon: 'close',
            message: `The file upload does not accept valid file types.`,
          });
          break;
        }
      }
    }
  }

  getProfileURL() {
    const { profileFileBase64, profileFileExtension } =
      this.formGroup.getRawValue();
    return (
      this.candidateCriteria?.profileURL ||
      this.blobUploadProfileURL ||
      Base64Util.convertToImageUrl(
        profileFileBase64,
        `image/${profileFileExtension}`,
      )
    );
  }

  clearProfileURL(): void {
    this.formGroup.patchValue({
      profileFileId: '',
      profileFileBase64: '',
      profileFileExtension: '',
    });
    this.blobUploadProfileURL = '';
  }

  private setupFormGroup(): void {
    this.formGroup = this.formBuilder.group({
      salutation: new FormControl(this.candidateCriteria?.salutation || '', [
        AddCandidateStepOneValidator.fieldSalutation(),
      ]),
      firstName: new FormControl(this.candidateCriteria?.firstName || '', [
        AddCandidateStepOneValidator.fieldFirstName(),
      ]),
      lastName: new FormControl(this.candidateCriteria?.lastName || '', [
        AddCandidateStepOneValidator.fieldLastName(),
      ]),
      gender: new FormControl(this.candidateCriteria?.gender || 'Male', [
        AddCandidateStepOneValidator.fieldGender(),
      ]),
      dateOfBirth: new FormControl(this.candidateCriteria?.dateOfBirth || '', [
        AddCandidateStepOneValidator.fieldDateOfBirth(),
      ]),
      email: new FormControl(this.candidateCriteria?.email || '', [
        AddCandidateStepOneValidator.fieldEmail(),
      ]),
      phoneNumbers: new FormControl(
        this.candidateCriteria?.phoneNumbers || [''],
        [],
      ),
      priority: new FormControl(this.candidateCriteria?.priority || 'Normal'),
      status: new FormControl(this.candidateCriteria?.status || 1),
      profileFileId: new FormControl(
        this.candidateCriteria?.profileFileId || '',
      ),
      profileFileBase64: new FormControl(
        this.candidateCriteria?.profileFileBase64,
      ),
      profileFileExtension: new FormControl(
        this.candidateCriteria?.profileFileExtension,
      ),
      description: new FormControl(this.candidateCriteria?.description),
    });
  }

  private subscribeToFormChanges(): void {
    const formSubscription = this.formGroup.valueChanges.subscribe(() => {
      this.shouldShowError = false;
      localStorage.setItem(
        'candidate-form-step-1',
        JSON.stringify(this.formGroup.getRawValue()),
      );

      if (this.formEdit) {
        localStorage.setItem('candidate-form-has-change', 'true');
      }
    });
    this.subscriptions.add(formSubscription);
  }

  private async checkDuplicatedEmail(email): Promise<number> {
    if (this.formEdit) {
      const data: CandidateFormModel = JSON.parse(
        localStorage.getItem('candidate-form-original'),
      );
      if (email?.trim() === data.email?.trim()) {
        return 0;
      }
    }

    return this.candidateService.checkDuplicatedEmail(email).toPromise();
  }
}
