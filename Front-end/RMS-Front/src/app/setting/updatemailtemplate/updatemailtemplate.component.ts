import { MessageService } from './../../core/service/message.service';
import { MailTemplateModel } from './../../core/model/Mailtemplate';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { MailtemplateService } from '../../core/service/Mailtemplate.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { IsLoadingService } from '@service-work/is-loading';
import { QuillEditorComponent } from 'ngx-quill';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-updatemailtemplate',
  templateUrl: './updatemailtemplate.component.html',
  styleUrls: ['./updatemailtemplate.component.scss'],
})
export class UpdatemailtemplateComponent implements OnInit {
  slidevalue = 'active';
  shouldShowError = false;
  form: FormGroup;
  id: number;
  action: boolean;
  selectedValue = 'Option1';
  lastSelectIndex = 0;
  mailTemplate: KeyValue<string, any>[] = [];
  @ViewChild(QuillEditorComponent, { static: true })
  editor: QuillEditorComponent;

  @ViewChild('dialogElement', { static: false }) dialogElement: ElementRef;
  contextMenuPosition: any;
  buttonClick = false;

  constructor(
    private formbuilder: FormBuilder,
    private message: MessageService,
    private actRoute: ActivatedRoute,
    private service: MailtemplateService,
    public dialogRef: MatDialogRef<UpdatemailtemplateComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: MailTemplateModel,
    private isloadingService: IsLoadingService,
  ) {}

  btnsave(): void {
    if (this.form.invalid) {
      this.shouldShowError = true;
      return;
    }
    const subscription = this.service
      .update(this.id, this.form.value)
      .subscribe(
        () => {
          this.message.showSuccess('Update Sucess', 'Update Mail Template');
          this.onNoClick();
        },
        () => {
          this.message.showError('Update Fail', 'Update Mail Template');
        },
      );
    this.isloadingService.add(subscription, {
      key: 'UpdatemailtemplateComponent',
      unique: 'UpdatemailtemplateComponent',
    });
  }

  ngOnInit(): void {
    this.getMailTemplateBody();
    this.form = this.formbuilder.group({
      id: [''],
      subject: ['', Validators.required],
      body: ['', Validators.required],
      active: [''],
    });
    this.form.setValue({
      subject: this.data.subject,
      body: this.data.body,
      id: this.data.id,
      active: this.data.active,
    });
    this.action = this.data.active;
    if (this.action) {
      this.slidevalue = 'Active';
    } else {
      this.slidevalue = 'Inactive';
    }
  }

  onChange(): void {
    if (!this.form.get('active').value) {
      this.slidevalue = 'Active';
    } else {
      this.slidevalue = 'Inactive';
    }
  }

  clearSubject(): void {
    this.form.controls.subject.setValue('');
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  changeSelection(event: string) {
    this.buttonClick = !this.buttonClick;
    this.editor.quillEditor.insertText(this.lastSelectIndex, event);
    this.form.patchValue({ body: this.editor.quillEditor.root.innerHTML });
  }

  getIndex() {
    const index = this.editor.quillEditor?.getSelection()?.index;
    if (index !== undefined) {
      this.lastSelectIndex = index;
    }
  }

  getMailTemplateBody() {
    this.service.getMailTemplateBody().subscribe((data) => {
      const remainder = ['title', 'description'];
      const candidate = ['candidate_name', 'candidate_link', 'date_reminder'];
      const interview = ['interview_title', 'interview_link', 'date_interview'];

      for (const [index, mailTem] of data.entries()) {
        switch (true) {
          case remainder.includes(mailTem.value):
            data[index].value = `${mailTem.value} from reminder`;
            break;
          case candidate.includes(mailTem.value):
            data[index].value = `${mailTem.value} from candidate`;
            break;
          case interview.includes(mailTem.value):
            data[index].value = `${mailTem.value} from interview`;
            break;
        }
      }

      this.mailTemplate = data;
    });
  }

  rightClick(event: MouseEvent) {
    event.preventDefault();
    this.buttonClick = !this.buttonClick;
    this.contextMenuPosition = { x: event.clientX, y: event.clientY };
    const height = this.dialogElement.nativeElement.clientHeight;

    if (window.innerWidth > 1350 && window.innerWidth < 1650) {
      this.contextMenuPosition.x = '85px';
    } else {
      if (event.clientX > 600 && event.clientX < 850) {
        this.contextMenuPosition.x = (event.clientX - 600) * 0.4 + 'px';
      } else if (event.clientX > 850 && event.clientX < 1200) {
        this.contextMenuPosition.x = 285 + (event.clientX - 800) * 0.4 + 'px';
      } else if (event.clientX > 1201) {
        this.contextMenuPosition.x = 325 + (event.clientX - 1100) * 0.4 + 'px';
      }
    }


    //height display
    if (height >= 300 && height <= 700) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 650 + 'px';
    } else if (height >= 701 && height <= 850) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 610 + 'px';
    } else if (height >= 851 && height <= 920) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 590 + 'px';
    } else if (height >= 921 && height <= 1000) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 535 + 'px';
    } else if (height >= 1001 && height <= 1200) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 520 + 'px';
    } else if (height >= 1201 && height <= 1500) {
      this.contextMenuPosition.y = this.contextMenuPosition.y - 510 + 'px';
    }
  }
}
