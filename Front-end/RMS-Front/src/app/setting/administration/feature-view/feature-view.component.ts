import { Component, Inject, OnInit } from '@angular/core';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { ModuleService, PrivilegeModel } from 'src/app/core';
import { FeatureModule } from 'src/app/core/model/user-role.model';

@Component({
  selector: 'app-feature-view',
  templateUrl: './feature-view.component.html',
  styleUrls: ['./feature-view.component.css'],
})
export class FeatureViewComponent implements OnInit {
  features: PrivilegeModel[];

  constructor(
    private featureService: ModuleService,
    public dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadFeature();
  }

  create(): void {
    const newFeature: FeatureModule = {
      active: true,
      description: '',
      name: '',
      id: 0,
      permission: [],
    };

    const dailogRef = this.dialog.open(FeatureFormDialogComponent, {
      data: newFeature,
      width: '400px',
      disableClose: true,
      panelClass: 'overlay-scrollable',
    });

    dailogRef.afterClosed().subscribe((result) => {
      if (result && result.changed) {
        this.loadFeature();
      }
    });
  }

  loadFeature(): void {
    this.featureService.get().subscribe((x) => (this.features = x.contents));
  }
}

@Component({
  selector: 'app-feature-form-dialog',
  templateUrl: './feature-form-dialog.component.html',
})
export class FeatureFormDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<FeatureFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: FeatureModule,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  success(): void {
    this.dialogRef.close({ changed: true });
  }
}
