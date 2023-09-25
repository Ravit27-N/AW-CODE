import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

// @ts-ignore
import { FileInfoModel, TemporaryFileService } from '../../../core';
import { AwSnackbarService, Base64Util, FileUtil } from '../../../shared';

@Component({
  selector: 'app-add-candidate-step-fourth',
  templateUrl: './add-candidate-step-fourth.component.html',
  styleUrls: ['./add-candidate-step-fourth.component.scss'],
})
export class AddCandidateStepFourthComponent implements OnInit, OnChanges {
  @Input() candidateStep4: FileInfoModel = {
    fileId: '',
    fileSize: 0,
    resourceBase64: '',
    originalFilename: '',
  };
  @Input() maxFileSize: number;

  @Output() goPreviousPageEvent = new EventEmitter<void>();
  @Output() goNextPageEvent = new EventEmitter<string>();

  fileURL = '';
  uploadedIcon = `${document.baseURI}assets/icons/icon-file-pdf.png`;
  maxFileSizeAmount = '';

  constructor(
    private temporaryFileService: TemporaryFileService,
    private domSanitizer: DomSanitizer,
    private awSnackbarService: AwSnackbarService,
  ) {}

  ngOnInit(): void {
    this.updateFileURL();
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  ngOnChanges(changes: SimpleChanges) {
    this.updateFileURL();
    if (this.maxFileSize) {
      this.maxFileSizeAmount = FileUtil.formatFileSize(this.maxFileSize || 0);
    }
  }

  private updateFileURL(): void {
    if (this.candidateStep4 && this.candidateStep4.resourceBase64) {
      this.fileURL = Base64Util.convertToPDFUrl(
        this.candidateStep4.resourceBase64,
      );
    }
  }

  goPrevious(): void {
    this.goPreviousPageEvent.emit();
  }

  goNext(): void {
    if (!this.candidateStep4?.fileId) {
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: 'Please upload the attachment file, the CV is required!',
      });
      return;
    }
    this.goNextPageEvent.emit(this.candidateStep4.fileId);
  }

  async uploadCV($event: any): Promise<void> {
    const files = Array.from($event)[0] as File;
    if (files.size > this.maxFileSize) {
      this.awSnackbarService.openCustomSnackbar({
        type: 'error',
        icon: 'close',
        message: `The CV size cannot be exceed ${this.maxFileSizeAmount}`,
      });

      return;
    }
    try {
      const formData = new FormData();
      formData.append('fileUpload', files);
      this.candidateStep4 = await this.temporaryFileService
        .fileUpload(formData, 'pdf')
        .toPromise();
      localStorage.setItem(
        'candidate-form-step-4',
        JSON.stringify(this.candidateStep4),
      );
      this.updateFileURL();
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

  get urlSanitizer(): any {
    return this.domSanitizer.bypassSecurityTrustResourceUrl(this.fileURL);
  }

  resetFile(): void {
    this.fileURL = '';
    this.candidateStep4 = {
      fileId: '',
      originalFilename: '',
      fileSize: 0,
      resourceBase64: '',
    };
    localStorage.setItem(
      'candidate-form-step-4',
      JSON.stringify(this.candidateStep4),
    );
  }

  formatFileSize(fileSize: number): string {
    return fileSize ? FileUtil.formatFileSize(fileSize) : '';
  }
}
