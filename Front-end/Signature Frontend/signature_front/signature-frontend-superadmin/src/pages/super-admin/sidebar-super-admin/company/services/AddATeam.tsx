import {NGPlusIcon} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {colorDisable} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {
  ContentsInterface,
  useAddServiceOrDepartmentsMutation,
} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import NGInput from '@components/ng-inputField/NGInput';
import {Stack} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import {IServiceState} from '../Services';
import {useSnackbar} from 'notistack';
import {UNKOWNERROR} from '@/constant/NGContant';
import {HandleException} from '@/utils/common/HandleException';

type IAddATeam = {
  open: boolean;
  state: IServiceState;
  setState: React.Dispatch<React.SetStateAction<IServiceState>>;
  onAddSuccess: (data: ContentsInterface) => void;
};

const AddATeam = ({open, state, setState, onAddSuccess}: IAddATeam) => {
  const [nameCompany, setNameCompany] = React.useState<string>('');
  return (
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
      titleDialog={<AddATeamTitle />}
      contentDialog={
        <AddATeamContent
          setNameCompany={setNameCompany}
          nameCompany={nameCompany}
        />
      }
      actionDialog={
        <AddATeamAction
          state={state}
          setState={setState}
          nameCompany={nameCompany}
          setNameCompany={setNameCompany}
          onAddSuccess={onAddSuccess}
        />
      }
    />
  );
};

export default AddATeam;

const AddATeamTitle = (): JSX.Element => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  return (
    <Stack gap={'12px'} alignItems={'center'} direction={'row'}>
      <NGPlusIcon
        sx={{
          width: '50px',
          height: '20px',
          mt: '-1px',
          color: activeColor,
        }}
      />
      <NGText
        myStyle={{width: '517px', height: '28px'}}
        text={t(Localization('enterprise-services', 'department'))}
        fontSize={'18px'}
        fontWeight="600"
      />
    </Stack>
  );
};

const AddATeamContent = ({
  setNameCompany,
  nameCompany,
}: {
  setNameCompany: React.Dispatch<React.SetStateAction<string>>;
  nameCompany: string;
}): JSX.Element => {
  return (
    <Stack
      sx={{p: '20px', border: '1px solid #E9E9E9'}}
      gap={'10px'}
      alignItems={'center'}
      width={'100%'}
      height={'114px'}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <Stack width="100%">
        <Stack direction={'row'}>
          <NGText
            text={t(Localization('form', 'name'))}
            myStyle={{fontSize: 13, fontWeight: 500}}
          />

          <NGText text={'*'} myStyle={{fontSize: 16, color: 'red'}} />
        </Stack>
        <NGInput
          setValue={setNameCompany}
          value={nameCompany}
          nameId={'optional'}
          placeholder={t(Localization('enterprise-services', 'name-service'))}
        />
      </Stack>
    </Stack>
  );
};
const AddATeamAction = ({
  setState,
  state,
  nameCompany,
  setNameCompany,
  onAddSuccess,
}: {
  state: IServiceState;
  setState: React.Dispatch<React.SetStateAction<IServiceState>>;
  nameCompany: string;
  setNameCompany: React.Dispatch<React.SetStateAction<string>>;
  onAddSuccess: (data: ContentsInterface) => void;
}): JSX.Element => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const [addTemplate] = useAddServiceOrDepartmentsMutation({});
  const {companyProviderTheme} = useAppSelector(state => state.enterprise);
  const {enqueueSnackbar} = useSnackbar();
  const handleClose = () => {
    setState({...state, toggle: false});
    setNameCompany('');
  };
  const addTemplateOrService = async () => {
    try {
      const resData = await addTemplate({
        unitName: nameCompany,
        companyId: companyProviderTheme.companyId!,
        sortOrder: null,
      }).unwrap();

      onAddSuccess(resData);
      handleClose();

      enqueueSnackbar(
        t(Localization('enterprise-services', 'create-successfully'), {
          name: resData.unitName,
        }),
        {
          variant: 'successSnackbar',
        },
      );
    } catch (error) {
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };
  return (
    <Stack
      gap={'10px'}
      width={'100%'}
      height={'64px'}
      justifyContent={'center'}>
      <Stack direction={'row'} justifyContent={'flex-end'} gap={'5px'}>
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
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            width: '77px',
            height: '36px',
            borderRadius: '6px',
            border: '1px solid #000000',
            '&.MuiButtonBase-root': {
              ':hover': {
                borderColor: activeColor ?? 'info.main',
              },
            },
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('upload-document', 'cancel'))}
        />
        <NGButton
          onClick={addTemplateOrService}
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          disabled={nameCompany.length <= 0}
          locationIcon="start"
          color={['#ffffff', '#ffffff']}
          variant="outlined"
          fontSize="11px"
          myStyle={{
            '&.Mui-disabled': {
              bgcolor: colorDisable,
            },
            '&.MuiButtonBase-root': {
              '&:hover': {
                borderStyle: 'none',
                bgcolor: colorDisable,
              },
              borderColor: activeColor ?? 'Primary.main',
            },
            bgcolor: activeColor ?? 'Primary.main',
            p: '8px, 16px',
            minHeight: 0,
            minWidth: 0,
            width: '77px',
            height: '36px',
            borderRadius: '6px',
            borderColor: '#000000',
          }}
          textSx={{width: '45px'}}
          fontWeight="600"
          title={t(Localization('enterprise-services', 'add'))}
        />
      </Stack>
    </Stack>
  );
};
