import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ConfigurationForm } from '@cxm-smartflow/client/data-access';

@Component({
  selector: 'cxm-smartflow-file-config',
  templateUrl: './file-config.component.html',
  styleUrls: ['./file-config.component.scss'],
})
export class FileConfigComponent {

  @Input() configurations: ConfigurationForm[];
  @Input() disableButtonAddConfig = false;

  @Output() deleteModel = new EventEmitter<ConfigurationForm>();
  @Output() modifyModel = new EventEmitter<ConfigurationForm>();
  @Output() orderModel = new EventEmitter<any>();
  @Output() addModel = new EventEmitter<boolean>();

  add() {
    this.addModel.emit(true);
  }

  order($event: any) {
    // The 5 refers to amount of elements cannot draggable count as index.
    const { currentIndex, previousIndex } = $event;
    this.orderModel.emit({ currentIndex: currentIndex + 4, previousIndex: previousIndex + 4 });
  }

  modify(row: ConfigurationForm) {
    this.modifyModel.emit(row)
  }

  delete(row: ConfigurationForm) {
    this.deleteModel.emit(row);
  }
}
