import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  CandidateService,
  StatusCandidateService,
  FileInfoModel,
} from '../../../core';
import { DomSanitizer } from '@angular/platform-browser';
import { Base64Util } from '../../../shared';
import { AddCandidateModel } from '../add-candidate.model';
import { Education } from '../add-candidate-step-two/add-candidate-step-two.model';
import { Experience } from '../add-candidate-step-three/add-candidate-step-three.model';
import { KeyValue } from '@angular/common';

@Component({
  selector: 'app-add-candidate-step-fifth',
  templateUrl: './add-candidate-step-fifth.component.html',
  styleUrls: ['./add-candidate-step-fifth.component.scss'],
})
export class AddCandidateStepFifthComponent implements OnInit, AfterViewInit {
  @Input() candidateStep1: AddCandidateModel;
  @Input() candidateStep2: { education: Education[]; form: any } = {
    form: {},
    education: [],
  };
  @Input() candidateStep3: { experiences: Experience[]; form: any } = {
    form: {},
    experiences: [],
  };
  @Input() candidateStep4: FileInfoModel;
  @Output() goPreviousPageEvent = new EventEmitter<void>();
  @Output() goNextPageEvent = new EventEmitter<string>();
  @Output() navigateToStep = new EventEmitter<number>();
  fileURL = '';
  form: FileInfoModel;
  editIconURL = `${document.baseURI}assets/icons/pencil-square.svg`;
  statusCriteria: KeyValue<any, any>[] = [];

  constructor(
    private statusCandidateService: StatusCandidateService,
    private domSanitizer: DomSanitizer,
    private changeDetector: ChangeDetectorRef,
  ) {}

  get urlSanitizer() {
    return this.domSanitizer.bypassSecurityTrustResourceUrl(this.fileURL);
  }

  async ngOnInit(): Promise<void> {
    const formStringify = localStorage.getItem('candidate-form-step-4');
    if (formStringify) {
      this.form = JSON.parse(formStringify);
      this.fileURL = Base64Util.convertToPDFUrl(this.form.resourceBase64);
    }

    await this.fetchStatusCriteria();
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

  goPrevious() {
    this.goPreviousPageEvent.emit();
  }

  goNext() {
    if (!this.form?.fileId) {
      return;
    }
    this.goNextPageEvent.emit(this.form.fileId);
  }

  ngAfterViewInit(): void {
    this.changeDetector.detectChanges();
  }

  getStatus(key: number) {
    return this.statusCriteria.find((item) => item.key === key)?.value || '';
  }

  getProfileURL() {
    const { profileFileBase64, profileFileExtension } = this.candidateStep1;
    return Base64Util.convertToImageUrl(
      profileFileBase64,
      `image/${profileFileExtension}`,
    );
  }
}
