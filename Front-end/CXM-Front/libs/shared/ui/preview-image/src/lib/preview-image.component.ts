import { Component, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DomSanitizer, SafeResourceUrl, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { ApiService, ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { HttpEvent, HttpEventType, HttpParams } from '@angular/common/http';
import { ApiEnvironment, PreviewEnvAdapter } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-preview-image',
  templateUrl: './preview-image.component.html',
  styleUrls: ['./preview-image.component.scss']
})
export class PreviewImageComponent extends PreviewEnvAdapter implements OnDestroy {

  imageSource$ = new BehaviorSubject<SafeResourceUrl>({});
  loading$ = new BehaviorSubject(false);
  fileName: string;

  constructor(
    private titleService: Title,
    private route: ActivatedRoute,
    private service: ApiService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private configuration: ConfigurationService,
    private _sanitizer: DomSanitizer
  ) {
    super();
    this.route.queryParams.subscribe((params) => {
      const { docName, fileId, apiType } = params;
      // Set tab title.
      this.titleService.setTitle(docName);
      this.fileName = docName;
      const settings = configuration.getAppSettings();

      PreviewImageComponent.environment(
        apiType as number,
        fileId as string,
        settings
      ).subscribe((env: ApiEnvironment) => {
        if (settings.apiGateway && env.contextPath) {
          this.service
            .downloadBase64(env.contextPath, new HttpParams())
            .subscribe(
              (httpEvent: HttpEvent<any>) => {
                if (httpEvent.type === HttpEventType.Sent) {
                  this.loading$.next(true);
                }
                if (httpEvent.type === HttpEventType.ResponseHeader) {
                  this.loading$.next(true);
                }
                if (httpEvent.type === HttpEventType.DownloadProgress) {
                  this.loading$.next(true);
                }
                if (httpEvent.type === HttpEventType.Response) {
                  setTimeout(() => {
                    this.loading$.next(false);
                    this.imageSource$.next(this._sanitizer.bypassSecurityTrustResourceUrl('data:image/jpg;base64,'
                      + httpEvent.body));
                  }, 1000);
                }
              },
              () => {
                this.translate
                  .get('globalMessage')
                  .toPromise()
                  .then((message) => {
                    setTimeout(() => {
                      this.loading$.next(false);
                      this.snackBarService.openCustomSnackbar({
                        message: message?.downloadFileFail,
                        type: 'error',
                        icon: 'close'
                      });
                    }, 1000);
                  });
              }
            );
        }
      });
    });
  }

  ngOnDestroy(): void {
    this.imageSource$.unsubscribe();
    this.loading$.unsubscribe();
  }
}
