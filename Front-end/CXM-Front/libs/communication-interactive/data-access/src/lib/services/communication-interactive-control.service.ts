import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { appLocalStorageConstant, appRoute } from '@cxm-smartflow/shared/data-access/model';
import { Router } from '@angular/router';
import { AnimatedConfirmationPageOptions } from '@cxm-smartflow/shared/common-typo';

@Injectable({
  providedIn: 'any'
})
export class CommunicationInteractiveControlService {
  constructor(private readonly translate: TranslateService,
              private readonly router: Router) {
    this.translate.use(localStorage.getItem(appLocalStorageConstant.Common.Locale.Locale) || appLocalStorageConstant.Common.Locale.Fr);
  }

  public navigateToEditor(id: number): void {
    this.router.navigate([appRoute.cxmCommunicationInteractive.navigateToEditor], { queryParams: { id: btoa(id.toString()) } });
  }

  public navigateToChoosingModel(): void {
    this.router.navigate([appRoute.cxmCommunicationInteractive.navigateToChooseModel]);
  }

  public navigateToSuccessPage(): void {
    this.router.navigate([appRoute.cxmCommunicationInteractive.navigateToSuccessPage]);
  }

  public async getSuccessMessage(): Promise<AnimatedConfirmationPageOptions> {
    const message: any = await this.translate.get('communicationInteractive.successPage').toPromise();
    return <AnimatedConfirmationPageOptions>message;
  }

  public navigateToFlow(): void {
    this.router.navigateByUrl('/cxm-flow-traceability/list');
  }
}
