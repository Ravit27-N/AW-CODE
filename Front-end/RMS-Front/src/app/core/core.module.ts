import {
  ActivityService,
  AdvanceSearchService,
  ApiService,
  CandidateService,
  CompanyProfileService,
  GroupService,
  InterviewService,
  InterviewTemplateService,
  JobDescriptionService,
  MailconfigService,
  ModuleService,
  NotificationSubscriptionService,
  ReminderService,
  ResultService,
  RoleService,
  StatusCandidateService,
  TemporaryFileService,
  UserGroupService,
} from './service';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpErrorHandlerInterceptor } from './http';
import { AutofocusDirective } from './autfocus.directive';
import { NoAvailablePipe } from './NA-pipe';
import { FileManagerService } from './service/file-manager.service';

@NgModule({
  declarations: [NoAvailablePipe, AutofocusDirective],
  imports: [CommonModule],
  exports: [NoAvailablePipe, AutofocusDirective],
  providers: [
    ApiService,
    MailconfigService,
    NotificationSubscriptionService,
    StatusCandidateService,
    CandidateService,
    InterviewService,
    ActivityService,
    ReminderService,
    StatusCandidateService,
    InterviewTemplateService,
    ResultService,
    AdvanceSearchService,
    CompanyProfileService,
    UserGroupService,
    GroupService,
    ModuleService,
    RoleService,
    JobDescriptionService,
    FileManagerService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorHandlerInterceptor,
      multi: true,
    },
    TemporaryFileService,
    NoAvailablePipe,
  ],
})
export class CoreModule {}
