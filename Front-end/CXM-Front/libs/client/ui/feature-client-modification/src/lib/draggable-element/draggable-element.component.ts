import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges
} from '@angular/core';
import { ConfigurationForm } from '@cxm-smartflow/client/data-access';
import {TranslateService} from "@ngx-translate/core";
import {BehaviorSubject, Observable} from "rxjs";

@Component({
  selector: 'cxm-smartflow-draggable-element',
  templateUrl: './draggable-element.component.html',
  styleUrls: ['./draggable-element.component.scss']
})
export class DraggableElementComponent implements OnChanges, AfterViewInit {

  @Input() configuration: ConfigurationForm;
  @Input() forceDisableDelete = false;
  @Input() forceDisableModify = false;

  @Output() deleteModel = new EventEmitter<ConfigurationForm>();
  @Output() modifyModel = new EventEmitter<ConfigurationForm>();

  modifiedButtonLabel = 'client.configuration_drag_modify';

  constructor(private _changeDetectorRef: ChangeDetectorRef) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (this.forceDisableDelete) {
      this.modifiedButtonLabel = 'client.configuration_drag_view';
    }
  }

  ngAfterViewInit(): void {
    this._changeDetectorRef.detectChanges();
  }

  modify(row: ConfigurationForm) {
    this.modifyModel.emit(row);
  }

  delete(row: ConfigurationForm) {
    this.deleteModel.emit(row);
  }

  addTitle(configuration: ConfigurationForm, inputElement: HTMLInputElement): string {
    return inputElement.clientWidth < inputElement.scrollWidth ? configuration.name : '';
  }


}
