import {NGAddParticipant, NGPlusIcon, NGThreeDotAlign} from '@/assets/Icon';
import {NGUser} from '@/assets/iconExport/Allicon';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import NGGroupAvatar from '@/components/ng-group-avatar/NGGroupAvatar';
import NGText from '@/components/ng-text/NGText';
import {FONT_TYPE, Participant} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {IRecipient, IUploadForm} from '@/pages/form/process-upload/type';
import {store} from '@/redux';
import {useAppSelector} from '@/redux/config/hooks';
import {
  getFirstNameAndLastName,
  getNameByFirstIndex,
} from '@/utils/common/HandlerFirstName_LastName';
import {Autocomplete, Button, Stack, TextField} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import UploadParticipant from './UploadParticipant';

type IAddParticipant = {
  trigger: boolean;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const AddParticipants = (props: IAddParticipant) => {
  const {trigger, setTrigger} = props;
  const [uploadTrigger, setUploadTrigger] = React.useState(false);
  const [value, setValue] = React.useState<
    IUploadForm['recipients'][number] | null
  >(null);

  return (
    <Stack>
      <NGDialog
        maxWidth="xl"
        sx={{
          '& .MuiPaper-root': {
            borderRadius: '16px',
            width: '764px',
          },
        }}
        open={trigger}
        sxProp={{
          titleSx: {
            p: '20px',
          },
          contentsSx: {
            p: 0,
          },
          actionSx: {
            padding: '14px 24px',
          },
        }}
        titleDialog={<AddParticipantTitle />}
        contentDialog={
          <AddParticipantContent
            uploadTrigger={uploadTrigger}
            trigger={trigger}
            setUploadTrigger={setUploadTrigger}
            setTrigger={setTrigger}
            value={value}
            setValue={setValue}
          />
        }
        actionDialog={
          <AddParticipantActions
            setTrigger={setTrigger}
            setUploadTrigger={setUploadTrigger}
            value={value}
          />
        }
      />
    </Stack>
  );
};

export default AddParticipants;

export const AddParticipantTitle = () => {
  const activeColor = store.getState().enterprise.theme[0].mainColor;
  const {createProjectActiveRole} = useAppSelector(
    state => state.authentication,
  );

  const getRole = (r: Participant): string => {
    const role: {[k: Participant[number]]: string} = {
      [Participant.Approval]: 'assign-an-approver',
      [Participant.Signatory]: 'assign-a-signatory',
      [Participant.Receipt]: 'assign-a-recipient',
    };

    return role[r];
  };

  return (
    <Stack gap={'12px'} alignItems={'center'} direction={'row'}>
      <NGPlusIcon
        sx={{
          height: '20px',
          mt: '-1px',
          color: activeColor,
        }}
      />
      <NGText
        myStyle={{width: '517px', height: '28px'}}
        text={t(
          Localization(
            'create-project-by-template',
            getRole(createProjectActiveRole?.role!) as any,
          ),
        )}
        fontSize={'18px'}
        fontWeight="600"
      />
    </Stack>
  );
};

type IAddParticipantContent = {
  uploadTrigger: boolean;
  trigger: boolean;
  setUploadTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  value: IRecipient | null;
  setValue: React.Dispatch<React.SetStateAction<IRecipient | null>>;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const initial: IRecipient = {
  id: '1',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  checked: false,
  sortOrder: 1,
};

const options: IUploadForm['recipients'] = [
  // {
  //   id: '1',
  //   firstName: 'Ethan',
  //   lastName: 'Evian',
  //   email: 'example@gmail.com',
  //   phone: '+85577360826',
  //   checked: false,
  //   sortOrder: 1,
  // },
  // {
  //   id: '2',
  //   firstName: 'Mathieu',
  //   lastName: 'Dupuit',
  //   email: 'example@gmail.com',
  //   phone: '+85577360826',
  //   checked: false,
  //   sortOrder: 1,
  // },
  // {
  //   id: '3',
  //   firstName: 'Mathide',
  //   lastName: 'Flobert',
  //   email: 'example@gmail.com',
  //   phone: '+85577360826',
  //   checked: false,
  //   sortOrder: 1,
  // },
];

export const AddParticipantContent = (props: IAddParticipantContent) => {
  const {
    uploadTrigger,
    setUploadTrigger,
    value,
    setValue,
    setTrigger,
    trigger,
  } = props;
  const [fillForm, setFillForm] = React.useState(true);
  const {createProjectActiveRole, approvals, signatories, recipients, viewers} =
    useAppSelector(state => state.authentication);

  const getRoleState = (
    role: Participant,
  ): (IRecipient & {fillForm?: boolean})[] => {
    const roleParticipant: {
      [k in Participant]: (IRecipient & {fillForm?: boolean})[];
    } = {
      [Participant.Approval]: approvals,
      [Participant.Signatory]: signatories,
      [Participant.Receipt]: recipients,
      [Participant.Viewer]: viewers,
    };

    return roleParticipant[role];
  };

  React.useEffect(() => {
    if (trigger) {
      if (createProjectActiveRole) {
        const temp = getRoleState(createProjectActiveRole.role);
        const participant = temp.find(
          item => item.id === createProjectActiveRole.id,
        );
        if (participant) {
          setFillForm(participant.fillForm ?? false);
          setValue(participant ?? null);
        }
      }
    }
  }, [trigger]);

  return (
    <>
      <Stack
        justifyContent={'center'}
        p="24px 40px 40px 40px"
        width="100%"
        bgcolor="#F0F1F380"
        sx={{
          border: '1px solid #E9E9E9',
        }}
        gap="16px">
        <Stack
          direction="row"
          gap={'14px'}
          bgcolor="#fff"
          borderRadius="6px"
          border="1px solid #E9E9E9"
          p="20px 16px"
          justifyContent="space-between">
          <Autocomplete
            disabled={fillForm}
            value={value}
            onChange={(
              event: any,
              newValue: IUploadForm['recipients'][number] | null,
            ) => {
              setValue(newValue);
            }}
            id="controllable-states-demo"
            options={options}
            getOptionLabel={option => `${option.firstName} ${option.lastName}`}
            renderOption={(props, option) => (
              <li {...props}>No contact</li>
              // <li {...props}>
              //   <Stack direction="row" gap="8px" alignItems="center">
              //     <NGGroupAvatar
              //       character={[
              //         getNameByFirstIndex(
              //           `${option.firstName} ${option.lastName}`,
              //         ),
              //       ]}
              //     />
              //     <Stack>
              //       <NGText
              //         text={getFirstNameAndLastName(
              //           `${option.firstName} ${option.lastName}`,
              //         )}
              //         myStyle={{
              //           fontSize: 14,
              //         }}
              //       />
              //       <NGText
              //         text={option.email}
              //         myStyle={{
              //           fontSize: 12,
              //         }}
              //       />
              //     </Stack>
              //   </Stack>
              // </li>
            )}
            sx={{width: 300, flexGrow: 1}}
            renderInput={params => (
              <TextField
                {...params}
                size="small"
                placeholder={
                  t(
                    Localization(
                      'create-project-by-template',
                      'search-an-existing',
                    ),
                  )!
                }
              />
            )}
          />

          <Button
            variant="outlined"
            onClick={() => {
              setValue(initial);
              setFillForm(true);
            }}
            startIcon={
              <NGAddParticipant
                sx={{
                  width: '15px',
                  color: '#000000',
                }}
              />
            }
            sx={{
              textTransform: 'none',
              fontSize: '11px',
              width: '170px',
              color: '#000000',
              fontWeight: 600,
              borderColor: '#000000',
            }}>
            {t(Localization('create-project-by-template', 'new-participant'))}
          </Button>
          {/*<Button*/}
          {/*  variant="outlined"*/}
          {/*  onClick={() => {*/}
          {/*    setValue(initial);*/}
          {/*    setFillForm(true);*/}
          {/*  }}*/}
          {/*  startIcon={*/}
          {/*    <NGPlusIcon*/}
          {/*      sx={{*/}
          {/*        width: '15px',*/}
          {/*        color: '#000000',*/}
          {/*      }}*/}
          {/*    />*/}
          {/*  }*/}
          {/*  sx={{*/}
          {/*    textTransform: 'none',*/}
          {/*    fontSize: '11px',*/}
          {/*    color: '#000000',*/}
          {/*    fontWeight: 600,*/}
          {/*    borderColor: '#000000',*/}
          {/*  }}>*/}
          {/*  {t(Localization('superadmin-dashboard', 'add'))}*/}
          {/*</Button>*/}
        </Stack>

        {/* Contacts participant */}
        {value && !fillForm && (
          <Stack>
            <ContactSelection
              name={value.lastName!}
              firstName={value.firstName!}
            />
            <UploadParticipant
              value={value}
              setFillForm={setFillForm}
              fillForm={fillForm}
              setValue={setValue}
              uploadTrigger={uploadTrigger}
              setUploadTrigger={setUploadTrigger}
              setTrigger={setTrigger}
            />
          </Stack>
        )}
        {value && fillForm && (
          <UploadParticipant
            setFillForm={setFillForm}
            fillForm={fillForm}
            setValue={setValue}
            value={value}
            uploadTrigger={uploadTrigger}
            setUploadTrigger={setUploadTrigger}
            setTrigger={setTrigger}
          />
        )}
      </Stack>
    </>
  );
};

type IContactSelection = {
  firstName: string;
  name: string;
};

export const ContactSelection = (props: IContactSelection) => {
  const {name, firstName} = props;
  return (
    <Stack
      p="12px 22px 12px 12px"
      direction="row"
      sx={{
        borderRadius: '6px',
        border: '1px solid #E9E9E9',
        borderBottomLeftRadius: 0,
        borderBottomRightRadius: 0,
        borderBottom: 0,
        bgcolor: 'white',
        height: '60px',
        gap: '12px',
      }}
      justifyContent="space-between"
      alignItems="center">
      <Stack direction="row" alignItems="center" gap="12px">
        <NGGroupAvatar
          character={[getNameByFirstIndex(`${firstName} ${name}`)]}
        />
        <Stack>
          <NGText
            text={getFirstNameAndLastName(`${firstName} ${name}`)}
            myStyle={{
              fontSize: 14,
              fontWeight: 500,
            }}
          />
          <Stack direction="row" gap="4px" alignItems="center">
            <NGUser sx={{width: '18px'}} />
            <NGText
              text={t(
                Localization('create-project-by-template', 'contact-existing'),
              )}
              myStyle={{
                fontSize: 12,
              }}
            />
          </Stack>
        </Stack>
      </Stack>

      <NGThreeDotAlign
        sx={{
          fontSize: '15px',
        }}
      />
    </Stack>
  );
};

type IAddParticipantActions = {
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  setUploadTrigger: React.Dispatch<React.SetStateAction<boolean>>;
  value: IRecipient | null;
};

export const AddParticipantActions = (props: IAddParticipantActions) => {
  const {setTrigger, setUploadTrigger, value} = props;

  return (
    <Stack direction="row" justifyContent="space-between" width="100%">
      <Button
        onClick={() => setTrigger(false)}
        variant="text"
        sx={{
          textTransform: 'none',
          color: '#000000',
          fontSize: 11,
          fontWeight: 600,
          fontFamily: FONT_TYPE.POPPINS,
        }}>
        {t(Localization('upload-document', 'cancel'))}
      </Button>

      <Button
        disabled={!value}
        onClick={() => {
          setUploadTrigger(true);
        }}
        variant="contained"
        sx={{
          width: '120px',
          textTransform: 'none',
          p: '8px 16px',
          fontSize: 11,
          boxShadow: 0,
          fontFamily: FONT_TYPE.POPPINS,
        }}>
        {t(Localization('create-project-by-template', 'assign'))}
      </Button>
    </Stack>
  );
};
