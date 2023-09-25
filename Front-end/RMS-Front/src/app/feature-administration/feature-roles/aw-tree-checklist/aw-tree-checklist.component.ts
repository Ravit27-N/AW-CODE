import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { TaskModel } from '../../../core/model/task.model';

@Component({
  selector: 'app-aw-tree-checklist',
  templateUrl: './aw-tree-checklist.component.html',
  styleUrls: ['./aw-tree-checklist.component.scss'],
})
export class AwTreeChecklistComponent implements OnInit {
  @Output() filterGroupResult = new EventEmitter<any>();
  @Input() filterGroup: TaskModel = {
    name: 'Activity',
    completed: false,
    subtasks: [
      { name: 'viewAble', title: 'view', completed: false },
      { name: 'insertAble', title: 'create', completed: false },
      { name: 'editAble', title: 'modify', completed: false },
      { name: 'deleteAble', title: 'delete', completed: false },
    ],
  };
  isShow: boolean;
  filterFormGroup: FormGroup;

  constructor(private formBuilder: FormBuilder) {
    this.filterFormGroup = this.formBuilder.group({});
  }

  ngOnInit(): void {
    this.initFilterFormGroup();
    this.parentChangeValue();
    this.formChanged();
  }

  initFilterFormGroup(): void {
    this.filterFormGroup.addControl(
      this.filterGroup.name,
      new FormControl(
        this.filterGroup.subtasks.some((subtask) => subtask.completed),
      ),
    );
    this.filterGroup.subtasks.forEach((subtask) => {
      this.filterFormGroup.addControl(
        subtask.name,
        new FormControl(subtask.completed),
      );
    });
  }

  formChanged(): void {
    this.filterFormGroup.valueChanges.subscribe((task) => {
      const allSubtasksComplete = Object.values(task)
        .slice(1)
        .some((completed) => completed);
      if (allSubtasksComplete) {
        this.filterFormGroup
          .get(this.filterGroup.name)
          .setValue(allSubtasksComplete, {
            onlySelf: false,
            emitEvent: false,
          });
      } else {
        this.resetTaskForm();
      }
      this.filterGroupResult.emit(this.filterFormGroup.value);
    });
  }

  resetTaskForm(): void {
    this.filterFormGroup.get(this.filterGroup.name).setValue(false, {
      onlySelf: false,
      emitEvent: false,
      nonNullable: true,
    });
    this.filterGroup.subtasks.forEach((subtask) => {
      subtask.completed = false;
      this.filterFormGroup.get(subtask.name).setValue(false, {
        onlySelf: false,
        emitEvent: false,
        nonNullable: true,
      });
    });
  }

  parentChangeValue(): void {
    this.filterFormGroup
      .get(this.filterGroup.name)
      .valueChanges.subscribe((completed) => {
        this.filterGroup.subtasks.forEach((subtask) => {
          subtask.completed = completed;
          this.filterFormGroup.get(subtask.name).setValue(completed || false, {
            onlySelf: false,
            emitEvent: false,
            nonNullable: true,
          });
        });
      });
  }

  showItems() {
    this.isShow = !this.isShow;
  }
}
