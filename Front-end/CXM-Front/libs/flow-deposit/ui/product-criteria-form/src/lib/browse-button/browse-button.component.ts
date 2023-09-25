import {Component, OnInit, ChangeDetectionStrategy, Output, EventEmitter, Input} from '@angular/core';

@Component({
  selector: 'cxm-smartflow-browse-button',
  templateUrl: './browse-button.component.html',
  styleUrls: ['./browse-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class BrowseButtonComponent implements OnInit {

  @Input()
  isDisabled = false;
  file: File | null;
  @Output() choose = new EventEmitter<File | null>();

  @Input() placeholder = '';

  ngOnInit(): void {
  }

  choosefile(event: any) {
    event.preventDefault();
    if (event.target?.files && event.target.files.length > 0) {
      this.file = event.target?.files[0];
      this.choose.emit(this.file);
    }
  }

  clear() {
    this.file = null;
    this.choose.emit(null);
  }

  constructor() {
    //
   }
}
