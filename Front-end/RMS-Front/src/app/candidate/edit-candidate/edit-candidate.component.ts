import {
  ACCEPT,
  checkFileType,
  downloadDocumentFile,
  getFileIcon,
  previewPdfFile,
} from '../../core';
import { RemoveFileDialogComponent } from '../remove-file-dialog';
import { VALIDATION_MESSAGE } from '../../core';
import { GetIconService } from '../../core';
import { USER_UPLOAD } from '../../core';
import { mask } from '../../core';
import { Location } from '@angular/common';
import { UniversityService } from '../../core';
import { UniversityModel } from '../../core';
import { StatusCandidateService } from '../../core';
import { StatusCandidateModel } from '../../core';
import { MessageService } from '../../core';
import { CandidateService } from '../../core';
import { CandidateFormModel } from '../../core';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DialogAddUniversityComponent } from '../dialog-add-university';
import { MatDialog } from '@angular/material/dialog';
import { EMAIL_PATERN } from 'src/app/core/model/email';
import { SALUTAIONS } from 'src/app/core/model/salutation';
import { PRIORITYS } from 'src/app/core/model/priority';
import { IsLoadingService } from '@service-work/is-loading';
import { formatDate } from '../../shared';

@Component({
  selector: 'app-edit-candidate',
  templateUrl: './edit-candidate.component.html',
  styleUrls: ['./edit-candidate.component.css'],
})
export class EditCandidateComponent implements OnInit {
  candidateID: any;

  candidate: CandidateFormModel;
  profileReplacemanet: any = false;
  useProfile: boolean;
  oldProfileUrl: string;

  selectedCover: any;
  listUniversity: number[] = [];
  filterUniversity = '';
  universities: UniversityModel[] = [];
  statuses: StatusCandidateModel[] = [];
  emailPartern = EMAIL_PATERN;
  salutations = SALUTAIONS;
  prioritys = PRIORITYS;
  validationMessage = VALIDATION_MESSAGE;
  mask = mask;
  acceptType = ACCEPT;
  fileDragAndDrop: any;

  constructor(
    private candidateService: CandidateService,
    private message: MessageService,
    private statusService: StatusCandidateService,
    private activateRoute: ActivatedRoute,
    private universityService: UniversityService,
    private pageHistory: Location,
    private dialog: MatDialog,
    private getIconService: GetIconService,
    private isloadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    // this.candidate = {
    //   id: 0,
    //   firstname: null,
    //   lastname: null,
    //   salutation: null,
    //   gender: null,
    //   email: null,
    //   telephone: '',
    //   photoUrl: null,
    //   gpa: null,
    //   priority: null,
    //   active: null,
    //   description: null,
    //   statusId: null,
    //   universities: [{ id: null }],
    //   filenames: [],
    //   dateOfBirth: new Date(),
    //   yearOfExperience: 0,
    // };
    this.loadData();
  }

  clearSalutation(): void {
    this.candidate.salutation = null;
  }

  clearFirstname(): void {
    this.candidate.firstname = '';
  }

  clearLastname(): void {
    this.candidate.lastname = '';
  }

  clearEmail(): void {
    this.candidate.email = '';
  }

  clearTelephone(): void {
    // this.candidate.telephone = '';
  }

  clearFrom(): void {
    // this.candidate.universities = null;
  }

  clearGpa(): void {
    // this.candidate.gpa = null;
  }

  onSubmit(): void {
    // this.candidate.universities = [{ id: null }];
    // if (this.candidate.gpa < 0) {
    //   this.message.showWarning(
    //     this.validationMessage.gpa.min,
    //     'GPA Validation'
    //   );
    // } else if (this.candidate.gpa > 4.0) {
    //   this.message.showWarning(
    //     this.validationMessage.gpa.max,
    //     'GPA Validation'
    //   );
    // } else {
    //   for (let i = 0; i < this.listUniversity.length; i++) {
    //     this.candidate.universities[i] = { id: this.listUniversity[i] };
    //   }
    //   // this.candidate.dateOfBirth = utils.formatDate(this.candidate.dateOfBirth);
    //   const subscription = this.candidateService
    //     .update(this.candidate)
    //     .subscribe(() => {
    //       this.message.showSuccess(
    //         'Candidate was update successfully...',
    //         'Update Candidate'
    //       );
    //       this.back();
    //     });
    //   this.isloadingService.add(subscription, {
    //     key: 'EditCandidateComponent',
    //     unique: 'EditCandidateComponent',
    //   });
    // }
  }

  onFilterUniversity(): void {
    if (
      this.filterUniversity === null ||
      this.filterUniversity === '' ||
      this.filterUniversity === undefined
    ) {
      this.loadUniversities();
    } else if (this.filterUniversity.length >= 2) {
      this.universityService
        .getList(1, 100, this.filterUniversity, 'asc', 'name')
        .subscribe((respone) => {
          this.universities = respone.contents;
        });
    }
  }

  loadStatus(): void {
    this.statusService
      .getList(1, 100, '', 'title', 'asc')
      .subscribe((respone) => {
        this.statuses = respone.contents.filter((x) => x.active && !x.deleted);
      });
  }

  loadUniversities(): void {
    this.universityService
      .getList(1, 100, this.filterUniversity, 'asc', 'name')
      .subscribe((respone) => {
        this.universities = respone.contents;
      });
  }

  loadData(): void {
    this.loadUniversities();
    this.loadStatus();
    this.candidateID = this.activateRoute.snapshot.params.id;

    const subscription = this.candidateService
      .getById(this.candidateID)
      .subscribe((respone) => {
        this.candidate = respone;
        this.candidate.dateOfBirth = new Date(this.candidate.dateOfBirth); // for datetime picker

        if (this.candidate.photoUrl) {
          this.useProfile = true;
          this.oldProfileUrl = this.candidate.photoUrl;
        } else {
          this.profileReplacemanet = USER_UPLOAD;
        }

      this.getCandidateFiles(this.candidate.id);
      // this.listUniversity = this.candidate.universities.map((x) => x.id);
    });

    this.isloadingService.add(subscription, {
      key: 'EditCandidateComponent',
      unique: 'EditCandidateComponent',
    });
  }

  back(): void {
    this.pageHistory.back();
  }

  clearUniversity(): void {
    this.listUniversity = null;
  }

  addUniversity(): void {
    const dialogRef = this.dialog.open(DialogAddUniversityComponent, {
      width: '700px',
      data: {
        title: this.filterUniversity,
      },
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.filterUniversity = '';
      this.loadUniversities();
    });
  }

  clearPriority(): void {
    this.candidate.priority = null;
  }

  clearStatusCandidate(): void {
    this.candidate.statusId = null;
  }

  clearGender(): void {
    this.candidate.gender = null;
  }

  openImage(event: any): void {
    this.selectedCover = event.target.files[0] as File;

    const reader = new FileReader();
    reader.readAsDataURL(this.selectedCover);
    reader.onload = () => {
      this.profileReplacemanet = reader.result;
    };

    this.uploadCandidateProfile();
  }

  uploadCandidateProfile(): void {
    const profile = new FormData();
    profile.append('filename', this.selectedCover);

    this.candidateService
      .uploadCandidateProfile(profile)
      .subscribe((respone) => {
        this.candidate.photoUrl = respone.photoUrl;
      });
  }

  clearImage(): void {
    this.selectedCover = null;
    if (this.useProfile) {
      this.candidate.photoUrl = this.oldProfileUrl;
      this.profileReplacemanet = false;
    } else {
      this.profileReplacemanet = USER_UPLOAD;
    }
  }

  get getUrl() {
    return `/candidate/${this.candidate.id}/view/${this.candidate.photoUrl}`;
  }

  openFiles(files?: File[]): void {
    if (files.length > 0) {
      const formDatas = new FormData();
      for (const item of files) {
        formDatas.append('filenames', item);
      }
      this.candidateService
        .updateCandidateFile(this.candidate.id, formDatas)
        .subscribe(() => {
          this.message.showSuccess(
            'Files was save successfully. ',
            'Update File',
          );
          this.getCandidateFiles(this.candidate.id);
          this.fileDragAndDrop = null;
        });
    }
  }

  getCandidateFiles(id: number | string): void {
    this.candidate.filenames = [];
    this.candidateService.getCandidateFileNames(id).subscribe((respone) => {
      for (const filename of respone.filenames) {
        this.candidate.filenames.push(filename);
      }
    });
  }

  removeCandidateFile(candidateId: number | string, filename: string): void {
    const dialogRef = this.dialog.open(RemoveFileDialogComponent, {
      data: {
        candidateId,
        filename,
      },
      width: '500px',
      panelClass: 'overlay-scrollable',
    });
    dialogRef.afterClosed().subscribe(() => {
      this.getCandidateFiles(candidateId);
    });
  }

  previewCandidateFile(candidateId: number | string, filename: string): void {
    this.candidateService
      .previewCandidateFile(candidateId, filename)
      .subscribe((respone) => {
        if (checkFileType(respone) === 'pdf') {
          previewPdfFile(respone);
        } else {
          downloadDocumentFile(respone, filename);
        }
      });
  }

  getFileIcon(filename: string): any {
    return getFileIcon(filename);
  }
}
