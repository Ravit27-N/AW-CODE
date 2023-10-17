import {NGPlusIcon} from '@/assets/Icon';
import {NGButton} from '@/components/ng-button/NGButton';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGText from '@/components/ng-text/NGText';
import {colorDisable} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {Stack, TextField} from '@mui/material';
import {t} from 'i18next';
import {useTranslation} from 'react-i18next';
import {IServiceState} from '../Services';
import React from 'react';

type IAddATeam = {
  open: boolean;
  state: IServiceState;
  setState: React.Dispatch<React.SetStateAction<IServiceState>>;
};

const AddParticipant = ({open, state, setState}: IAddATeam) => {
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
      titleDialog={<AddParticipantTitle />}
      contentDialog={<AddParticipantContent />}
      actionDialog={<AddParticipantAction state={state} setState={setState} />}
    />
  );
};

export default AddParticipant;

const AddParticipantTitle = (): JSX.Element => {
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
        text={t(Localization('enterprise-services', 'add-a-service'))}
        fontSize={'18px'}
        fontWeight="600"
      />
    </Stack>
  );
};

const AddParticipantContent = (): JSX.Element => {
  const {t} = useTranslation();
  return (
    <Stack
      sx={{border: '1px solid #E9E9E9', p: '24px'}}
      gap={'10px'}
      alignItems={'center'}
      width={'100%'}
      height={'114px'}
      justifyContent={'center'}
      bgcolor={'#FAFAFA'}>
      <Stack width="581px" direction="row" bgcolor={'red'}>
        <TextField
          placeholder={t(Localization('enterprise-services', 'search'))!}
          sx={{
            fontSize: '14px',
          }}
          size="small"
        />
        <NGButton
          title={t(Localization('enterprise-services', 'create-user'))}
        />
        {/* <NGInputField<{name: string}>
          size="small"
          sx={{
            fontSize: 15,
            minWidth: '15rem',
            // '&.MuiOutlinedInput-root': {
            //   '&.Mui-focused fieldset': {
            //     border: `1px solid ${'black' ?? 'Primary.main'}`,
            //   },
            // },
          }}
          control={control}
          eMessage={t(Localization('form-error', 'name'))!}
          inputProps={{color: 'red'}}
          errorInput={errors ? errors.name : undefined}
          typeInput={'last-name'}
          type={'text'}
          name={`name`}
          placeholder={t(Localization('form', 'name'))!}
        /> */}
      </Stack>
    </Stack>
  );
};

const AddParticipantAction = ({
  setState,
  state,
}: {
  state: IServiceState;
  setState: React.Dispatch<React.SetStateAction<IServiceState>>;
}): JSX.Element => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  return (
    <Stack
      gap={'10px'}
      width={'100%'}
      height={'64px'}
      justifyContent={'center'}>
      <Stack direction={'row'} justifyContent={'flex-end'} gap={'5px'}>
        <NGButton
          onClick={() => setState({...state, toggle: false})}
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
          btnProps={{
            disableFocusRipple: true,
            disableRipple: true,
            disableTouchRipple: true,
          }}
          disabled={false}
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
