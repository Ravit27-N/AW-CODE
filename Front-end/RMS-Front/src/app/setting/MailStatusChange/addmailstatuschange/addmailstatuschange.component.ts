import { MessageService } from './../../../core/service/message.service';
import { Router } from '@angular/router';
import { MailtemplateService } from './../../../core/service/Mailtemplate.service';
import { MailTemplateModel } from './../../../core/model/Mailtemplate';
import { StatusCandidateModel } from 'src/app/core';
import { StatusCandidateService } from './../../../core/service/status-candidate.service';
import { MailStatusChangeService } from './../../../core/service/mailStatusChange.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Component, HostListener, OnInit, ViewChild } from '@angular/core';
import { MailStatuschangeModel } from 'src/app/core/model/mailStatusChange';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent, MatChipList } from '@angular/material/chips';
import { Location } from '@angular/common';
import { IsLoadingService } from '@service-work/is-loading';
@Component({
  selector: 'app-addmailstatuschange',
  templateUrl: './addmailstatuschange.component.html',
  styleUrls: ['./addmailstatuschange.component.scss']
})
export class AddmailstatuschangeComponent implements OnInit {
  toEmails: string[] = [];
  ccEmails: string[] = [];
  selectable = true;
  removable = true;
  addOnBlur = true;
  searchCandidate: string;
  filtercandidate: string;
  slidevalue = 'Active';
  public form: FormGroup;
  haveToEmail = false;
  candidates: StatusCandidateModel[];
  mailTemplates: MailTemplateModel[];
  mail: MailTemplateModel;
  saveButtonEnable = true;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

  @ViewChild('chipListCc') chipList: MatChipList;
  @ViewChild('chipListTo') chipListTo: MatChipList;

  constructor(private formbuilder: FormBuilder, private pageHistory: Location, private service: MailStatusChangeService,
    private statusCandidateService: StatusCandidateService, private mailTemplateService: MailtemplateService,
    private router: Router, private message: MessageService, private isloadingService: IsLoadingService) { }

  @HostListener('document:click') enableSave(): void {
    if (this.form.get('to').touched === true && (this.form.get('to').value === '')) {
      this.chipListTo.errorState = false;
    }
    if (!this.form.invalid && this.toEmails.length > 0 && this.form.get('to').touched && this.form.get('to').value === '') {
      this.saveButtonEnable = false;
    }
    else {
      this.saveButtonEnable = true;
    }

  }

  btnsubmit(): void {
    this.mail = this.form.get('title').value;
    const payLoad: MailStatuschangeModel = {
      from: this.form.get('from').value,
      cc: this.ccEmails,
      mailTemplateId: this.form.get('mailtemplate').value,
      candidateStatusId: this.form.get('statuscandidate').value,
      title: this.form.get('title').value,
      to: this.toEmails,
      active: this.form.get('active').value
    };

    const subscription = this.service.create(payLoad).subscribe(() => {
      this.router.navigate(['/admin/setting/mailconfiguration/']);
      this.message.showSuccess('Sucess', 'Add Mail Configuration');
    }, () => {
      this.message.showError('Error Save', 'Add Mail Configuration');
    });
    this.isloadingService.add(subscription, { key: 'AddmailstatuschangeComponent', unique: 'AddmailstatuschangeComponent' });
  }

  ngOnInit(): void {
    this.form = this.formbuilder.group({
      title: ['', Validators.required],
      from: ['', [Validators.required, Validators.email, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      to: [''],
      cc: [''],
      statuscandidate: ['', Validators.required],
      mailtemplate: ['', Validators.required],
      active: [true],
      filterCandidates: [],
      filterMailStatus: []
    });
    this.loadCandidate();
    this.loadMail();
  }
  loadCandidate(): void {
    this.statusCandidateService.getListStatusMailNotUsed({ sortDirection: 'asc', sortByField: 'title', filter: '' }).subscribe((data) => {
      this.candidates = data.candidateStatus;
    }, () => { });
  }
  loadMail(): void {
    this.mailTemplateService.getListOnActive(1, 100).subscribe(data => {
      this.mailTemplates = data.contents;
    }, () => { });
  }
  onFilterCandidate(): void {
    if (this.form.get('filterCandidates').value === null || this.form.get('filterCandidates').value === '') {
      this.statusCandidateService.getListStatusMailNotUsed({ sortDirection: 'asc', sortByField: 'title', filter: '' })
      .subscribe((respone) => {
        this.candidates = respone.candidateStatus;
      }, () => { });
    } else if (this.form.get('filterCandidates').value.length >= 3) {
      this.statusCandidateService.getListStatusMailNotUsed(this.form.get('filterCandidates').value).subscribe((respone) => {
        this.candidates = respone.candidateStatus;
      }, () => { });
    }
  }

  onFilterMail(): void {
    if (this.form.get('filterMailStatus').value === null || this.form.get('filterMailStatus').value === '') {
      this.mailTemplateService.getListOnActive(1, 100)
        .subscribe((respone) => {
          this.mailTemplates = respone.contents;
        }, () => { });
    } else if (this.form.get('filterMailStatus').value.length >= 3) {
      this.mailTemplateService.getList(1, 100, this.form.get('filterMailStatus').value, 'asc', 'subject').subscribe((respone) => {
        this.mailTemplates = respone.contents;
      }, () => { }
      );
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
    this.chipList.errorState = false;
    this.form.controls.cc.setValue('');
  }
  onChange(): void {
    if (this.form.get('active').value === false) {
      this.slidevalue = 'Active';
    }
    else {
      this.slidevalue = 'Inactive';
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
      this.haveToEmail = true;
      this.toEmails.push(value);
      this.form.controls.to.setValue('');
    }
  }
  addCc(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value.trim();
    if (value !== '') {
      this.chipList.errorState = !this.validateEmail(value);
    }
    if (this.validateEmail(value)) {
      input.value = '';
      this.ccEmails.push(value);
      this.form.controls.cc.setValue(' ');
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
