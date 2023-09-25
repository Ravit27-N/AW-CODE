import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { IAppSettings } from '@cxm-smartflow/shared/app-config';
import { ConfigurationService } from '@cxm-smartflow/shared/data-access/api';
import { TemplateModel } from '@cxm-smartflow/shared/data-access/model';
import { templateEnv as cxmTemplateEnvironment } from '@env-cxm-template';
import { take } from 'rxjs/operators';

@Component({
  selector: 'cxm-smartflow-preview-email-template',
  templateUrl: './preview-email-template.component.html',
  styleUrls: ['./preview-email-template.component.scss']
})
export class PreviewEmailTemplateComponent implements OnInit, OnDestroy{

  template: TemplateModel;
  sourceHTML: string;
  settings: IAppSettings;

  close(): void {
    this.dialogRef.close(true);
  }

  getImageURL(filename?: string) {
    return `${this.settings.apiGateway}${cxmTemplateEnvironment.templateContext}/templates/composition/load-file/${filename}`;
  }

  setup(): void {
    this.template = this.data?.emailTemplate;
    this.sourceHTML = this.data?.HTMLSource;
  }

  showTooltip(id: string, content: string): string {
    const el = document.querySelector(id);
    return el ? (el.scrollWidth > el.clientWidth ? content : '') : '';
  }

  constructor(public dialogRef: MatDialogRef<PreviewEmailTemplateComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              configuration: ConfigurationService,
              private _router: Router) {
                Object.assign(this, { settings: configuration.getAppSettings() })
              }

  ngOnInit(): void {
    this._router.events.pipe(take(1)).subscribe(() => {
      this.dialogRef.close();
    });
    this.setup();
  }

  ngOnDestroy(): void {
     this.sourceHTML = '';
  }

}
