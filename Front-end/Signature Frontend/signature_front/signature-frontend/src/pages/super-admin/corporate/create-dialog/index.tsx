import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {CorporateAdmin} from '@/redux/slides/super-admin/corporateAdminSlide';
import CloseIcon from '@mui/icons-material/Close';
import {IconButton, Stack} from '@mui/material';
import {t} from 'i18next';
import {FormProvider, useForm} from 'react-hook-form';
import CreateCorporateUserForm from '@pages/super-admin/corporate/create-dialog/form';

export type ICorporateRegister = Omit<CorporateAdmin, 'companyId'>;

const defaultValues: ICorporateRegister = {
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  password: '',
  functional: '',
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
  onAddSuccess?: (data: CorporateAdmin) => void;
}) {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const methods = useForm({
    defaultValues,
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
            onAddSuccess={(data: CorporateAdmin) => {
              handleClose();

              if (typeof onAddSuccess === 'function') {
                onAddSuccess(data);
              }
            }}
          />
        }
        actionDialog={
          <NGButton
            type={'submit'}
            form="super_admin_add_corporate_admin_dialog_form"
            btnProps={{
              disableFocusRipple: true,
              disableRipple: true,
              disableTouchRipple: true,
            }}
            disabled={Object.keys(methods.formState.errors).length > 0}
            color={['#ffffff', '#ffffff']}
            variant="contained"
            myStyle={{
              bgcolor: activeColor ?? 'Primary.main',
              p: '8px, 16px',
              borderRadius: '6px',
              mr: 1,
              my: 0.5,
              borderColor: '#000000',
            }}
            fontWeight="600"
            title={t(Localization('super-admin-add-corporate-user', 'submit'))}
          />
        }
      />
    </FormProvider>
  );
}
