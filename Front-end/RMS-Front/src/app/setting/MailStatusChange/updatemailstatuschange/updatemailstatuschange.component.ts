import { MessageService } from './../../../core/service/message.service';
import { MailStatuschangeModel } from 'src/app/core/model/mailStatusChange';
import { ActivatedRoute, Router } from '@angular/router';
import { MailTemplateModel } from './../../../core/model/Mailtemplate';
import { StatusCandidateModel } from './../../../core/model/statuscandidate';
import { StatusCandidateService } from './../../../core/service/status-candidate.service';
import { MailtemplateService } from './../../../core/service/Mailtemplate.service';
import { MailStatusChangeService } from './../../../core/service/mailStatusChange.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Location } from '@angular/common';
import { IsLoadingService } from '@service-work/is-loading';
@Component({
  selector: 'app-updatemailstatuschange',
  templateUrl: './updatemailstatuschange.component.html',
  styleUrls: ['./updatemailstatuschange.component.scss']
})
export class UpdatemailstatuschangeComponent implements OnInit {
  id: any;
  selectable = true;
  removable = true;
  addOnBlur = true;
  toEmails: string[] = [];
  ccEmails: string[] = [];
  statusCandidate: StatusCandidateModel;
  candidates: StatusCandidateModel[] = [];
  mailTemplates: MailTemplateModel[];
  slideValue: string;
  public form: FormGroup;
  mail: MailTemplateModel;
  saveButtonEnable = false;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  @ViewChild('chipListCc') chipList: MatChipList;
  @ViewChild('chipListTo') chipListTo: MatChipList;

  constructor(private formbuilder: FormBuilder, private message: MessageService, private service: MailStatusChangeService,
    private statusCandidateService: StatusCandidateService, private router: Router, private pageHistory: Location
    , private actRoute: ActivatedRoute, private mailTemplateService: MailtemplateService, private isloadingService: IsLoadingService) { }

  @HostListener('document:click') enableSave(): void {
    if (this.form.get('to').value === '') {
      this.chipListTo.errorState = false;
    }
    if (!this.form.invalid && this.toEmails.length > 0 && this.form.get('to').value === '') {
      this.saveButtonEnable = false;
    }
    else {
      this.saveButtonEnable = true;
    }
  }

  savebtn(): void {
    this.mail = this.form.get('title').value;
    const payLoad: MailStatuschangeModel = {
      id: this.id,
      from: this.form.get('from').value,
      cc: this.ccEmails,
      mailTemplateId: this.form.get('mailtemplate').value,
      candidateStatusId: this.form.get('statuscandidate').value,
      title: this.form.get('title').value,
      to: this.toEmails,
      active: this.form.get('active').value
    };
    const subscription = this.service.update(this.id, payLoad).subscribe(() => {
      this.message.showSuccess('Save Complete', 'Update Mail Configuration');
      this.router.navigate(['/admin/setting/mailconfiguration/']);
    }, () => {
      this.message.showError('Save Fail', 'Update Mail Configuration');
    });
    this.isloadingService.add(subscription, { key: 'UpdatemailstatuschangeComponent', unique: 'UpdatemailstatuschangeComponent' });
  }
  ngOnInit(): void {
    this.id = Number(this.actRoute.snapshot.paramMap.get('id'));
    this.form = this.formbuilder.group({
      id: [''],
      title: ['', Validators.required],
      from: ['', Validators.required],
      to: [''],
      cc: [''],
      statuscandidate: ['', Validators.required],
      mailtemplate: ['', Validators.required],
      filtercandidate: [''],
      filterMailStatus: [''],
      active: ['']
    });
    this.service.getById(this.id).subscribe(data => {
      data.to.forEach(getToEmail => {
        if (getToEmail !== '') {
          this.toEmails.push(getToEmail);
        }
      });
      data.cc.forEach(getCcEmail => {
        if (getCcEmail !== '') {
          this.ccEmails.push(getCcEmail);
        }
      });
      this.form.patchValue({
        id: data.id,
        title: data.title,
        from: data.from,
        statuscandidate: data.candidateStatus.id,
        mailtemplate: data.mailTemplate.id,
        active: data.active
      });
      this.statusCandidate = data.candidateStatus;
      if (data.active) {
        this.slideValue = 'Active';
      } else {
        this.slideValue = 'Inactive';
      }
      this.loadCandidate();
      this.loadMail();
      this.enableSave();
    }, () => { });
  }
  loadCandidate(): void {
    this.statusCandidateService.getListStatusMailNotUsed({ sortDirection: 'asc', sortByField: 'title', filter: '' }).subscribe((data) => {
      this.candidates = [];
      data.candidateStatus.forEach(x => {
        this.candidates.push(x);
      });
      this.candidates.push(this.statusCandidate);
    }, () => { });
  }
  loadMail(): void {
    this.mailTemplateService.getList(1, 100).subscribe(data => {
      this.mailTemplates = data.contents;
    }, () => { });
  }
  onFilterCandidate(): void {
    if (this.form.get('filtercandidate').value === null || this.form.get('filtercandidate').value === '') {
      this.candidates = [];
      this.statusCandidateService.getListStatusMailNotUsed({ sortDirection: 'asc', sortByField: 'title', filter: '' }).subscribe((data) => {
        data.candidateStatus.forEach(getCandidate => {
          this.candidates.push(getCandidate);
        });
        this.candidates.push(this.statusCandidate);
      }, () => { });
    } else if (this.form.get('filtercandidate').value.length >= 3) {
      this.candidates = [];
      this.statusCandidateService.getListStatusMailNotUsed({ sortDirection: 'asc', sortByField: 'title', filter: '' }).subscribe((data) => {
        data.candidateStatus.forEach(x => {
          this.candidates.push(x);
        });
        this.candidates.push(this.statusCandidate);
        this.candidates = this.candidates.filter(name => name.title.toLocaleLowerCase().includes(this.form.get('filtercandidate').value));
      }, () => { });
    }
  }
  onFilterMail(): void {
    if (this.form.get('filterMailStatus').value === null || this.form.get('filterMailStatus').value === '') {
      this.mailTemplateService.getList(1, 100).subscribe((respone) => {
        this.mailTemplates = respone.contents;
      }, () => { });
    } else if (this.form.get('filterMailStatus').value.length >= 3) {
      this.mailTemplateService.getList(1, 100, this.form.get('filterMailStatus').value, 'asc', 'subject').subscribe((respone) => {
        this.mailTemplates = respone.contents;
      }, () => { });
    }
  }
  clearTitle(): void {

    this.form.controls.title.setValue('');
  }
  clearFrom(): void {

    this.form.controls.from.setValue('');
  }
  clearTo(): void {

    this.form.controls.to.setValue('');
  }
  clearCc(): void {
    this.form.controls.cc.setValue('');
  }
  onChange(): void {

    if (this.form.get('active').value === false) {
      this.slideValue = 'Active';
    }
    else {
      this.slideValue = 'Inactive';
    }
  }
  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if (value !== '') {
      this.chipListTo.errorState = !this.validateEmail(value);

    }
    if (this.validateEmail(value)) {
      input.value = '';
      this.toEmails.push(value);
      this.form.controls.to.setValue('');
    }

  }
  addCc(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    if (value !== '') {
      this.chipList.errorState = !this.validateEmail(value);

    }
    if (this.validateEmail(value)) {
      input.value = '';
      this.ccEmails.push(value);
      this.form.controls.cc.setValue('');
    }
  }
  remove(i): void {
    const index = this.toEmails.indexOf(i);
    if (index >= 0) {
      this.toEmails.splice(index, 1);
    }
    this.enableSave();
  }
  removeCc(i): void {
    const index = this.ccEmails.indexOf(i);
    if (index >= 0) {
      this.ccEmails.splice(index, 1);
    }
  }
  checkCc() {
    if (this.form.controls.cc.value === ' ') {
      this.chipList.errorState = false;
    }
  }
  validateEmail(email) {
    // eslint-disable-next-line max-len
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }
  back(): void {
    this.pageHistory.back();
  }
}
