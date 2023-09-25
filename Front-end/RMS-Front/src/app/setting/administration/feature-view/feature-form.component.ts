import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { ModuleService } from 'src/app/core';
import { FeatureModule } from 'src/app/core/model/user-role.model';

@Component({
  selector: 'app-feature-form',
  templateUrl: './feature-form.component.html',
  styleUrls: ['./feature-form.component.css'],
})
export class FeatureFormComponent implements OnChanges {
  @Input() feature: FeatureModule;
  @Input() editMode: boolean;
  model: FeatureModule;

  @Output() onsuccess = new EventEmitter();
  @Output() oncancel = new EventEmitter();

  constructor(private featureService: ModuleService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.feature) {
      this.model = changes.feature.currentValue;
    }
  }

  remove(): void {
    if (this.editMode) {
      this.featureService
        .delete(this.model.id)
        .subscribe(() => this.onsuccess.emit());
    }
  }

  saveChange(): void {
    if (this.editMode) {
      this.featureService
        .update(this.model.id, this.model)
        .subscribe(() => this.onsuccess.emit());
    } else {
      this.featureService
        .create(this.model)
        .subscribe(() => this.onsuccess.emit());
    }
  }
}
