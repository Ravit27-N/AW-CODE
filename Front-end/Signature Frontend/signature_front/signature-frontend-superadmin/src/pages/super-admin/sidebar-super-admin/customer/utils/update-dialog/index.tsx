import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import CloseIcon from '@mui/icons-material/Close';
import {IconButton, Stack} from '@mui/material';
import {t} from 'i18next';
import {FormProvider, useForm} from 'react-hook-form';
import UpdateCorporateUserForm from '@pages/super-admin/sidebar-super-admin/customer/utils/update-dialog/form';
import {IGetUsersContent} from '@/redux/slides/corporate-admin/corporateUserSlide';
import {useState} from 'react';
import {NGPlusIcon} from '@/assets/Icon';
import {colorDisable} from '@/constant/style/StyleConstant';
import {defaultCorporateAdminForm} from '@pages/super-admin/sidebar-super-admin/customer/utils/create-dialog';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGPencil} from '@/assets/iconExport/ExportIcon';

export default function UpdateCorporateUserDialog({
  open,
  onClose,
  onUpdateSuccess,
  data,
}: {
  open: boolean;
  data: IGetUsersContent;
  onClose: () => void;
  onUpdateSuccess?: (data: IGetUsersContent) => void;
}) {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const [disableSubmit, setDisableSubmit] = useState(true);
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
                <NGPencil
                  sx={{
                    color: 'primary.main',
                    fontSize: pixelToRem(16),
                    mr: 1,
                  }}
                />
              }
              text={t(
                Localization(
                  'super-admin-update-corporate-user',
                  'update-corporate-admin',
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
          <UpdateCorporateUserForm
            data={data}
            setDisableSubmit={setDisableSubmit}
            onUpdateSuccess={(data: IGetUsersContent) => {
              handleClose();

              if (typeof onUpdateSuccess === 'function') {
                onUpdateSuccess(data);
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
                Localization('super-admin-update-corporate-user', 'cancel'),
              )}
            />
            <NGButton
              type={'submit'}
              form="super_admin_update_corporate_admin_dialog_form"
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
              disabled={
                disableSubmit &&
                (Object.keys(methods.formState.errors).length > 0 ||
                  Object.keys(methods.formState.dirtyFields).length <= 0)
              }
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
                Localization('super-admin-update-corporate-user', 'submit'),
              )}
            />
          </Stack>
        }
      />
    </FormProvider>
  );
}
