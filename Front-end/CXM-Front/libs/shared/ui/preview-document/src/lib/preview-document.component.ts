import { HttpEvent, HttpEventType, HttpParams } from '@angular/common/http';
import { Component, OnDestroy } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { ApiService, ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { SnackBarService } from '@cxm-smartflow/shared/data-access/services';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { ApiEnvironment, PreviewEnvAdapter } from '@cxm-smartflow/shared/utils';

@Component({
  selector: 'cxm-smartflow-preview-document',
  templateUrl: './preview-document.component.html',
  styleUrls: ['./preview-document.component.scss']
})
export class PreviewDocumentComponent extends PreviewEnvAdapter implements OnDestroy {
  base64$ = new BehaviorSubject('');
  zoomLevels = [
    'auto',
    'page-actual',
    'page-fit',
    'page-width',
    0.5,
    0.75,
    1,
    1.25,
    1.5,
    2,
    2.5,
    3
  ];

  loading$ = new BehaviorSubject(false);
  fileName: string;

  constructor(
    private titleService: Title,
    private route: ActivatedRoute,
    private readonly service: ApiService,
    private snackBarService: SnackBarService,
    private translate: TranslateService,
    private configuration: ConfigurationService
  ) {
    super();

    this.route.queryParams.subscribe((params) => {
      const { docName, fileId, apiType } = params;
      // Set tab title.
      this.titleService.setTitle(docName);
      this.fileName = docName;
      const settings = configuration.getAppSettings();

      PreviewDocumentComponent.environment(
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
                    this.base64$.next(httpEvent.body);
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
    this.base64$.unsubscribe();
    this.loading$.unsubscribe();
  }
}
