import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnDestroy,
  Output,
  SimpleChanges,
} from '@angular/core';
import { AwMultiSelectionBoxModel } from './aw-multi-selection-box.model';
import { BehaviorSubject, Subscription } from 'rxjs';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { CustomMaterialUiUtil } from '../../utils/custom-material-ui.util';

@Component({
  selector: 'app-aw-multi-selection-box',
  templateUrl: './aw-multi-selection-box.component.html',
  styleUrls: ['./aw-multi-selection-box.component.scss'],
})
export class AwMultiSelectionBoxComponent implements OnChanges, OnDestroy {
  @Input() datasource: AwMultiSelectionBoxModel[] = [];
  @Input() selectedKeys: any[] = [];
  @Input() unRemovableKeys: any[] = [];
  @Input() errorMsg = 'Error';
  @Input() isShowError = false;
  @Input() addSourceBtnLabel = '';
  @Input() lockModifyOverlayContainer = false;
  @Output() selectedSource = new EventEmitter<any[]>();
  @Output() removeUnRemovableSource = new EventEmitter<any>();

  selectionCriteria: AwMultiSelectionBoxModel[] = [];
  private formValueSubject = new BehaviorSubject<any>({});
  private formValueSubscription: Subscription | undefined;

  ngOnChanges(changes: SimpleChanges): void {
    if ('selectedKeys' in changes) {
      this.updateSelectionCriteria(changes.selectedKeys.currentValue);
    }
  }

  ngOnDestroy(): void {
    if (this.formValueSubscription) {
      this.formValueSubscription.unsubscribe();
    }
  }

  private updateSelectionCriteria(keys: any[]): void {
    this.selectionCriteria = this.datasource.filter((element) =>
      keys.some((key) => key === element.key),
    );
    this.emitSelection();
  }

  remove(key: any): void {
    if (this.unRemovableKeys.includes(key)) {
      this.removeUnRemovableSource.emit(key);
      return;
    }

    this.selectionCriteria = this.selectionCriteria.filter(
      (element) => element.key !== key,
    );
    this.emitSelection();
  }

  toggleSelection(key: any): void {
    if (this.unRemovableKeys.includes(key)) {
      this.removeUnRemovableSource.emit(key);
      return;
    }

    const index = this.selectionCriteria.findIndex(
      (element) => element.key === key,
    );

    if (index === -1) {
      const element = this.datasource.find(
        (item) => item.key === key,
      ) as AwMultiSelectionBoxModel;
      this.selectionCriteria.push(element);
    } else {
      this.selectionCriteria.splice(index, 1);
    }

    this.emitSelection();
  }

  private emitSelection(): void {
    const selectedKeys = this.selectionCriteria.map((element) => element.key);
    this.selectedSource.emit(selectedKeys);
  }

  setCustomStyle() {
    const matMenuPanel = document.querySelector('.mat-menu-panel');
    if (matMenuPanel) {
      matMenuPanel.classList.add('common-mat-menu-panel');
    }
  }

  mainMenuOpen() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomMaterialUiUtil.decreaseCdkOverlayContainerZIndex();
  }

  mainMenuClose() {
    if (this.lockModifyOverlayContainer) {
      return;
    }

    CustomMaterialUiUtil.increaseCdkOverlayContainerZIndex();
  }

  getIsActive(key: string | number) {
    return this.selectionCriteria.some((element) => element.key === key);
  }

  reorderItems(event: any) {
    const { previousIndex, currentIndex } = event;
    moveItemInArray(this.selectionCriteria, previousIndex, currentIndex);
    this.emitSelection();
  }
}
