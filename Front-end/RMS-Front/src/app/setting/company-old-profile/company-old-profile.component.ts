import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CompanyProfileService, MessageService } from '../../core';
import { DomSanitizer } from '@angular/platform-browser';
import { IsLoadingService } from '@service-work/is-loading';
import { getAssetPrefix } from '../../shared';

@Component({
  selector: 'app-company-old-profile',
  templateUrl: './company-old-profile.component.html',
  styleUrls: ['./company-old-profile.component.scss'],
})
export class CompanyOldProfileComponent implements OnInit {
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
  constructor(
    private formbuilder: FormBuilder,
    private service: CompanyProfileService,
    private message: MessageService,
    private sanitizer: DomSanitizer,
    private isloadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    this.form = this.formbuilder.group({
      id: [''],
      title: ['', Validators.required],
      description: ['', Validators.required],
      address: ['', Validators.required],
      telephone: ['', Validators.required],
      email: ['', [Validators.email, Validators.required]],
      website: ['', Validators.required],
    });
    const subscription = this.service.getData().subscribe(
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
    this.isloadingService.add(subscription, {
      key: 'company',
      unique: 'company',
    });
    const getImgCompany = this.service.getCompanyProfileImage().subscribe(
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
        this.image = `${getAssetPrefix()}/assets/img/all-web-logo.png`;
      },
    );
    this.isloadingService.add(getImgCompany, { key: 'image', unique: 'image' });
  }
  update(): void {
    if (this.form.controls.id.value === 0) {
      this.form.patchValue({
        id: 1,
        companyLogoUrl: 'null',
      });
    }
    if (this.form.invalid) {
      this.message.showWarning('Invalid Input', 'Company Profile');
      return;
    }
    this.service.update(this.form.value).subscribe(
      () => {
        this.message.showSuccess('Update Sucess', 'Company Profile');
      },
      () => {
        this.message.showError('Update Fail', 'Company Profile');
      },
    );
  }
  clearTitle(): void {
    this.form.controls.title.setValue('');
  }

  clearAddress(): void {
    this.form.controls.address.setValue('');
  }

  clearTelephone(): void {
    this.form.controls.telephone.setValue('');
  }

  clearEmail(): void {
    this.form.controls.email.setValue('');
  }

  clearWebsite(): void {
    this.form.controls.website.setValue('');
  }
  onValueChange(event: any): void {
    this.imageToUpload = event.target.files[0] as File;
    const reader = new FileReader();
    reader.readAsDataURL(this.imageToUpload);
    reader.onload = () => {};
    const fd = new FormData();
    fd.append('companyLogoUrl', this.imageToUpload);
    this.service.uploadCompanyProfileImage(fd).subscribe(
      () => {
        this.image = reader.result;
        this.message.showSuccess('Image Upload Sucessfully', 'Company Profile');
      },
      () => {
        this.message.showError('Image Upload Unsucessfully', 'Company Profile');
      },
    );
  }
}
