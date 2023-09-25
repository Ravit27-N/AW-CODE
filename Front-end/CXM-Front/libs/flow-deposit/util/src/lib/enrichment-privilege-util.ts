import { AttachmentDetail } from '@cxm-smartflow/flow-deposit/data-access';
import { EnrichmentMailing } from '@cxm-smartflow/shared/data-access/model';

export interface PrivillegeKeyUtil {
  modifyPrivKey: string;
  deletePrivKey: string;
}

export class EnrichmentPrivilegeUtil {

  public static getPrivillegKey(detail: AttachmentDetail): PrivillegeKeyUtil {
    const modifyPrivKey = detail.default ? EnrichmentMailing.MODIFY_DEFAULT_RESOURCE : EnrichmentMailing.MODIFY_CUSTOM_RESOURCE;
    const deletePrivKey = detail.default ? EnrichmentMailing.DELETE_DEFAULT_RESOURCE : EnrichmentMailing.DELETE_CUSTOM_RESOURCE;

    return {
      modifyPrivKey,
      deletePrivKey
    };
  }
}
