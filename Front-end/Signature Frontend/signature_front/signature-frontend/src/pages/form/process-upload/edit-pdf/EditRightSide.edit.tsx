import {PersonSVG} from '@/assets/svg/person/person';
import {defaultColor} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  storeApprovals,
  storeRecipient,
  storeSignatories,
} from '@/redux/slides/authentication/authenticationSlide';
import NGText from '@components/ng-text/NGText';
import AddIcon from '@mui/icons-material/Add';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import {
  Backdrop,
  CircularProgress,
  Divider,
  Stack,
  Typography,
} from '@mui/material';
import {
  blue,
  deepOrange,
  deepPurple,
  green,
  grey,
  red,
} from '@mui/material/colors';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {IRecipient} from '../type';
import {
  AddMore,
  AntSwitch,
  Approvals,
  Receptient,
  Signatories,
  SubTitleAddMore,
  TitleAddMore,
  UniqueSignatory,
} from './other/common';

const bg = [deepOrange[50], deepPurple[50], green[50], blue[50], red[50]];
const textColor = [
  deepOrange[800],
  deepPurple[800],
  green[800],
  blue[800],
  red[800],
];

const EditRightSide = ({
  setActiveStep,
}: {
  setActiveStep: React.Dispatch<React.SetStateAction<number>>;
}) => {
  const {t} = useTranslation();
  // Data from redux
  const {
    signatories: secondSignatories,
    approvals: secondApprovals,
    recipients: secondRecipients,
  } = useAppSelector(state => state.authentication);
  const dispatch = useAppDispatch();

  // Dialog popup toggle
  const [toggleDialog, setToggleDialog] = React.useState<{
    approval: boolean;
    signatory: boolean;
    recipient: boolean;
  }>({
    approval: false,
    signatory: false,
    recipient: false,
  });
  const [deleteProgress, setDeleteProgress] = React.useState(false);

  // Display participants
  const [display, setDisplay] = React.useState<{
    approval: boolean;
    signatory: boolean;
    recipient: boolean;
  }>({
    approval: false,
    signatory: true,
    recipient: false,
  });

  // Approvals static data
  const [approvals, setApproval] =
    React.useState<IRecipient[]>(secondApprovals);
  // Signatories static data
  const [signatories, setSignatories] =
    React.useState<IRecipient[]>(secondSignatories);
  const [recipients, setRecipients] =
    React.useState<IRecipient[]>(secondRecipients);

  // Handle close dialog
  const handleCloseApproval = () => {
    setToggleDialog({...toggleDialog, approval: false});
  };
  const handleCloseSignatory = () => {
    setToggleDialog({...toggleDialog, signatory: false});
  };
  const handleCloseRecipient = () => {
    setToggleDialog({...toggleDialog, recipient: false});
  };

  const handleOpenApproval = () => {
    setToggleDialog({...toggleDialog, approval: true});
  };
  const handleOpenSignatory = () => {
    setToggleDialog({...toggleDialog, signatory: true});
  };
  const handleOpenRecipient = () => {
    setToggleDialog({...toggleDialog, recipient: true});
  };

  // Filter approvals
  const filterApproval = React.useCallback(
    (e: React.MouseEvent<HTMLButtonElement, MouseEvent>, index: number) => {
      setDeleteProgress(true);
      setTimeout(() => {
        const temp = [...approvals];
        const data = temp.filter((t, i: number) => i !== index && t);
        setApproval(data);
        setDeleteProgress(false);

        dispatch(storeApprovals({data}));
      }, 3000);

      handleCloseApproval();
    },

    [approvals],
  );
  // Filter signatories
  const filterSignatory = React.useCallback(
    (e: React.MouseEvent<HTMLButtonElement, MouseEvent>, index: number) => {
      setDeleteProgress(true);
      setTimeout(() => {
        const temp = [...signatories];
        const data = temp.filter((t, i: number) => i !== index && t);
        setSignatories(data);
        setDeleteProgress(false);
        dispatch(storeSignatories({data}));
      }, 3000);

      handleCloseSignatory();
    },
    [signatories],
  );
  // Filter recipients
  const filterRecipient = React.useCallback(
    (e: React.MouseEvent<HTMLButtonElement, MouseEvent>, index: number) => {
      setDeleteProgress(true);
      setTimeout(() => {
        const temp = [...recipients];
        const data = temp.filter((t, i: number) => i !== index && t);
        setRecipients(data);
        setDeleteProgress(false);

        dispatch(storeRecipient({data}));
      }, 3000);

      handleCloseRecipient();
    },

    [recipients],
  );

  const genColor = React.useMemo(() => {
    const genArr: number[] = [];
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    for (const _element of bg) {
      const random = Math.floor(Math.random() * bg.length);
      genArr.push(random);
    }

    return genArr;
  }, []);

  const handleAddMore = () => {
    setActiveStep(v => v - 1);
  };

  return (
    <Stack sx={{width: '100%', border: `1px solid ${grey[400]}`}}>
      <TitleAddMore
        name={t(Localization('table', 'recipient'))}
        icon={<PersonSVG sx={{color: '#D6056A'}} />}
        onClick={handleAddMore}
      />
      <Divider sx={{borderBottomWidth: 2}} />
      {/* Approbateurs */}
      <AddMore
        name={t(Localization('upload-signatories', 'approvals'))}
        icon={
          display.approval ? <KeyboardArrowDownIcon /> : <KeyboardArrowUpIcon />
        }
        onClick={() => setDisplay({...display, approval: !display.approval})}
        num={secondApprovals.length}
      />
      <Divider sx={{borderBottomWidth: 2}} />
      <Stack>
        <Stack sx={{px: '2rem', display: display.approval ? 'flex' : 'none'}}>
          {approvals.length ? (
            <Stack
              direction={'row'}
              alignItems={'center'}
              spacing={2}
              sx={{py: 2}}>
              <AntSwitch
                defaultChecked
                overridecolor="green"
                inputProps={{'aria-label': 'ant design'}}
              />

              <NGText
                text={t(Localization('pdf-edit', 'define-an-approval-order'))}
              />
            </Stack>
          ) : undefined}
          {approvals.map(({lastName: name, firstName}, index: number) => (
            <Approvals
              avatarSx={{
                bgcolor: bg[genColor[index]],
                color: textColor[genColor[index]],
              }}
              onEdit={handleAddMore}
              key={`approval_${name}_${index}`}
              name={`${firstName} ${name}`}
              index={index}
              popUp={handleOpenApproval}
              open={toggleDialog.approval}
              handleClose={handleCloseApproval}
              onDelete={filterApproval}
            />
          ))}
          {approvals.length ? (
            <>
              <br />
              <Divider sx={{borderBottomWidth: 2}} />
            </>
          ) : undefined}
        </Stack>
        <SubTitleAddMore
          name={t(Localization('pdf-edit', 'add-approvals'))}
          icon={<AddIcon sx={{fontSize: '16px', color: defaultColor}} />}
          onClick={handleAddMore}
        />
        <Divider sx={{borderBottomWidth: 2}} />
      </Stack>

      {/* Signataires */}
      <AddMore
        name={t(Localization('upload-signatories', 'signatories'))}
        icon={
          display.signatory ? (
            <KeyboardArrowDownIcon />
          ) : (
            <KeyboardArrowUpIcon />
          )
        }
        onClick={() => setDisplay({...display, signatory: !display.signatory})}
        num={secondSignatories.length}
      />
      <Divider sx={{borderBottomWidth: 2}} />
      <Stack>
        <Stack sx={{px: '2rem', display: display.signatory ? 'flex' : 'none'}}>
          <Stack
            direction={'row'}
            alignItems={'center'}
            spacing={2}
            sx={{py: 2}}>
            <AntSwitch
              defaultChecked
              inputProps={{'aria-label': 'ant design'}}
              overridecolor="green"
            />
            <Typography component={'span'}>
              {t(Localization('pdf-edit', 'define-a-signature-order'))}
            </Typography>
          </Stack>
          {signatories.length > 1
            ? signatories.map(({lastName: name, firstName}, index: number) => (
                <Signatories
                  avatarSx={{
                    bgcolor: bg[genColor[index]],
                    color: textColor[genColor[index]],
                  }}
                  onEdit={handleAddMore}
                  key={`signatory_${name}_ ${index}`}
                  name={`${firstName} ${name}`}
                  index={index}
                  popUp={handleOpenSignatory}
                  open={toggleDialog.signatory}
                  handleClose={handleCloseSignatory}
                  onDelete={filterSignatory}
                />
              ))
            : signatories.map(({lastName: name, firstName}, index: number) => (
                <UniqueSignatory
                  avatarSx={{
                    bgcolor: bg[genColor[index]],
                    color: textColor[genColor[index]],
                  }}
                  key={`signatory_${name}_${index}`}
                  name={`${firstName} ${name}`}
                  index={index}
                  open={toggleDialog.signatory}
                  handleClose={handleCloseSignatory}
                  onDelete={filterSignatory}
                />
              ))}
          {signatories.length ? (
            <>
              <br />
              <Divider sx={{borderBottomWidth: 2}} />
            </>
          ) : undefined}
        </Stack>
        <SubTitleAddMore
          name={t(Localization('pdf-edit', 'add-signatories'))}
          icon={
            <AddIcon
              sx={{fontSize: '16px', color: defaultColor, strokeWidth: 4}}
            />
          }
          onClick={handleAddMore}
        />
        <Divider sx={{borderBottomWidth: 2}} />
      </Stack>

      {/* Participants */}
      <AddMore
        name={t(Localization('upload-signatories', 'receipts'))}
        icon={
          display.recipient ? (
            <KeyboardArrowDownIcon />
          ) : (
            <KeyboardArrowUpIcon />
          )
        }
        onClick={() => setDisplay({...display, recipient: !display.recipient})}
        num={secondRecipients.length}
      />
      <Divider sx={{borderBottomWidth: 2}} />
      <Stack>
        <Stack sx={{px: '2rem'}}>
          {recipients.length ? (
            <Stack
              direction={'row'}
              alignItems={'center'}
              spacing={2}
              sx={{py: 2}}>
              <AntSwitch
                defaultChecked
                inputProps={{'aria-label': 'ant design'}}
                overridecolor="green"
              />

              <NGText
                text={t(
                  Localization('pdf-edit', 'define-a-destinataires-order'),
                )}
              />
            </Stack>
          ) : undefined}

          {recipients.map(({lastName: name, firstName}, index: number) => (
            <Receptient
              avatarSx={{
                bgcolor: bg[genColor[index]],
                color: textColor[genColor[index]],
              }}
              onEdit={handleAddMore}
              key={`receptient_${name}_${index}`}
              name={`${firstName} ${name}`}
              index={index}
              popUp={handleOpenRecipient}
              open={toggleDialog.recipient}
              handleClose={handleCloseRecipient}
              onDelete={filterRecipient}
            />
          ))}
          {recipients.length ? (
            <>
              <br />
              <Divider sx={{borderBottomWidth: 2}} />
            </>
          ) : undefined}
        </Stack>
        <SubTitleAddMore
          name={t(Localization('pdf-edit', 'add-receipt'))}
          icon={
            <AddIcon
              sx={{fontSize: '16px', color: defaultColor, strokeWidth: 4}}
            />
          }
          onClick={handleAddMore}
        />
        <Divider sx={{borderBottomWidth: 2}} />
      </Stack>

      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={deleteProgress}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

export default EditRightSide;
