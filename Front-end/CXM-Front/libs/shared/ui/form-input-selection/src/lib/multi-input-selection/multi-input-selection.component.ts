import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { InputSelectionCriteria } from '../input-selection/input-selection.component';
import { CustomAngularMaterialUtil } from '@cxm-smartflow/shared/utils';
import { BehaviorSubject } from 'rxjs';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

@Component({
  selector: 'cxm-smartflow-multi-input-selection',
  templateUrl: './multi-input-selection.component.html',
  styleUrls: ['./multi-input-selection.component.scss'],
})
export class MultiInputSelectionComponent implements OnChanges, OnDestroy {
  @Input() datasource: InputSelectionCriteria[] = [];
  @Input() selectSourceKey: any[] = [];
  @Input() unRemovableSourceKey: any[] = [];
  @Input() errorMsg = '';
  @Input() isShowError = false;
  @Input() addSourceBtn = '';
  @Input() lockModifyOverlayContainer = false;
  @Output() selectedSource = new EventEmitter<any[]>();
  @Output() removeUnRemovableSource = new EventEmitter<any>();
  selectionCriteria: InputSelectionCriteria[] = [];
  private formValue = new BehaviorSubject<any>({});


  ngOnChanges(changes: SimpleChanges): void {
    this.change(this.selectSourceKey);
  }

  ngOnDestroy(): void {
    this.formValue.unsubscribe();
  }

  change(keys: any[]): void {
    this.selectionCriteria = this.datasource.filter(element => keys.some(key => key == element.key));
    this.emit();
  }

  remove(key: any): void {

    if(this.unRemovableSourceKey.some(item => item == key)) {
      this.removeUnRemovableSource.emit(key);
      return;
    }

    this.selectionCriteria = this.selectionCriteria.filter(element => element.key != key);
    this.emit();
  }

  select(key: any): void {
    const found = this.selectionCriteria.findIndex(element => element.key == key);

    if (found == -1) {
      const element = this.datasource.find(element => element.key == key) as InputSelectionCriteria;
      this.selectionCriteria = [...this.selectionCriteria, element];
    } else {

      if(this.unRemovableSourceKey.some(item => item === key)) {
        this.removeUnRemovableSource.emit(key);
        return;
      }

      this.selectionCriteria = this.selectionCriteria.filter(element => element.key != key);
    }

    this.emit();
  }

  emit(): void {
    const emitData = this.selectionCriteria.map(element => element.key);
    this.selectedSource.emit(emitData);
  }

  setCustomStyle() {
    document
      .querySelector('.mat-menu-panel')
      ?.classList.add('common-mat-menu-panel');
  }

  mainMenuOpen() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomAngularMaterialUtil.decrease_cdk_overlay_container_z_index();
  }

  mainMenuClose() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomAngularMaterialUtil.increase_cdk_overlay_container_z_index();
  }

  getIsActive(key: string | number) {
    return this.selectionCriteria.some(element => element.key == key);
  }
  getDropdownItems(): InputSelectionCriteria[] {
    return this.datasource.filter(item => !this.selectionCriteria.some(sc => sc.key === item.key));
}

  order($event: any) {
    const { previousIndex, currentIndex } = $event;

    const collections = this.selectionCriteria;

    moveItemInArray(collections, previousIndex, currentIndex);

    this.selectionCriteria = collections;
    this.emit();
  }
}
