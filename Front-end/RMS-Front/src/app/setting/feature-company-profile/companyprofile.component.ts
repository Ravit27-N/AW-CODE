import { MessageService } from '../../core';
import { CompanyProfileService } from '../../core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { IsLoadingService } from '@service-work/is-loading';
import { getAssetPrefix } from '../../shared';
import { CompanyProfileValidation } from './CompanyProfileValidation';

@Component({
  selector: 'app-feature-company-profile',
  templateUrl: './companyprofile.component.html',
  styleUrls: ['./companyprofile.component.scss'],
})
export class CompanyprofileComponent implements OnInit {
  form: FormGroup;
  public mask = [
    /[0]/,
    /\d/,
    /\d/,
    ' ',
    /\d/,
    /\d/,
    /\d/,
    ' ',
    /\d/,
    /\d/,
    /\d/,
    /\d/,
  ];
  image: any;
  imageToUpload: any;
  shouldShowError = false;
  constructor(
    private formBuilder: FormBuilder,
    private companyProfileService: CompanyProfileService,
    private messageService: MessageService,
    private sanitizer: DomSanitizer,
    private isLoadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      id: new FormControl(''),
      title: new FormControl('', [CompanyProfileValidation.titleValidation()]),
      description: new FormControl(''),
      address: new FormControl('', [
        CompanyProfileValidation.addressValidation(),
      ]),
      telephone: new FormControl('', [
        CompanyProfileValidation.telephoneValidate(),
      ]),
      email: new FormControl('', [CompanyProfileValidation.emailValidation()]),
      website: new FormControl('', [
        CompanyProfileValidation.websiteValidation(),
      ]),
    });
    this.companyProfileService.getData().toPromise().then(
      (data) => {
        this.form.patchValue({
          id: data.id,
          title: data.title,
          description: data.description,
          address: data.address,
          telephone: data.telephone,
          email: data.email,
          website: data.website,
        });
      },
      () => {},
    );

    const getImgCompany = this.companyProfileService.getCompanyProfileImage().subscribe(
      (imageData) => {
        const TYPED_ARRAY = new Uint8Array(imageData);
        const STRING_CHAR = TYPED_ARRAY.reduce(
          (data, byte) => data + String.fromCharCode(byte),
          '',
        );
        const base64String = btoa(STRING_CHAR);
        this.image = this.sanitizer.bypassSecurityTrustUrl(
          'data:image/jpg;base64, ' + base64String,
        );
      },
      () => {
        this.image = `${document.baseURI}/assets/img/all-web-logo.png`;
      },
    );
    this.isLoadingService.add(getImgCompany, { key: 'image', unique: 'image' });
  }
  update(): void {
    if (this.form.controls.id.value === 0) {
      this.form.patchValue({
        id: 0,
        companyLogoUrl: 'null',
      });
    }
    if (this.form.invalid) {
      this.shouldShowError = true;
      this.messageService.showWarning('Invalid Input', 'Company Profile');
      return;
    }
    this.companyProfileService.update(this.form.value).subscribe(
      () => {
        this.messageService.showSuccess('Update Success', 'Company Profile');
      },
      () => {
        this.messageService.showError('Update Fail', 'Company Profile');
      },
    );
  }
  clearTitle(): void {
    this.form.controls.title.setValue('');
  }

  onValueChange(event: any): void {
    this.imageToUpload = event.target.files[0] as File;
    const reader = new FileReader();
    reader.readAsDataURL(this.imageToUpload);
    reader.onload = () => {};
    const fd = new FormData();
    fd.append('companyLogoUrl', this.imageToUpload);
    this.companyProfileService.uploadCompanyProfileImage(fd).toPromise().then(
      () => {
        this.image = reader.result;
        this.messageService.showSuccess('Image Upload Successfully', 'Company Profile');
      },
      () => {
        this.messageService.showError('Image Upload Unsuccessfully', 'Company Profile');
      },
    );
  }
}
