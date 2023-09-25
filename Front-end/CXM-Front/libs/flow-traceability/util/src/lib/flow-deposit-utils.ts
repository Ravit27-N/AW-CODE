import { appRoute } from '@cxm-smartflow/shared/data-access/model';

export function getDepositPath(step: number): string {
  const depositRoute = appRoute.cxmDeposit;
  switch (step) {
    case 1:
      return depositRoute.navigateToAcquisition;
    case 2:
      return depositRoute.navigateToPreAnalysis;
    case 3:
      return depositRoute.navigateToAnalysisResult;
    case 4:
      return depositRoute.navigateToProductionCriteria;
    case 5:
      return depositRoute.navigateToFinished;
  }
  return '';
}
