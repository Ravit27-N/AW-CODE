import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LocalStorageService} from "./storages";

@NgModule({
  imports: [CommonModule],
  providers: [LocalStorageService]
})
export class FollowMyCampaignUtilModule {}
