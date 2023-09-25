import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { UserPayload } from 'src/app/core/model/user-admin.model';
import { UserAdminService } from '../../../core/service/user-admin.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css'],
})
export class UserFormComponent implements OnInit, OnChanges {
  @Input() user: UserPayload;
  @Input() editMode: boolean;

  @Output() onsuccess = new EventEmitter();
  @Output() ondelete = new EventEmitter();

  model: UserPayload;

  constructor(private service: UserAdminService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.user) {
      this.model = changes.user.currentValue;
    }
  }

  ngOnInit(): void {
    this.model = this.user;
  }

  save(): void {
    if (this.editMode) {
      this.service.update(this.model).subscribe(() => this.onsuccess.emit());
    } else {
      this.service.create(this.model).subscribe(() => this.onsuccess.emit());
    }
  }
}
