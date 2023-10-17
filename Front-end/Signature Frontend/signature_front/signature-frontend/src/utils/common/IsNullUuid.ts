import {splitUserCompany} from '@/utils/common/String';
import {store} from '@/redux';
import {useEffect} from 'react';
import {enqueueSnackbar} from 'notistack';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';
export const IsNullUuid = () => {
  const {t} = useTranslation();
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  useEffect(() => {
    if (company.companyUuid === '' || company.companyUuid === 'companyUuid') {
      enqueueSnackbar(t(Localization('company-form-error', 'uuid')), {
        variant: 'errorSnackbar',
      });
    }
  }, [company.companyUuid]);
};
