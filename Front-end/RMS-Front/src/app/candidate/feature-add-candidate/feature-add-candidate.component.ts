import { Component, OnInit } from '@angular/core';
import { AwStepModel } from '../../shared/components/aw-step';
import {
  addCandidateSteps,
  pageSubtitle,
} from './feature-add-candidate.constant';
import { AddCandidateModel } from './add-candidate.model';
import { Education } from './add-candidate-step-two/add-candidate-step-two.model';
import { Experience } from './add-candidate-step-three/add-candidate-step-three.model';
// @ts-ignore
import { CandidateService, FileInfoModel } from '../../core';
import {
  AwSnackbarService,
  DateUtil,
  FileUtil,
  StringUtil,
} from '../../shared';
import { FeatureAddCandidateModel } from './feature-add-candidate.model';
import { ActivatedRoute } from '@angular/router';
import { FileManagerService } from '../../core/service/file-manager.service';

@Component({
  selector: 'app-feature-add-candidate',
  templateUrl: './feature-add-candidate.component.html',
  styleUrls: ['./feature-add-candidate.component.scss'],
})
export class FeatureAddCandidateComponent implements OnInit {
  addCandidateSteps: AwStepModel[] = addCandidateSteps;
  currentStep = 1;
  pageSubtitle = '';
  pageSubtitles: string[] = pageSubtitle;
  candidateStep1: AddCandidateModel;
  candidateStep2: { education: Education[]; form: any } = {
    form: {},
    education: [],
  };
  candidateStep3: { experiences: Experience[]; form: any } = {
    form: {},
    experiences: [],
  };
  candidateStep4: FileInfoModel;

  maxFileSize = 0;

  constructor(
    private candidateService: CandidateService,
    private activateRoute: ActivatedRoute,
    private awSnackbarService: AwSnackbarService,
    private fileManagerService: FileManagerService,
  ) {}

  get title() {
    const editedForm = !!this.activateRoute.snapshot.params.id;
    return editedForm ? 'Updated Candidate' : 'Add Candidate';
  }

  async ngOnInit(): Promise<void> {
    // Get maximum upload fileSize.
    this.maxFileSize = await this.fileManagerService
      .getMaxUploadFileSize()
      .toPromise();

    if (this.activateRoute.snapshot.params.id) {
      await this.loadUserDetail();
    }

    this.loadFromLocalStorage();
    this.reArrangeFormCriteria();
  }

  goNextPageEvent() {
    if (this.currentStep === this.addCandidateSteps.length) {
      this.submitForm();
      return;
    }
    this.currentStep++;
    this.updateFormState();
    this.loadFromLocalStorage();
  }

  submitForm(): void {
    localStorage.setItem('submit-form-candidate', 'true');
    const formBody: FeatureAddCandidateModel = {
      id: this.activateRoute.snapshot.params?.id || null,
      photoUrl: this.candidateStep1?.profileFileId,
      salutation: this.candidateStep1?.salutation,
      firstname: StringUtil.capitalize(this.candidateStep1?.firstName),
      lastname: this.candidateStep1?.lastName?.toUpperCase(),
      gender: this?.candidateStep1?.gender ? 'Female' : 'Male',
      dateOfBirth: DateUtil.formatDateToDDMMYYYY(
        new Date(this?.candidateStep1?.dateOfBirth),
      ),
      email: this?.candidateStep1?.email,
      telephones: this?.candidateStep1?.phoneNumbers,
      priority: this?.candidateStep1?.priority,
      statusId: this?.candidateStep1?.status,
      description: this?.candidateStep1?.description,
      candidateExperiences: this.candidateStep3.experiences
        ?.filter((item) => item.experienceStartDate)
        .map((item) => ({
          id: item?.id || null,
          companyName: item?.companyName,
          position: item?.position,
          level: item?.level,
          startDate: DateUtil.formatDateToDDMMYYYY(
            new Date(item?.experienceStartDate),
          ),
          endDate: item?.experienceEndDate
            ? DateUtil.formatDateToDDMMYYYY(new Date(item?.experienceEndDate))
            : null,
          projectType: item?.projectType,
          technology: item?.technology,
          remarks: item?.technology,
        })),
      candidateUniversities: this.candidateStep2.education
        .filter((item) => item.academicYearStart)
        .map((item) => ({
          id: item?.id || null,
          universityId: item?.universityId,
          major: item?.major,
          gpa: item?.gpa,
          graduate: item?.graduate,
          startDate: DateUtil.formatDateToDDMMYYYY(
            new Date(item?.academicYearStart),
          ),
          endDate: item?.academicYearEnd
            ? DateUtil.formatDateToDDMMYYYY(new Date(item?.academicYearEnd))
            : null,
          remarks: item?.remarks,
          degree: item?.degree,
        })),
      cvFileName: this.candidateStep4.fileId,
    };

    if (this.activateRoute.snapshot.params?.id) {
      this.candidateService
        .update(formBody)
        .toPromise()
        .then(() => {
          this.awSnackbarService.openCustomSnackbar({
            type: 'success',
            message: 'The candidate is updated successfully.',
            icon: 'close',
          });

          history.back();
        })
        .catch(() => {
          this.awSnackbarService.openCustomSnackbar({
            type: 'error',
            message: 'Fail to communication with server.',
            icon: 'close',
          });
        });
    } else {
      this.candidateService
        .create(formBody)
        .toPromise()
        .then(() => {
          this.awSnackbarService.openCustomSnackbar({
            type: 'success',
            message: 'A new candidate is added successfully.',
            icon: 'close',
          });

          history.back();
        })
        .catch(() => {
          this.awSnackbarService.openCustomSnackbar({
            type: 'error',
            message: 'Fail to communication with server.',
            icon: 'close',
          });
        });
    }
  }

  goPreviousPageEvent(): void {
    if (this.currentStep === 1) {
      return;
    }
    this.currentStep--;
    this.updateFormState();
    this.loadFromLocalStorage();
  }

  async cancelForm(): Promise<void> {
    history.back();
  }

  reArrangeFormCriteria(): void {
    this.checkActiveStep();
    this.checkPageSubtitle();
  }

  checkActiveStep(): void {
    this.addCandidateSteps = this.addCandidateSteps.map((step, order) => {
      const isCurrentStep = order === this.currentStep - 1;
      const localStorageKey = `candidate-form-step-${order + 1}`;
      const isStepDisabled = !localStorage.getItem(localStorageKey);
      const step5Enabled =
        this.currentStep === 4 || localStorage.getItem(`candidate-form-step-4`);

      // Define active and disabled properties based on conditions
      const active = isCurrentStep || order === 0; // First step is always active
      const disabled = isStepDisabled && !isCurrentStep && !step5Enabled;

      // Return the updated step object
      return {
        ...step,
        active,
        disabled,
      };
    });
  }

  checkPageSubtitle(): void {
    const founded = this.pageSubtitles[this.currentStep - 1];
    this.pageSubtitle = founded || '';
  }

  updateFormState(): void {
    this.checkActiveStep();
    this.setFormToStorage();
  }

  setFormToStorage(): void {
    // Store the candidate form in local storage
    localStorage.setItem('currentStep', `${this.currentStep}`);
  }

  loadFromLocalStorage(): void {
    const candidateStep1 = localStorage.getItem('candidate-form-step-1');
    const candidateStep2 = localStorage.getItem('candidate-form-step-2');
    const candidateStep3 = localStorage.getItem('candidate-form-step-3');
    const candidateStep4 = localStorage.getItem('candidate-form-step-4');

    this.candidateStep1 = candidateStep1 ? JSON.parse(candidateStep1) : {};
    this.candidateStep2 = candidateStep2 ? JSON.parse(candidateStep2) : {};
    this.candidateStep3 = candidateStep3 ? JSON.parse(candidateStep3) : {};
    this.currentStep = JSON.parse(localStorage.getItem('currentStep') || '1');
    this.candidateStep4 = JSON.parse(candidateStep4);
  }

  changeStep($event: number) {
    this.currentStep = $event;
    this.setFormToStorage();
    this.updateFormState();
    this.loadFromLocalStorage();
  }

  private async loadUserDetail(): Promise<void> {
    const candidateId = this.activateRoute.snapshot.params.id;

    if (candidateId) {
      const candidateDetails = await this.candidateService
        .getById(candidateId)
        .toPromise();

      let profileImage: any;
      if (candidateDetails.photoUrl) {
        try {
          profileImage = await this.getProfileBase64(
            candidateDetails.id,
            candidateDetails.photoUrl,
          );
        } catch (error) {
          console.error({ error });
        }

      }

      const form1: any = {
        id: candidateDetails.id,
        profileFileExtension: profileImage?.photoUrl
          ? FileUtil.getFileExtension(profileImage?.originalFilename)
          : '',
        profileFileBase64: candidateDetails.photoUrl
          ? profileImage?.resourceBase64
          : '',
        salutation: candidateDetails.salutation,
        profileFileId: candidateDetails.photoUrl,
        firstName: candidateDetails.firstname,
        lastName: candidateDetails.lastname,
        gender: candidateDetails.gender,
        dateOfBirth: new Date(candidateDetails.dateOfBirth),
        phoneNumbers: candidateDetails.telephones,
        email: candidateDetails.email,
        priority: candidateDetails.priority,
        status: candidateDetails.statusId,
        description: candidateDetails.description,
        profileURL: '',
      };

      const form2: Education[] = candidateDetails.candidateUniversities.map(
        (item) => ({
          id: item.id,
          degree: item.degree,
          gpa: item.gpa,
          graduate: item.graduate,
          remarks: item.remarks,
          academicYearStart: new Date(item.startDate),
          academicYearEnd: item.endDate ? new Date(item.endDate) : null,
          universityId: item.universityId,
          major: item.major,
        }),
      );

      const form3: Experience[] = candidateDetails.candidateExperiences.map(
        (item) => ({
          id: null,
          position: item.position,
          companyName: item.companyName,
          experienceStartDate: new Date(item.startDate),
          experienceEndDate: item.endDate ? new Date(item.endDate) : null,
          technology: item.technology,
          projectType: item.projectType,
          level: item.level,
        }),
      );

      const form4: FileInfoModel = {
        fileId: '',
        resourceBase64: '',
        originalFilename: '',
        fileSize: 0,
      };

      if (candidateDetails.cvFileName) {
        try {
          const data = await this.getCVBase64(
            candidateDetails.id,
            candidateDetails.cvFileName,
          );
          form4.fileId = data.fileId;
          form4.resourceBase64 = data.resourceBase64;
          form4.originalFilename = data.originalFilename;
          form4.fileSize = data.fileSize;
        } catch (error) {
          console.error({ error });
        }
      }

      localStorage.setItem(
        'candidate-form-step-4',
        JSON.stringify(form4),
      );

      localStorage.setItem(
        'candidate-form-step-3',
        JSON.stringify({
          experiences: form3,
        }),
      );

      localStorage.setItem('candidate-form-step-1', JSON.stringify(form1));
      localStorage.setItem(
        'candidate-form-step-2',
        JSON.stringify({
          education: form2,
        }),
      );
      localStorage.setItem(
        'candidate-form-original',
        JSON.stringify(candidateDetails),
      );
    }
  }

  private getProfileBase64(candidateId: number, profileURL: string) {
    return this.candidateService
      .getFileBase64(candidateId, profileURL)
      .toPromise();
  }

  private getCVBase64(candidateId: number, cvURL: string) {
    return this.candidateService.getFileBase64(candidateId, cvURL).toPromise();
  }
}
