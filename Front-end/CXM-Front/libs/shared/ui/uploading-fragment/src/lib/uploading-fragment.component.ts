import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Output, Renderer2,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

export enum FileExtension {
  "PDF" = ".pdf",
  "CSV" = ".csv"
}

@Component({
  selector: 'cxm-smartflow-uploading-fragment',
  templateUrl: './uploading-fragment.component.html',
  styleUrls: ['./uploading-fragment.component.scss'],
})
export class UploadingFragmentComponent
  implements OnDestroy, OnChanges, OnInit {
  @Input() acceptExtension = '.pdf';
  showName = false;
  @Output() fileChange = new EventEmitter<FileList>();
  @Output() resetUploadFile = new EventEmitter<any>();
  @Input() fileName = '';
  @Input() fileSize = '';
  @Input() isHidden = false;
  @Input() isDisabled = false;
  @Input() icon = 'assets/icons/icon-file-pdf.png';
  @Input() uploadedIcon = 'assets/images/pdf2.png';
  @ViewChild('fileUpload') fileUpload: any;
  @ViewChild('fragmentContainer') fragmentContainer: any;
  private _destroy$ = new Subject<boolean>();
  private _dragAction$ = new Subject<any>();

  @Input() offsetX: null | number;
  @Input() width: string;
  @Input() truncateFilenameWidth = 0.7;
  @Input() fragmentStyles= '';
  @Input() isIncludeExtension= false;

  dragOpacity = '1';
  buttonOffsetX = '40%';
  titleOffsetX = '30%';
  isLeave = false;

  private _fg: FormGroup;
  private _unsubscribe: Subscription;
  constructor(private _fb: FormBuilder, private _renderer: Renderer2) {
    this._fg = _fb.group({
      files: new FormControl(),
    });

    this._unsubscribe = this._fg.valueChanges.subscribe((data) => {
      this.fileChange.emit(data.files);
    });

    this._dragAction$.pipe(takeUntil(this._destroy$)).subscribe((data) => {
      this.playAnimate(data.offsetY);
    });
  }
  ngOnInit(): void {
    this.resetStyle();
  }

  ngOnDestroy(): void {
    this._unsubscribe.unsubscribe();
    this._destroy$.next(true);
    this._dragAction$.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.showName = !!this.fileName && this.fileName.length > 0;
    this.resetStyle();

    this._disableFragment();
  }

  onFileSelected($event: any) {
    const fileList = $event.target.files as FileList;
    this._fg.controls['files'].patchValue(fileList);
    this.resetFile();
  }

  onDropFile(fileList: FileList) {
    this._fg.controls['files'].patchValue(fileList);
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
    this._dragAction$.next({ offsetY });
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

  private _disableFragment(): void {
    if (this.fragmentContainer?.nativeElement) {
      const div = this.fragmentContainer?.nativeElement;
      const elements = div.getElementsByTagName('*');

      Array.from(elements).forEach((element) => {
        this._renderer.setProperty(element, 'disabled', this.isDisabled);
      });
    }
  }

  getWidth(container: HTMLDivElement): string {
    return `${container.clientWidth * this.truncateFilenameWidth}px !important`;
  }

  replaceExtension(text: string) {
    return text?.replace('${extension}', this.isIncludeExtension ? this.acceptExtension : '');
  }
}
