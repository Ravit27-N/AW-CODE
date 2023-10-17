import {InvitationStatus} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {getSignatureProgress} from '@/utils/common/SignatureProjectCommon';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  NGCorrect,
  NGDanger,
  NGExpiredTime,
  NGFalse,
} from '@assets/iconExport/Allicon';
import {BoxStatus} from '@components/ng-switch-case-status/BoxStatus';
import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';
import NGText from '@components/ng-text/NGText';
import {useTranslation} from 'react-i18next';

import {Signatory} from '@/utils/request/interface/Project.interface';

import {StatusProject} from '@components/ng-table/TableDashboard/resource/TCell';
interface NGProjectStatusInterface {
  StatusName: keyof ProjectStatusInterfaces;
  signatories?: Signatory[];
}
export function NGProjectStatus({
  StatusName,
  signatories,
}: NGProjectStatusInterface) {
  const {t} = useTranslation();

  switch (StatusName) {
    /*
        Status completed
     */
    case 'COMPLETED': {
      return (
        <BoxStatus
          title={t(Localization('projectStatus', StatusName))}
          icon={
            <NGCorrect
              sx={{
                fontSize: pixelToRem(9),
                color: 'white',
              }}
            />
          }
        />
      );
    }

    /*
        Status draft, expired
     */
    case 'DRAFT': {
      return (
        <BoxStatus
          title={t(Localization('projectStatus', StatusName))}
          bgColor={'White.main'}
          textColor={'Black.main'}
          borderColor={'1px solid Black'}
        />
      );
    }
    /*
          Status draft, expired
       */

    case 'EXPIRED': {
      return (
        <BoxStatus
          title={t(Localization('projectStatus', StatusName))}
          icon={
            <NGExpiredTime
              sx={{
                fontSize: pixelToRem(12),
                color: 'white',
              }}
            />
          }
          bgColor={'red'}
          textColor={'White.main'}
          borderColor={'1px solid red'}
        />
      );
    }
    /*
             Status refused
          */

    case 'REFUSED': {
      return (
        <BoxStatus
          title={t(Localization('documentStatus', StatusName))}
          bgColor={'red'}
          icon={
            <NGFalse
              sx={{
                fontSize: pixelToRem(9),
                color: 'white',
              }}
            />
          }
        />
      );
    }
    /*
            Status in progress
        */
    case 'IN_PROGRESS': {
      return (
        <StatusProject
          status={
            StatusName === InvitationStatus.IN_PROGRESS
              ? getSignatureProgress(signatories ?? [])
              : StatusName
          }
          t={t}
        />
      );
    }
    /*
      Status in abandon / cancel
    */
    case 'ABANDON': {
      return (
        <BoxStatus
          title={t(Localization('cancel-project', 'cancel-abandon'))}
          bgColor={'red'}
          icon={
            <NGDanger
              sx={{
                fontSize: pixelToRem(13),
                color: 'white',
              }}
            />
          }
        />
      );
    }

    /*
         Status don't have in enum in const file
     */
    default: {
      return <NGText text={'UNKNOWN'} />;
    }
  }
}
