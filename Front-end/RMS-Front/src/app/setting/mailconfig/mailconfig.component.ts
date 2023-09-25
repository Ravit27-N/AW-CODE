import {
  AfterViewInit,
  Component,
  EventEmitter,
  Inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { IsLoadingService } from '@service-work/is-loading';
import { BehaviorSubject, merge, Subject } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  skip,
  startWith,
  switchMap,
} from 'rxjs/operators';
import { ComfirmDailogComponent } from 'src/app/shared/components';
import { FilterOptions, MailconfigService } from '../../core';
import { SystemConfiguration } from '../../core/model/MailconfigFormModel';
import { formatApiDateWithoutTime } from '../../shared';

@Component({
  selector: 'app-mailconfig',
  templateUrl: './systemconfig.component.html',
  styleUrls: ['./mailconfig.component.css'],
})
export class SystemConfigComponent implements AfterViewInit {
  list: SystemConfiguration[] = [];
  displayedColumns = ['icon', 'key', 'value', 'description', 'action'];

  detectChange$ = new Subject<boolean>();
  searchTerm$ = new BehaviorSubject<string>('');

  constructor(
    private route: ActivatedRoute,
    private mailconfig: MailconfigService,
    public dialog: MatDialog,
    private isloadingService: IsLoadingService,
  ) {}

  ngAfterViewInit(): void {
    const observeSearchTerm$ = this.searchTerm$
      .asObservable()
      .pipe(debounceTime(300))
      .pipe(distinctUntilChanged())
      .pipe(skip(1));

    merge(this.detectChange$, observeSearchTerm$)
      .pipe(
        startWith({}),
        switchMap(() => {
          const filters: FilterOptions = {};

          if (this.searchTerm$.value && this.searchTerm$.value !== '') {
            filters.filter = this.searchTerm$.value;
          }
          filters.sortByField = 'configKey';
          this.isloadingService.add({ key: 'config', unique: 'config' });
          return this.mailconfig.getConfigList(undefined, undefined, filters);
        }),
      )
      .subscribe((data) => {
        this.isloadingService.remove({ key: 'config' });
        this.list = data.contents;
      });
  }

  add(): void {
    this.dialog
      .open(ConfigurationFormDialogComponent, {
        width: '800px',
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => this.detectChange$.next(result?.changed));
  }

  edit(config: SystemConfiguration): void {
    this.dialog
      .open(ConfigurationFormDialogComponent, {
        width: '800px',
        data: config,
        disableClose: true,
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => this.detectChange$.next(result?.changed));
  }

  delete(row: SystemConfiguration): void {
    this.dialog
      .open(ComfirmDailogComponent, {
        width: '450px',
        data: { title: row.configKey },
        panelClass: 'overlay-scrollable',
      })
      .afterClosed()
      .subscribe((result) => {
        if (result) {
          this.mailconfig
            .deleteConfig(row.id)
            .subscribe(() => this.detectChange$.next(true));
        }
      });
  }
}

@Component({
  selector: 'app-config-form',
  templateUrl: './configform.component.html',
  styleUrls: ['./mailconfig.component.css'],
})
export class ConfigurationFormComponent implements OnInit {
  @Input() model: SystemConfiguration;
  @Input() editing: boolean;

  @Output() oncancel = new EventEmitter();
  @Output() onupdateSuccess = new EventEmitter();

  validateForm: FormGroup;

  constructor(
    private mailConfigService: MailconfigService,
    private isloadingService: IsLoadingService,
  ) {}

  ngOnInit(): void {
    if (!this.model) {
      this.model = {
        configKey: '',
        configValue: '',
        description: '',
        dateActivity: formatApiDateWithoutTime(new Date()),
      };
    } else {
      this.editing = true;
    }

    this.validateForm = new FormGroup({
      key: new FormControl({
        value: this.model.configKey,
        disabled: this.editing,
      }),
    });
  }

  save(): void {
    const subscription = this.mailConfigService
      .saveConfig(this.model)
      .subscribe(() => this.onupdateSuccess.emit());
    this.isloadingService.add(subscription, {
      key: 'ConfigurationFormComponent',
      unique: 'ConfigurationFormComponent',
    });
  }

  cancel(): void {
    this.oncancel.emit();
  }
}

@Component({
  selector: 'app-config-form-dialog',
  templateUrl: './configform-dialog.component.html',
})
export class ConfigurationFormDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfigurationFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SystemConfiguration,
  ) {}

  success(): void {
    this.dialogRef.close({ changed: true });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
