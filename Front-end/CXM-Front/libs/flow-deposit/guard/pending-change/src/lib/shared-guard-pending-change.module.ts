import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PendingChangeGuard } from './pending-change.guard';
import { LockableFormGuardService } from './LockableFormGuard.service';
import { SharedUiComfirmationMessageModule } from '@cxm-smartflow/shared/ui/comfirmation-message';

@NgModule({
  imports: [CommonModule, SharedUiComfirmationMessageModule],
  providers: [PendingChangeGuard, LockableFormGuardService],
})
export class SharedGuardPendingChangeModule {}
