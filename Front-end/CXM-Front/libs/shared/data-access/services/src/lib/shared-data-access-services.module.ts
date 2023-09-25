import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MaterialModule } from '@cxm-smartflow/shared/material';
import { SnackBarService } from './snack-bar/snack-bar.service';
import { CanModificationService } from './can-modification';
import { HttpClientModule } from '@angular/common/http';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { CanVisibilityService } from './can-visibility';
import { UserRightService } from './user-right';
import { CanAccessibilityService } from './can-accessability';
import { IconSnackbarComponent } from './snack-bar/icon-snackbar.component';
import { UserPermissionService } from './user-permission';

@NgModule({
  imports: [
    CommonModule,
    MaterialModule,
    HttpClientModule,
    TranslateModule,
  ],
  providers: [
    MatSnackBar,
    SnackBarService,
    CanModificationService,
    CanVisibilityService,
    UserRightService,
    CanAccessibilityService,
    UserPermissionService,
    TranslateService
  ],
  declarations: [IconSnackbarComponent],
})
export class SharedDataAccessServicesModule {}
