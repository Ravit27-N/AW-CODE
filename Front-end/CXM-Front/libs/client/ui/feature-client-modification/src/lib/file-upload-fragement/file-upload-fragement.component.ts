import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import {FileValidator, FileValidatorUtil} from "@cxm-smartflow/shared/common-typo";

@Component({
  selector: 'cxm-smartflow-file-upload-fragement',
  templateUrl: './file-upload-fragement.component.html',
  styleUrls: ['./file-upload-fragement.component.scss']
})
export class FileUploadFragementComponent {

  @ViewChild('fileUpload') csvFile: any;
  @Input() fileValidator: FileValidator = { fileSize: 5 };
  @Output() onDropFile = new EventEmitter<any>();

  dragOpacity = '1';
  buttonOffsetX = '35%';
  titleOffsetX = '0%';

  handleDragover($event: DragEvent, isLeave?: boolean): void {
    if (isLeave) {

      // if (screen.availWidth === 1366) {
      //   this.dragOpacity = '1';
      //   this.buttonOffsetX = '35%';
      //   this.titleOffsetX = '-10%';
      // } else {
        this.dragOpacity = '1';
        this.buttonOffsetX = '42%';
        this.titleOffsetX = '0%';
      // }
    } else {

      if ($event.offsetY <= 30 || $event.offsetY >= 160) {
        this.buttonOffsetX = '30%';
        // this.titleOffsetX = '53%';
        this.titleOffsetX = '-5%';
        this.dragOpacity = '0.9';
      } else if ($event.offsetY <= 50 || $event.offsetY >= 150) {
        this.buttonOffsetX = '28%';
        this.dragOpacity = '0.7';
        this.titleOffsetX = '-10%';
      } else if ($event.offsetY <= 60 || $event.offsetY >= 140) {
        this.buttonOffsetX = '25%';
        this.dragOpacity = '0.6';
        this.titleOffsetX = '-15%';
      } else if ($event.offsetY <= 80 || $event.offsetY >= 100) {
        this.titleOffsetX = '-20%';
        this.buttonOffsetX = '10%';
        this.dragOpacity = '0.0';
      }
    }

  }

  private prepareFile(files: FileList) {
    if (files && files.length > 0) {
      if(files[0].type !== 'application/pdf') {
        this.translateService.get('client.formError.invalidPdfFile')
          .toPromise()
          .then(message => {
            this.snackbarService.openCustomSnackbar({
              icon: 'close',
              type: 'error',
              message,
            });
          });
        this.resetFile();
        return;
      }
      if (FileValidatorUtil.bytesToMegaBytes(files[0].size) > (this.fileValidator.fileSize || 5)) {
        this.snackbarService.openCustomSnackbar({
          message: this.fileValidator.fileSizeMessage,
          type: 'error',
          icon: 'close'
        });
        this.resetFile();
        return;
      }
      this.onDropFile.emit({ type: 'drop', files});
    }
  }

  onFileSelected(event: any) {
    this.prepareFile(event.target?.files);
  }

  onFileDrop(files: any) {
    this.prepareFile(files);
  }

  private resetFile() {
    this.csvFile.nativeElement.value = '';
  }

  constructor(private readonly translateService: TranslateService,
              private readonly snackbarService: SnackBarService) {}

}
