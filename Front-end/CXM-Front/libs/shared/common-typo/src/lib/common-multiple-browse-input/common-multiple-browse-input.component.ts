import {
  AfterContentInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { Observable, of } from 'rxjs';
import { FileValidator, FileValidatorUtil } from './file-validator-util';
import { CustomFileModel } from '@cxm-smartflow/shared/data-access/model';

@Component({
  selector: 'cxm-smartflow-common-multiple-browse-input',
  templateUrl: './common-multiple-browse-input.component.html',
  styleUrls: ['./common-multiple-browse-input.component.scss']
})
export class CommonMultipleBrowseInputComponent implements OnChanges, AfterContentInit {

  // Input properties.
  @Input() fileValidator: FileValidator = { fileSize: 5, fileLimit: 4 };
  @Input() filePropertiesOfAPI: CustomFileModel [] = [];
  @Input() acceptType: string;

  // Output events.
  @Output() removingFile = new EventEmitter<CustomFileModel | null>();
  @Output() choosingOneFile = new EventEmitter<CustomFileModel | null>();
  @Output() choosingMultipleFile = new EventEmitter<CustomFileModel []>();

  internalFileProperties: CustomFileModel [] = [];
  acceptableType = '*.*';
  totalFileSizeUploaded = 0;
  firstInitial = 0;

  // Css properties.
  cssBrowseInput = 'none';

  @ViewChild('fileInput') fileInput: ElementRef;

  constructor(private snackBar: SnackBarService, private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes?.filePropertiesOfAPI) {
      // Reset internal file properties.
      this.internalFileProperties = [];
      // Always set new files.
      this.internalFileProperties.push(...this.filePropertiesOfAPI);
      this.initTotalFileSize(this.filePropertiesOfAPI);
    }

    if(changes?.acceptType){
      this.acceptableType = this.acceptType;
    }
    this.firstInitial++;
  }

  initTotalFileSize(allFileExist: CustomFileModel[]): void {
    if (this.firstInitial == 1) {
      if (allFileExist.length) {
        const allFileSize = Array.from(allFileExist)?.map(file => Number((file as CustomFileModel)?.fileSize))
          ?.reduce((previousValue: number, currentValue: number) => previousValue + currentValue);
        this.totalFileSizeUploaded = this.getTotalFileSize(allFileSize);
      }
    }
  }

  chooseFile(event: any) {
    event?.preventDefault();
    this.uploadOnceFiles((event.target?.files) as FileList)
      .subscribe((files: File[]) => {
        if (files?.length === 0) return;
        // Emit value all files one time.
        this.choosingMultipleFile.emit(files);
      });

    // Clear file input.
    this.fileInput.nativeElement.value = null;
  }

  handleFileDrop(event: any) {
    this.cssBrowseInput = 'none';

    this.uploadOnceFiles(event as FileList)
      .subscribe((files: File[]) => {
        if (files?.length === 0) return;
        // Emit value all files one time.
        this.choosingMultipleFile.emit(files);
      });
  }

  handleFileOver() {
    this.cssBrowseInput = '1px solid gray';
  }

  removeFile(index: number, file: CustomFileModel, event: any) {
    event?.preventDefault();
    if (file) {
      this.removeFileSize(Number(file.fileSize));
      this.removingFile.emit(file);
    }
  }
  removeFileSize(fileSize: number): void {
    this.totalFileSizeUploaded -= fileSize;
  }

  getTotalFileSize(fileSize: number): number {
    this.totalFileSizeUploaded += fileSize;
    return this.totalFileSizeUploaded;
  }

  /**
   * Emit output value to parent of each files.
   * Validate with, file limit, file size, and file extension.
   * @param fileList
   * @private array of {@link File}
   */
  private uploadOnceFiles(fileList: FileList): Observable<File[]> {
    // Validate file length with file limit.
    if ((fileList?.length > (this.fileValidator.fileLimit || 4)) || ((fileList.length + this.internalFileProperties.length) > (this.fileValidator.fileLimit || 4))) {
      this.snackBar.openCustomSnackbar({
        message: this.fileValidator.fileLimitMessage,
        type: 'error',
        icon: 'close'
      });
      return of([]);
    }

    // Validate total files size.
    const uploadFileSize = Array.from(fileList)?.map(value => (value as File)?.size)
      ?.reduce((previousValue: number, currentValue: number) => previousValue + currentValue);

    if (FileValidatorUtil.bytesToMegaBytes(this.getTotalFileSize(uploadFileSize)) > (this.fileValidator.fileSize || 5)) {
      this.removeFileSize(uploadFileSize);
      this.snackBar.openCustomSnackbar({
        message: this.fileValidator.fileSizeMessage,
        type: 'error',
        icon: 'close'
      });
      return of([]);
    }

    // Validate file extensions.
    const totalExtensionValid = Array.from(fileList)?.map((value: File) =>
      FileValidatorUtil.getFileExtension().includes(<string>value?.name?.split('.')?.pop()?.toLowerCase()))
      .filter(extensionStatus => extensionStatus);

    if (totalExtensionValid.length < fileList.length) {
      this.snackBar.openCustomSnackbar({
        message: this.fileValidator.fileExtensionMessage,
        type: 'error',
        icon: 'close'
      });
      return of([]);
    }

    // It always new content of multiple files.
    const multipleFile: CustomFileModel [] = [];

    Array.from(fileList)?.forEach((file: File) => {
      // validate file limit with internal file properties.
      if (this.internalFileProperties.length === this.fileValidator.fileLimit) {
        this.snackBar.openCustomSnackbar({
          message: this.fileValidator.fileSizeMessage,
          type: 'error',
          icon: 'close'
        });
        return;
      }

      // Validate duplicate filename.
      if(this.fileValidator.checkDuplicate){
        if(this.validateDuplicate(file)){
          this.snackBar.openCustomSnackbar({
            message: this.fileValidator.duplicateMessage,
            type: 'error',
            icon: 'close'
          });
          return;
        }
      }

      // Accept only file limit.
      if (this.internalFileProperties.length <= (this.fileValidator.fileLimit || 4)) {
        // Set new multiple files.
        multipleFile.push(file as File);
        // Emit value once a file.
        this.choosingOneFile.emit(file as File);
      }
    });

    return of(multipleFile);
  }

  private validateDuplicate(file: File): boolean {
    return this.internalFileProperties?.map(item => item.filename)?.some(filename => filename === file.name);
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  ngAfterContentInit(): void {
    this.changeDetectorRef.detectChanges();
  }
}
