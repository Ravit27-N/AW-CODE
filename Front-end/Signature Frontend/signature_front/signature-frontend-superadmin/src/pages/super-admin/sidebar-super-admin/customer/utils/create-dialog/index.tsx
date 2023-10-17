import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import CloseIcon from '@mui/icons-material/Close';
import {IconButton, Stack} from '@mui/material';
import {t} from 'i18next';
import {FormProvider, useForm} from 'react-hook-form';
import CreateCorporateUserForm from '@pages/super-admin/sidebar-super-admin/customer/utils/create-dialog/form';
import {NGPlusIcon} from '@/assets/Icon';
import {colorDisable} from '@/constant/style/StyleConstant';
import {IDType} from '@/utils/request/interface/type';
import {pixelToRem} from '@/utils/common/pxToRem';

export type CorporateAdminForm = {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
  functional: string;
  businessId: IDType;
  userAccessId: IDType;
};

export const defaultCorporateAdminForm: CorporateAdminForm = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  functional: '',
  businessId: '',
  userAccessId: '',
};

export default function CreateCorporateUserDialog({
  companyId,
  open,
  onClose,
  onAddSuccess,
}: {
  companyId: number;
  open: boolean;
  onClose: () => void;
  onAddSuccess?: (data: CorporateAdminForm) => void;
}) {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const methods = useForm({
    defaultValues: defaultCorporateAdminForm,
  });

  const handleClose = () => {
    methods.reset();
    onClose();
  };

  return (
    <FormProvider {...methods}>
      <NGDialog
        open={open}
        sx={{
          '& .MuiPaper-root': {
            boxSizing: 'border-box',
            borderRadius: '16px',
          },
        }}
        sxProp={{
          titleSx: {
            borderRadius: '28px',
            p: '20px',
          },
          contentsSx: {
            p: 0,
          },
        }}
        titleDialog={
          <Stack
            direction={'row'}
            alignItems={'center'}
            justifyContent={'space-between'}>
            <NGText
              iconStart={
                <NGPlusIcon
                  sx={{
                    color: 'primary.main',
                    fontSize: pixelToRem(16),
                    mr: 1,
                  }}
                />
              }
              text={t(
                Localization(
                  'super-admin-add-corporate-user',
                  'create-corporate-admin',
                ),
              )}
              fontSize={'18px'}
              fontWeight="600"
            />
            <IconButton
              onClick={handleClose}
              disableTouchRipple
              disableFocusRipple
              disableRipple
              aria-label="delete">
              <CloseIcon color={'primary'} />
            </IconButton>
          </Stack>
        }
        contentDialog={
          <CreateCorporateUserForm
            companyId={companyId}
            onAddSuccess={(data: CorporateAdminForm) => {
              handleClose();

              if (typeof onAddSuccess === 'function') {
                onAddSuccess(data);
              }
            }}
          />
        }
        actionDialog={
          <Stack
            direction={'row'}
            justifyContent={'flex-end'}
            gap={1}
            marginY={1}>
            <NGButton
              onClick={handleClose}
              btnProps={{
                disableFocusRipple: true,
                disableRipple: true,
                disableTouchRipple: true,
              }}
              locationIcon="start"
              color={['#ffffff', '#000000']}
              variant="outlined"
              fontSize="11px"
              myStyle={{
                borderRadius: '6px',
                border: '1px solid #000000',
                '&:hover': {
                  borderColor: activeColor ?? 'info.main',
                },
              }}
              fontWeight="600"
              title={t(
                Localization('super-admin-add-corporate-user', 'cancel'),
              )}
            />
            <NGButton
              type={'submit'}
              form="super_admin_add_corporate_admin_dialog_form"
              btnProps={{
                disableFocusRipple: true,
                disableRipple: true,
                disableTouchRipple: true,
              }}
              icon={
                <NGPlusIcon
                  sx={{
                    width: '18px',
                    height: '20px',
                    color: 'White.main',
                    ml: 0.5,
                  }}
                />
              }
              disabled={Object.keys(methods.formState.errors).length > 0}
              locationIcon="end"
              color={['#ffffff', '#ffffff']}
              variant="contained"
              fontSize="11px"
              myStyle={{
                '&.Mui-disabled': {
                  bgcolor: colorDisable,
                },
                '&.MuiButtonBase-root': {
                  borderColor: activeColor ?? 'Primary.main',
                },
                bgcolor: activeColor ?? 'Primary.main',
                borderRadius: '6px',
                borderColor: '#000000',
              }}
              fontWeight="600"
              title={t(
                Localization('super-admin-add-corporate-user', 'submit'),
              )}
            />
          </Stack>
        }
      />
    </FormProvider>
  );
}
