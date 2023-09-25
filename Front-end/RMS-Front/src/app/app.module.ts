import { RemindersModule } from './reminders';
import { ActivitiesModule } from './activities';
import { CoreModule } from './core/core.module';
import { ToastContainerModule, ToastrModule } from 'ngx-toastr';
import { ImageCropperModule } from 'ngx-image-cropper';
import { SimpleNotificationsModule } from 'angular2-notifications';
import { RouterModule } from '@angular/router';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DefaultModule } from './layouts/default';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { APP_BASE_HREF, CommonModule, DatePipe } from '@angular/common';
import { MaterialModule } from './material';
import { AuthModule } from './auth';
import { SettingModule } from './setting/setting.module';
import { CandidateModule } from './candidate';
import { InterviewModule } from './interview';
import { ChartsModule } from 'ng2-charts';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { AppConfigService, ErrorMessageHandler } from './core';
import { NgxFileDragDropModule } from 'ngx-file-drag-drop';
import { CalendarModule } from './calendar';
import { IsLoadingModule } from '@service-work/is-loading';
import { environment } from '../environments/environment';
import { SharedModule } from './shared';
import { initializeApp } from 'firebase/app';
import { DemandModule } from './feature-dashboard';
import { FeatureAdministrationModule } from './feature-administration/feature-administration.module';

initializeApp(environment.firebase);

@NgModule({
  declarations: [AppComponent],
  imports: [
    CommonModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FlexLayoutModule,
    DefaultModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    HttpClientModule,
    SimpleNotificationsModule,
    ImageCropperModule,
    ToastrModule.forRoot({}),
    ToastContainerModule,
    NgxFileDragDropModule,
    CoreModule,
    SettingModule,
    CandidateModule,
    ActivitiesModule,
    AuthModule,
    InterviewModule,
    RemindersModule,
    CalendarModule,
    ChartsModule,
    SlickCarouselModule,
    IsLoadingModule,
    DemandModule,
    SharedModule,
    FeatureAdministrationModule,
  ],
  providers: [
    DatePipe,
    AppConfigService,
    ErrorMessageHandler,
    {
      provide: APP_BASE_HREF,
      useValue: environment.basePath,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
