import { UserUtil } from "@cxm-smartflow/shared/data-access/services"
import { EnrichmentMailing } from "@cxm-smartflow/template/data-access"

export const getbackgroundPerm = () => {
  return {
    add: UserUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.ADD_RESOURCE),
    delete: UserUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.DELETE_CUSTOM_RESOURCE),
    modify: UserUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.MODIFY_CUSTOM_RESOURCE),
    library: UserUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.USE_RESOURCE_IN_LIBRARY),
    single: UserUtil.canAccess(EnrichmentMailing.CXM_ENRICHMENT_MAILING, EnrichmentMailing.UPLOAD_A_SINGLE_RESOURCE)
  }
}

export const backgroundTypeCtrl = (types: any[], perm: any) => {
  const t = [];
  if(perm.library) t.push(types[0]);
  if(perm.single) t.push(types[1])

  return t;
}
