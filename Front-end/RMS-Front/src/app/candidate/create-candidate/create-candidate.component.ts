import { ACCEPT, getFileIcon } from '../../core';
import { PRIORITYS } from '../../core';
import { VALIDATION_MESSAGE } from '../../core';
import { SALUTAIONS } from '../../core';
import { EMAIL_PATERN } from '../../core';
import { GetIconService } from '../../core';
import { mask } from '../../core';
import { USER_UPLOAD } from '../../core';
import { Location } from '@angular/common';
import { UniversityService } from '../../core';
import { UniversityModel } from '../../core';
import { MessageService } from '../../core';
import { CandidateService } from '../../core';
import { StatusCandidateService } from '../../core';
import { StatusCandidateModel } from '../../core';
import { CandidateFormModel, CandidateModel } from '../../core';
import { Component, OnInit } from '@angular/core';
import { DialogAddUniversityComponent } from '../dialog-add-university';
import { MatDialog } from '@angular/material/dialog';
import { IsLoadingService } from '@service-work/is-loading';
import { formatDate } from '../../shared';

@Component({
  selector: 'app-create-candidate',
  templateUrl: './create-candidate.component.html',
  styleUrls: ['./create-candidate.component.css'],
})
export class CreateCandidateComponent {
  //
  // candidate: CandidateFormModel;
  // statuses: StatusCandidateModel[];
  // candidateEmail: CandidateModel[];
  // universities: UniversityModel[];
  // defualtIcon = USER_UPLOAD;
  // listUniversity: number[];
  // filterUniversity = '';
  // coverImage: any;
  // selectedCover: any;
  // emailPartern = EMAIL_PATERN;
  // salutations = SALUTAIONS;
  // prioritys = PRIORITYS;
  // validationMessage = VALIDATION_MESSAGE;
  // public mask = mask;
  // acceptType = ACCEPT;
  // fileDragAndDrop: any;
  // constructor(
  //   private statusService: StatusCandidateService,
  //   private candidateService: CandidateService,
  //   private message: MessageService,
  //   private universityService: UniversityService,
  //   private pageHistory: Location,
  //   private dialog: MatDialog,
  //   private getIconService: GetIconService,
  //   private isloadingService: IsLoadingService
  // ) {
  //   this.candidate = {
  //     id: null,
  //     firstname: '',
  //     lastname: '',
  //     salutation: SALUTAIONS[0].title,
  //     gender: null,
  //     email: null,
  //     telephone: [],
  //     photoUrl: null,
  //     gpa: null,
  //     priority: PRIORITYS[1].title,
  //     active: null,
  //     description: null,
  //     filenames: [],
  //     statusId: null,
  //     universities: [
  //       { id: null }
  //     ],
  //     dateOfBirth: new Date(),
  //     yearOfExperience: ''
  //   };
  // }
  //
  // ngOnInit(): void {
  //   this.loadStatus();
  //   this.loadUniversities();
  // }
  //
  // loadStatus(): void {
  //   this.statusService.getlist(1, 100, '', 'title', 'asc')
  //     .subscribe((respone) => {
  //       this.statuses = respone.contents.filter(x => x.active && !x.deleted);
  //       this.candidate.statusId = this.statuses[0].id;
  //     });
  // }
  //
  // loadUniversities(): void {
  //   this.universityService.getList(1, 100, this.filterUniversity, 'asc', 'name')
  //     .subscribe((respone) => {
  //       this.universities = respone.contents;
  //     });
  // }
  //
  // clearSalutation(): void {
  //   this.candidate.salutation = null;
  // }
  //
  // clearFirstname(): void {
  //   this.candidate.firstname = '';
  // }
  //
  // clearLastname(): void {
  //   this.candidate.lastname = '';
  // }
  //
  // clearEmail(): void {
  //   this.candidate.email = '';
  // }
  //
  // clearTelephone(): void {
  //   this.candidate.telephone = [];
  // }
  //
  // clearFrom(): void {
  //   this.candidate.universities = null;
  // }
  // clearGpa(): void {
  //   this.candidate.gpa = null;
  // }
  //
  // onFilterUniversity(): void {
  //   if (this.filterUniversity === undefined || this.filterUniversity === null || this.filterUniversity === '') {
  //     this.loadUniversities();
  //   } else if (this.filterUniversity.length >= 2) {
  //     this.universityService.getList(1, 100, this.filterUniversity, 'asc', 'name').subscribe((respone) => {
  //       this.universities = respone.contents;
  //     });
  //   }
  // }
  //
  // onSubmit(): void {
  //   for (let i = 0; i < this.listUniversity.length; i++) {
  //     this.candidate.universities[i] = { id: this.listUniversity[i] };
  //   }
  //   if (this.candidate.gpa < 0) {
  //     this.message.showWarning(this.validationMessage.gpa.min, 'GPA Validation');
  //   } else if (this.candidate.gpa > 4.0) {
  //     this.message.showWarning(this.validationMessage.gpa.max, 'GPA Validation');
  //   } else {
  //     this.candidateService.emailValidation(this.candidate.email).subscribe((respone) => {
  //       if (respone === 0) {
  //         this.candidate.dateOfBirth = utils.formatDate(this.candidate.dateOfBirth);
  //         const subscription = this.candidateService.create(this.candidate).subscribe(() => {
  //           this.message.showSuccess('Candidate was save successfully...', 'Save Candidate');
  //           this.back();
  //         });
  //         this.isloadingService.add(subscription, { key: 'CreateCandidateComponent', unique: 'CreateCandidateComponent' });
  //       } else {
  //         const archieveLink = `<a class="text-link-abs" href="/admin/candidate?archive=archive">Archieve</a>`;
  //         this.message
  //         .showError(`${this.validationMessage.email.exist} Please check in candidate archive. ${archieveLink}`,
  //         'Email Validation', 55000);
  //       }
  //     });
  //   }
  // }
  //
  // back(): void {
  //   this.pageHistory.back();
  // }
  //
  // addUniversity(): void {
  //   const dialogRef = this.dialog.open(DialogAddUniversityComponent, {
  //     width: '700px',
  //     data: {
  //       title: this.filterUniversity
  //     },
  //     panelClass: 'overlay-scrollable'
  //   });
  //   dialogRef.afterClosed().subscribe(() => {
  //     this.filterUniversity = '';
  //     this.loadUniversities();
  //   });
  // }
  //
  // clearUniversity(): void {
  //   this.listUniversity = null;
  // }
  //
  // clearPriority(): void {
  //   this.candidate.priority = null;
  // }
  //
  // clearStatusCandidate(): void {
  //   this.candidate.statusId = null;
  // }
  //
  // clearGender(): void {
  //   this.candidate.gender = null;
  // }
  //
  // openImage(event: any): void {
  //   this.selectedCover = (event.target.files[0] as File);
  //   const reader = new FileReader();
  //   reader.readAsDataURL(this.selectedCover);
  //   reader.onload = () => {
  //     this.coverImage = reader.result;
  //   };
  //   this.uploadCandidateProfile();
  // }
  //
  // uploadCandidateProfile(): void {
  //   const profile = new FormData();
  //   profile.append('filename', this.selectedCover);
  //   this.candidateService.uploadCandidateProfile(profile).subscribe((respone) => {
  //     this.candidate.photoUrl = respone.photoUrl;
  //   });
  // }
  //
  // clearImage(): void {
  //   this.selectedCover = null;
  //   this.coverImage = null;
  //   this.candidate.photoUrl = '';
  //   this.checkGender();
  // }
  //
  // checkGender(): void {
  //   if (!!this.coverImage) {
  //   } else {
  //     this.defualtIcon = this.getIconService.checkGender(this.candidate?.gender);
  //   }
  // }
  //
  // openFiles(files?: File[]): void {
  //   if (files.length > 0){
  //     const formDatas = new FormData();
  //     for (const item of files) {
  //       formDatas.append('filenames', item);
  //     }
  //     this.candidateService.uploadCandidateFiles(formDatas).subscribe((respone) => {
  //       for(const filename of respone.filenames){
  //         this.candidate.filenames.push(filename);
  //       }
  //       this.fileDragAndDrop = null;
  //     });
  //   }
  // }
  //
  //
  // removeFile(filename: string): void{
  //   const index = this.candidate.filenames.indexOf(filename, 0);
  //   if (index > -1) {
  //     this.candidate.filenames.splice(index, 1);
  //   }
  // }
  //
  // previewFile(filename: string): void{
  //   alert('Preview' + filename);
  // }
  //
  // getFileIcon(filename: string): any{
  //   return getFileIcon(filename);
  // }

}
