import {Localization} from '@/i18n/lan';
import {HStack} from '@/theme';
import {getSignatureProgress} from '@/utils/common/SignatureProjectCommon';
import {pixelToRem} from '@/utils/common/pxToRem';
import {$ok} from '@/utils/request/common/type';
import {
  downloadCurrentDocumentProject,
  getFilePdfProjectDetail,
} from '@/utils/request/services/MyService';
import {NGDownloadIcon} from '@assets/iconExport/Allicon';
import {
  NGBell,
  NGCalendarTableParticipant,
  NGDanger,
  NGDownload,
  NGDropDown,
  NGTelegramOutline,
  NGWatch,
} from '@assets/iconExport/ExportIcon';

import {NGButton} from '@/components/ng-button/NGButton';
import NgPopOver from '@/components/ng-popover/NGPopOver';
import {NgProgress} from '@/components/ng-progression/NGProgress';
import NGText from '@/components/ng-text/NGText';
import {
  InvitationStatus,
  ProjectStatus,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {
  colorBlack,
  colorDisable,
  colorWhite,
} from '@/constant/style/StyleConstant';
import {store} from '@/redux';
import {projectDetailAction} from '@/redux/slides/authentication/authenticationSlide';

import {CancelProjectDialog} from '@/components/ng-cancel-project/NGCancelProject';
import {useSendReminderMutation} from '@/redux/slides/process-control';
import {convertUTCToLocalTimeCN} from '@/utils/common/ConvertDatetoSecond';
import {Box, Divider, MenuItem, Stack} from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import {useSnackbar} from 'notistack';
import React from 'react';
import {useTranslation} from 'react-i18next';

const iconStyle = {
  mr: 2,
};

export function ProjectRightSide({data}: {data?: any}) {
  console.log('RIGHT_SIDE', data);
  const [dataResponse, setDataResponse] = React.useState<
    {
      fileName: string;
      file64: string;
      docId: string;
    }[]
  >([]);
  const [sendReminder] = useSendReminderMutation();
  const [cancelProject, setCancelProject] = React.useState(false);

  const getFilePdfProjectDetailData = () => {
    data.documents.map(async (item: any) => {
      const fileBase64 = await getFilePdfProjectDetail({
        docName: item.fileName,
      });
      setDataResponse(prevState => [
        ...prevState,
        {file64: fileBase64, fileName: item.originalFileName, docId: item.id},
      ]);
    });
  };

  const {t} = useTranslation();
  const [valueOfProgress, setValueOfProgress] = React.useState<number>(0);
  const [btnLoading, setBtnLoading] = React.useState(false);
  const [btnLoadingAction, setBtnLoadingAction] = React.useState(false);

  const {enqueueSnackbar, closeSnackbar} = useSnackbar();
  const getData = () => {
    const progress = getSignatureProgress(data.signatories ?? []) * 100;
    setValueOfProgress(progress);
  };
  /** Send reminder */
  const handleSendReminder = async () => {
    if (!$ok(data.flowId)) {
      return;
    }
    setBtnLoading(true);
    try {
      await sendReminder({flowId: data.flowId});
      enqueueSnackbar(t(Localization('invitation', 'reminder-is-sent')), {
        variant: 'successSnackbar',
      });
      setBtnLoading(false);
    } catch (e: any) {
      setBtnLoading(false);
      enqueueSnackbar(e ? e.message : UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };
  React.useEffect(() => {
    getData();
    return () => {
      // when exit profile page close Snackbar too
      closeSnackbar();
    };
  }, []);
  React.useEffect(() => {
    getFilePdfProjectDetailData();
  }, [data]);
  return (
    <Box
      display={'flex'}
      sx={{
        position: 'absolute',
        right: 50,
        top: '25%',
        width: '550px',
      }}
      justifyContent={'flex-end'}>
      <Box
        sx={{width: 'inherit'}}
        boxShadow={1}
        borderRadius={pixelToRem(8)}
        pl={pixelToRem(24)}
        pr={pixelToRem(20)}
        py={pixelToRem(20)}
        bgcolor={'white'}>
        <Stack gap={pixelToRem(20)} justifyContent={'center'}>
          <HStack
            sx={{
              width: '100%',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
            }}>
            <NGText
              text={t(
                Localization(
                  'projectStatus',
                  $ok(data.status) ? data.status : 'IN_PROGRESS',
                ),
              )}
              myStyle={{
                color: 'Black.main',
                fontWeight: 600,
                cursor: 'pointer',
                fontSize: pixelToRem(22),
                lineHeight: pixelToRem(32),
              }}
            />
            <NGText
              text={valueOfProgress + '%'}
              color={'Black.main'}
              fontSize={pixelToRem(18)}
              lineHeight={pixelToRem(28)}
              myStyle={{fontWeight: 600}}
            />
          </HStack>
          <NgProgress
            sx={{
              width: '100%',
              height: '10px',
              borderRadius: 5,
              '& .MuiLinearProgress-bar': theme => {
                if (
                  data.status === ProjectStatus.EXPIRED ||
                  data.status === ProjectStatus.ABANDON
                ) {
                  return {backgroundColor: colorDisable};
                } else if (valueOfProgress === 100) {
                  return {backgroundColor: 'green'};
                } else if (
                  /** In progress change to grey when it's refused with orderSign is True **/
                  data.status === InvitationStatus.REFUSED &&
                  data.orderSign
                ) {
                  return {backgroundColor: 'DarkGrey.main'};
                }
                return {
                  backgroundColor: 'Primary.main',
                  backgroundImage: `linear-gradient(235deg, ${theme.palette.primary.main} 15%, rgba(255,255,255,1) 20%, ${theme.palette.primary.main} 25%, ${theme.palette.primary.main} 100%)`,
                  backgroundSize: '12px 45px',
                };
              },
            }}
            variant="determinate"
            value={valueOfProgress}
          />
          <Stack
            direction={'row'}
            justifyContent={'space-between'}
            gap="20px"
            alignItems="center">
            <Box>
              <NGText
                text={
                  $ok(data?.createdAt) &&
                  convertUTCToLocalTimeCN(data.createdAt) + ' '
                }
                iconStart={
                  <NGTelegramOutline
                    sx={{
                      color: 'Primary.main',
                      mr: pixelToRem(6),
                      fontSize: pixelToRem(18),
                    }}
                  />
                }
                color={'black.main'}
                myStyle={{
                  fontSize: pixelToRem(12),
                  fontWeight: 400,
                  lineHeight: pixelToRem(16),
                }}
              />
            </Box>
            <Box>
              <NGText
                text={
                  data.expireDate && convertUTCToLocalTimeCN(data.expireDate)
                  // : convertUTCToLocalTimeCN(new Date())
                }
                iconStart={
                  <NGWatch
                    sx={{
                      color: 'Primary.main',
                      mr: 0.8,
                      fontSize: pixelToRem(16),
                    }}
                  />
                }
                color={'black.main'}
                myStyle={{
                  fontSize: pixelToRem(12),
                  fontWeight: 400,
                  lineHeight: pixelToRem(16),
                }}
              />
            </Box>
          </Stack>
          <Divider sx={{width: '100%'}} />
          <Stack
            direction={'row'}
            justifyContent={'space-between'}
            width={'100%'}>
            <Box sx={{width: '70%'}} zIndex={5}>
              {valueOfProgress === 100 ? (
                <NGButton
                  disabled={data.status === ProjectStatus.EXPIRED || btnLoading}
                  icon={
                    btnLoading ? (
                      <CircularProgress
                        color="inherit"
                        size={'20px'}
                        sx={{
                          mx: pixelToRem(8),
                        }}
                      />
                    ) : (
                      <NGDownload
                        sx={{
                          ml: pixelToRem(8),
                          pt: pixelToRem(2),
                          color: '#ffffff',
                        }}
                      />
                    )
                  }
                  locationIcon={'end'}
                  title={
                    <NGText
                      text={t(
                        Localization('sign-document', 'download-document'),
                      )}
                      myStyle={{
                        color: 'white',
                        fontSize: pixelToRem(11),
                        fontWeight: 600,
                        lineHeight: pixelToRem(20),
                      }}
                    />
                  }
                  myStyle={{
                    width: '100%',
                    height: '100%',
                    '&.Mui-disabled': {
                      bgcolor: colorDisable,
                      color: colorWhite,
                    },
                  }}
                  bgColor={'primary'}
                  onClick={async () => {
                    setBtnLoading(true);
                    const res = await downloadCurrentDocumentProject(
                      dataResponse[0].docId,
                    );
                    if (res) {
                      setBtnLoading(false);
                      window.open(res);
                    }
                  }}
                />
              ) : (
                <NGButton
                  onClick={handleSendReminder}
                  disabled={
                    (data.status === InvitationStatus.REFUSED &&
                      data.orderSign) ||
                    data.status === ProjectStatus.EXPIRED ||
                    data.status === ProjectStatus.DRAFT ||
                    data.status === ProjectStatus.ABANDON
                  }
                  icon={
                    btnLoading ? (
                      <CircularProgress
                        color="inherit"
                        size={'20px'}
                        sx={{
                          mx: pixelToRem(8),
                        }}
                      />
                    ) : (
                      <NGBell sx={{ml: 2, fontSize: pixelToRem(16)}} />
                    )
                  }
                  locationIcon={'end'}
                  title={
                    <NGText
                      text={t(Localization('project', 'send-a-reminder'))}
                      myStyle={{
                        fontSize: pixelToRem(11),
                        fontWeight: 600,
                        lineHeight: pixelToRem(20),
                        letterSpacing: '1%',
                        color: 'white',
                        textTransform: 'none',
                      }}
                    />
                  }
                  myStyle={{
                    width: '100%',
                    height: '100%',
                    borderRadius: pixelToRem(6),
                    '&.MuiButton-contained': {
                      fontWeight: 600,
                      textTransform: 'capitalize',
                    },
                    '&.Mui-disabled': {
                      bgcolor: colorDisable,
                      color: colorWhite,
                    },
                    '&:hover': {
                      bgcolor: colorBlack,
                    },
                  }}
                  bgColor={'primary'}
                />
              )}
            </Box>
            <Box sx={{width: '25%'}}>
              <NgPopOver
                button={
                  <NGButton
                    icon={
                      <NGDropDown
                        sx={{
                          fontSize: pixelToRem(15),
                          ml: pixelToRem(8),
                          mt: pixelToRem(4),
                        }}
                      />
                    }
                    locationIcon={'end'}
                    title={
                      <NGText
                        text={'Actions'}
                        myStyle={{
                          fontSize: pixelToRem(11),
                          fontWeight: 600,
                          lineHeight: pixelToRem(20),
                          letterSpacing: '1%',
                        }}
                      />
                    }
                    myStyle={{
                      width: '100%',
                      height: '2.5rem',
                      borderRadius: pixelToRem(6),
                      border: '1px solid #000000',
                    }}
                    bgColor={'buttonWhite'}
                  />
                }
                contain={
                  <>
                    <MenuItem
                      disableRipple
                      onClick={() =>
                        store.dispatch(
                          projectDetailAction({'modified-date': true}),
                        )
                      }>
                      <NGCalendarTableParticipant sx={{...iconStyle}} />
                      <NGText
                        text={t(
                          Localization('list-action', 'modify-expire-date'),
                        )}
                      />
                    </MenuItem>

                    <MenuItem
                      onClick={async () => {
                        setBtnLoadingAction(true);
                        const res = await downloadCurrentDocumentProject(
                          dataResponse[0].docId,
                        );
                        if (res) {
                          setBtnLoadingAction(false);
                          window.open(res);
                        }
                      }}
                      disableRipple>
                      {btnLoadingAction ? (
                        <CircularProgress
                          color="inherit"
                          size={'20px'}
                          sx={{
                            mx: pixelToRem(8),
                          }}
                        />
                      ) : (
                        <NGDownloadIcon sx={{...iconStyle}} />
                      )}
                      <NGText
                        text={t(
                          Localization('list-action', 'download-document'),
                        )}
                      />
                    </MenuItem>

                    <Divider />
                    <MenuItem
                      disableRipple
                      onClick={() => setCancelProject(true)}>
                      <NGDanger sx={{...iconStyle}} />
                      <NGText
                        text={t(Localization('list-action', 'cancel-project'))}
                      />
                    </MenuItem>
                  </>
                }
              />
            </Box>
          </Stack>
          {cancelProject && data && (
            <CancelProjectDialog
              data={{
                projectId: data.id,
                projectName: data.name,
              }}
              open={cancelProject}
              setCancelProject={setCancelProject}
            />
          )}
        </Stack>
      </Box>
    </Box>
  );
}
