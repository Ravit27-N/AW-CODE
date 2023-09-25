import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output,
  Renderer2,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-aw-upload-fragment',
  templateUrl: './aw-upload-fragment.component.html',
  styleUrls: ['./aw-upload-fragment.component.scss'],
})
export class AwUploadFragmentComponent implements OnInit, OnChanges, OnDestroy {
  @Input() acceptExtension = '.pdf';
  showName = false;
  @Output() fileChange = new EventEmitter<FileList>();
  @Output() resetUploadFile = new EventEmitter<any>();
  @Input() fileName = '';
  @Input() fileSize = '';
  @Input() isHidden = false;
  @Input() isDisabled = false;
  @Input() icon = `${document.baseURI}assets/icons/icon-file-pdf.png`;
  @Input() uploadedIcon = `${document.baseURI}assets/icons/icon-file-png.png`;
  @ViewChild('fileUpload') fileUpload: any;
  @ViewChild('fragmentContainer') fragmentContainer: any;
  private destroy$ = new Subject<boolean>();
  private dragAction$ = new Subject<any>();

  @Input() offsetX: null | number;
  @Input() width: string;
  @Input() truncateFilenameWidth = 0.7;

  dragOpacity = '1';
  buttonOffsetX = '40%';
  titleOffsetX = '30%';
  isLeave = false;

  private formGroup: FormGroup;
  private unsubscribe: Subscription;

  constructor(
    private formBuilder: FormBuilder,
    private renderer: Renderer2,
  ) {
    this.formGroup = formBuilder.group({
      files: new FormControl(),
    });

    this.unsubscribe = this.formGroup.valueChanges.subscribe((data) => {
      this.fileChange.emit(data.files);
    });

    this.dragAction$.pipe(takeUntil(this.destroy$)).subscribe((data) => {
      this.playAnimate(data.offsetY);
    });
  }

  ngOnInit(): void {
    this.resetStyle();
  }

  ngOnDestroy(): void {
    this.unsubscribe.unsubscribe();
    this.destroy$.next(true);
    this.dragAction$.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.showName = !!this.fileName && this.fileName.length > 0;
    this.resetStyle();

    this.disableFragment();
  }

  onFileSelected($event: any) {
    const fileList = $event.target.files as FileList;
    this.formGroup.controls.files.patchValue(fileList);
    this.resetFile();
  }

  onDropFile(fileList: FileList) {
    this.formGroup.controls.files.patchValue(fileList);
    this.resetFile();
  }

  resetFile(): void {
    this.fileUpload.nativeElement.value = '';
    this.resetStyle();
  }

  reset(): void {
    this.resetStyle();
    this.resetUploadFile.next(true);

    if (this.fileUpload) {
      this.fileUpload.nativeElement.value = '';
    }
  }

  handleDragover($event: any, isLeave: boolean) {
    const { offsetY } = $event;
    this.dragAction$.next({ offsetY });
    this.isLeave = isLeave;
  }

  playAnimate(offsetY: number): void {
    if (offsetY < 140 && offsetY > 1) {
      this.buttonOffsetX = this.offsetX ? this.offsetX + 100 + '%' : '55%';
      this.titleOffsetX = '45%';
      this.dragOpacity = '0.1';
    } else {
      this.resetStyle();
    }

    if (this.isLeave) {
      this.resetStyle();
    }
  }

  resetStyle(): void {
    this.dragOpacity = '1';
    this.buttonOffsetX = this.offsetX ? this.offsetX + 15 + '%' : '40%';
    this.titleOffsetX = this.offsetX ? this.offsetX + '%' : '30%';
  }

  private disableFragment(): void {
    if (this.fragmentContainer?.nativeElement) {
      const div = this.fragmentContainer?.nativeElement;
      const elements = div.getElementsByTagName('*');

      Array.from(elements).forEach((element) => {
        this.renderer.setProperty(element, 'disabled', this.isDisabled);
      });
    }
  }

  getWidth(container: HTMLDivElement): string {
    return `${container.clientWidth * this.truncateFilenameWidth}px !important`;
  }

  getFileName(fileName: string): string {
    return fileName.substring(fileName.indexOf('_') + 1);
  }
}
