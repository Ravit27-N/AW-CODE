import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { BehaviorSubject, interval } from 'rxjs';
import { take } from 'rxjs/operators';
import { CsvUploadComponentStore } from '../csv-upload-component.store';


export interface ICsvUploaderFragementEvent {
  type: 'drop' | 'fail';
  files: FileList
}

@Component({
  selector: 'cxm-smartflow-csv-uploader-fragement',
  templateUrl: './csv-uploader-fragement.component.html',
  styleUrls: ['./csv-uploader-fragement.component.scss'],
  providers: [CsvUploadComponentStore]
})
export class CsvUploaderFragementComponent implements OnInit {


  dragOpacity = '1';
  buttonOffsetX = '35%';
  titleOffsetX = '0%';

  @Output() ondropfile = new EventEmitter<ICsvUploaderFragementEvent>()

  isUploaded$ = new BehaviorSubject<boolean>(false);
  isUploading$ = new BehaviorSubject<boolean>(false);
  isLoadProgress$ = new BehaviorSubject<boolean>(false);
  isUploadFail$ = new BehaviorSubject<boolean>(true);

  @Input() errorMessage = '';

  @ViewChild('fileUpload') csvFile: any;

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

  onFileSelected(event: any) {
    this.prepareFile(event.target?.files);
  }

  onFileDrop(files: any) {
    this.prepareFile(files);
  }

  private prepareFile(files: FileList) {
    if (files && files.length > 0) {
      const fileName = files[0].name;
      const form = new FormData();
      Array.from(files).forEach(f => form.append('file', f));

      this.store.dropFile({ form, fileName });
      this.ondropfile.emit({ type: 'drop', files});
    }

    this.resetFile();
  }


  setAnimation(): void {

    this.isUploadFail$.next(true);
    this.isUploaded$.next(true);

    interval(1000).pipe(take(1)).subscribe(() => {
      this.isUploading$.next(true);
    });

    interval(2000).pipe(take(1)).subscribe(() => {
      this.isLoadProgress$.next(true);
    });
  }

  private resetFile() {
    this.csvFile.nativeElement.value = '';
  }

  ngOnInit(): void {
    this.store.state$.subscribe(s => {
      if(s.progressFileName) {
        this.isLoadProgress$.next(true);
      }
    })
  }

  constructor(private store: CsvUploadComponentStore) {
    //
   }
}
