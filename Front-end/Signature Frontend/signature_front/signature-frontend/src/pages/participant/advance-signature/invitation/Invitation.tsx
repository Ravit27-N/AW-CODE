import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {NGPdfView} from '@/components/ng-pdf-view/NGPdfView';
import {
  ProjectStatus,
  SIGNING_PROCESS,
  defaultColor,
  KeySignatureLevel,
  processAdvancedInvitation,
} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {
  StyleConstant,
  colorBlack,
  colorDisable,
  colorPrimary,
  colorWhite,
} from '@/constant/style/StyleConstant';

import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {useGetProjectByFlowIdQuery} from '@/redux/slides/process-control/participant';
import {secondToFullDate} from '@/utils/common/Date';
import {pixelToRem} from '@/utils/common/pxToRem';
import {getFilePdf} from '@/utils/request/services/MyService';
import bgLogo from '@assets/background/invitation/NGInvitation.svg';

import certignaLogo from '@assets/background/login/NGLogo.svg';
import {SendIcon} from '@assets/svg/send/sendIcon';
import NGText from '@components/ng-text/NGText';

import {
  Button,
  Card,
  CardActions,
  CardContent,
  Container,
  Drawer,
  Stack,
  Typography,
  useMediaQuery,
} from '@mui/material';
import Checkbox from '@mui/material/Checkbox';

import {Box} from '@mui/system';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useNavigate, useParams} from 'react-router-dom';

import {handlerFirstNameAndLastName} from '@/utils/common/HandlerFirstName_LastName';
import {heightDrawer} from '@/utils/common/HeightDrawer';

import {validateRoleSign} from '@pages/participant/common/checkRole';
import InvitationLayout from '@pages/participant/invitation/InvitationLayout';

const PDFCard = ({
  content,
  pages,
  fileName,
  xl,
}: {
  content: JSX.Element[] | JSX.Element;
  pages: number | null;
  fileName: string;
  xl: boolean;
}) => {
  return (
    <Card sx={{boxShadow: 3, width: xl ? '327px' : '311px', height: '231px'}}>
      <CardContent sx={{maxHeight: '120px', zIndex: 0, alignItems: 'center'}}>
        {content}
      </CardContent>
      <CardActions sx={{p: 0}}>
        <Box sx={{mt: 6, zIndex: 1, bgcolor: '#ffffff', width: '100%', p: 1.5}}>
          <Typography component="p" sx={{fontWeight: 600, fontSize: '14px'}}>
            {fileName.length < 25
              ? fileName
              : `${fileName.substring(0, 25)} ...`}
          </Typography>
          <Typography component="p" sx={{fontWeight: 400, fontSize: '14px'}}>
            {pages ?? '...'} pages
          </Typography>
        </Box>
      </CardActions>
    </Card>
  );
};

const AdvanceInvitationPages = () => {
  //Redux invitation
  const {inviter, timeDelivery, documents} = useAppSelector(
    state => state.authentication.invitation,
  );
  const {signatureLevels} = useAppSelector(state => state.authentication);
  const xl = useMediaQuery(`(min-width:1441px)`);
  // Desktop style permission delete denied
  const [desktopToggle] = React.useState(false);
  const [desktopChecked, setDesktopChecked] = React.useState(false);
  const [acceptIdentity, setAcceptIdentity] = React.useState(false);
  const [activeStep, setActiveStep] = React.useState<number>(0);
  const [pages, setPages] = React.useState<null | number>(null);
  const navigate = useNavigate();
  const {t} = useTranslation();
  const [dataResponse, setDataResponse] = React.useState<
    {
      fileName: string;
      file64: string;
    }[]
  >([]);
  const {id} = useParams();
  const queryParameters = new URLSearchParams(window.location.search);
  const {theme} = useAppSelector(state => state.enterprise);
  const {data, isLoading} = useGetProjectByFlowIdQuery({
    id: id + '?' + queryParameters,
  });
  // ** back to root if signature level is null **/
  React.useEffect(() => {
    if (
      signatureLevels.signatureLevel === KeySignatureLevel.NONE &&
      processAdvancedInvitation
    ) {
      navigate(`${Route.participant.root}/${id}?${queryParameters}`);
    }
  }, [signatureLevels.signatureLevel]);
  /** +====================================  **/
  React.useMemo(() => {
    if (data) {
      const {
        actor: {role},
        projectStatus,
      } = data;

      if (projectStatus === ProjectStatus.EXPIRED) {
        navigate(
          `${Route.participant.advance.expiredProject}/${id}?${queryParameters}`,
        );
      }
      const validate = validateRoleSign(role);
      if (validate) {
        navigate(`${validate}/${id}?${queryParameters}`);
      }
      data.documents.map(async (item: any) => {
        const fileBase64 = await getFilePdf({
          flowId: id + '?' + queryParameters,
          docId: item.docId,
        });
        setDataResponse(prevState => [
          ...prevState,
          {file64: fileBase64, fileName: item.name},
        ]);
      });
    }
  }, [data]);

  // Loading fetch document
  if (isLoading) {
    return <>loading...</>;
  }

  const onLoadSuccess = ({numPages}: PDFDocumentProxy) => {
    setPages(numPages);
  };

  const validateTemplateId = () => {
    if ('signingProcess' in data!) {
      const {signingProcess} = data as {signingProcess: SIGNING_PROCESS};
      let goto = Route.participant.viewFile;
      if (signingProcess) {
        if (
          [SIGNING_PROCESS.COSIGN, SIGNING_PROCESS.COUNTER_SIGN].indexOf(
            signingProcess,
          ) > -1
        ) {
          goto = Route.participant.viewSignatoryFile;
        } else if (signingProcess === SIGNING_PROCESS.INDIVIDUAL_SIGN) {
          goto = Route.participant.viewFile;
        }
      }
      navigate(`${goto}/${id}?${queryParameters}`);
    }
  };

  return (
    <InvitationLayout>
      {[0, 1].indexOf(activeStep) > -1 && (
        <>
          <Stack
            width="100%"
            alignItems={'center'}
            height={xl ? '397px' : '254px'}
            sx={{
              background: `url(${bgLogo})`,
              backgroundRepeat: 'no-repeat',
              backgroundPosition: 'center',
              backgroundSize: 'cover',
            }}>
            <Stack
              sx={{
                width: '375px',
                height: '100%',
                p: '40px 24px 45px 24px',
                gap: '32px',
                alignItem: 'center',
              }}>
              <Stack
                width="327px"
                height="174px"
                gap={'24px'}
                alignItems={'center'}>
                <Stack height="128px" alignItems={'center'} direction="row">
                  <Typography
                    component="div"
                    sx={{
                      fontSize: '22px',
                      fontWeight: 600,
                      textAlign: 'center',
                    }}>
                    {data ? data.projectName : '...'}
                    <Typography
                      component="p"
                      sx={{fontSize: '22px', fontWeight: 600}}>
                      {data ? handlerFirstNameAndLastName({data}) : '...'}
                    </Typography>

                    <Typography
                      component="p"
                      sx={{fontSize: '22px', fontWeight: 600}}>
                      {t(Localization('invitation', 'invite-to-sign'))}
                    </Typography>
                    <Typography
                      component="p"
                      sx={{
                        fontSize: '22px',
                        fontWeight: 600,
                        color: 'Primary.main',
                      }}>
                      {data?.documents.length ?? 0} documents
                    </Typography>
                  </Typography>
                </Stack>

                <Stack
                  direction={'row'}
                  sx={{alignItems: 'center'}}
                  borderColor={'red'}>
                  <SendIcon sx={{color: 'Primary.main'}} />
                  <Typography
                    component="p"
                    sx={{fontWeight: 400, fontSize: '14px'}}>
                    {secondToFullDate(data?.invitationDate!) ??
                      new Date(timeDelivery!).toDateString()}
                  </Typography>
                </Stack>
              </Stack>
            </Stack>
          </Stack>

          <Container
            sx={{
              width: 'auto',
              height: 'auto',
              alignItem: 'center',
              justifyContent: 'center',
              mt: xl ? '60px' : '45px',
            }}>
            <Stack
              sx={{
                alignItems: 'center',
                py: 0.5,
                height: `calc(100vh - ${xl ? '614px' : '457px'})`,
                overflow: 'hidden',
                overflowY: 'scroll',

                ...StyleConstant.scrollNormal,
                gap: '10px',
              }}>
              {/*  List all PDF*/}
              {dataResponse.map((item, index) => (
                <Stack key={item.file64 + item.fileName}>
                  <PDFCard
                    xl={xl}
                    fileName={item.fileName}
                    pages={pages}
                    content={
                      <NGPdfView
                        height={200}
                        width={xl ? 295 : 278}
                        handleSuccess={onLoadSuccess}
                        file={'Data:application/pdf;base64,' + item.file64}
                      />
                    }
                  />
                </Stack>
              ))}
            </Stack>
          </Container>
        </>
      )}
      <Drawer
        PaperProps={{
          style: {
            backgroundColor: 'white',
            height: heightDrawer(activeStep),
            borderTopLeftRadius: pixelToRem(12),
            borderTopRightRadius: pixelToRem(12),
            alignItems: 'center',
          },
        }}
        anchor={'bottom'}
        open={activeStep === 0}
        ModalProps={{disableScrollLock: true}}
        hideBackdrop={[1, 2].indexOf(activeStep) > -1}>
        {activeStep === 0 && (
          <Stack
            width="375px"
            height="469px"
            sx={{
              alignItems: 'center',
              py: '40px',
            }}>
            <Stack
              gap={'28px'}
              alignItems={'center'}
              width={'327px'}
              height={'210px'}>
              <Stack>
                <img
                  src={theme[0].logo!}
                  style={{maxWidth: '170px', height: '40px'}}
                  alt={'Logo'}
                />
              </Stack>
              <Stack
                height="142px"
                width={'100%'}
                gap={'24px'}
                alignItems={'flex-start'}>
                <Stack width={'100%'}>
                  <NGText
                    fontSize={'18px'}
                    fontWeight="400"
                    myStyle={{
                      width: '100%',
                      height: '26px',
                      textAlign: 'center',
                    }}
                    text="Pour continuer, consultez les "
                  />
                  <NGText
                    component="p"
                    fontSize={'18px'}
                    fontWeight="400"
                    myStyle={{
                      width: '100%',
                      height: '26px',
                      textAlign: 'center',
                      textDecoration: 'underline',
                    }}
                    text=" Conditions Générales dUtilisation"
                  />
                  <NGText
                    fontSize={'18px'}
                    fontWeight="400"
                    myStyle={{
                      width: '100%',
                      height: '26px',
                      textAlign: 'center',
                    }}
                    text="de la plateforme."
                  />
                </Stack>
                {/** checkBox **/}
                <Stack width={'100%'} spacing={2}>
                  {/**  checkbox accept terms **/}
                  <Stack
                    sx={{
                      cursor: 'pointer',
                      width: '100%',
                      height: '56px',
                      p: '11px',
                      border: `1px solid ${
                        desktopChecked ? theme[0].mainColor : '#E9E9E9'
                      }`,
                      borderRadius: '6px',
                      alignItem: 'center',
                    }}
                    direction={'row'}
                    gap={'8px'}>
                    <Checkbox
                      size="small"
                      checked={desktopChecked}
                      onChange={e => setDesktopChecked(e.target.checked)}
                      sx={{
                        width: '16px',
                        height: '100%',
                        color: '#BFBFBF',
                        '&.Mui-checked': {
                          color: 'Primary.main',
                        },
                      }}
                    />

                    <NGText
                      text={t(Localization('invitation', 'accept-the-terms'))}
                      onClick={() => {
                        setDesktopChecked(!desktopChecked);
                      }}
                      fontSize={'12px'}
                    />
                  </Stack>
                  {/**  checkbox accept relate identity **/}
                  <Stack
                    sx={{
                      cursor: 'pointer',
                      width: '100%',
                      height: '56px',
                      p: '11px',
                      border: `1px solid ${
                        acceptIdentity ? theme[0].mainColor : '#E9E9E9'
                      }`,
                      borderRadius: '6px',
                      alignItem: 'center',
                    }}
                    direction={'row'}
                    gap={'8px'}>
                    <Checkbox
                      size="small"
                      checked={acceptIdentity}
                      onChange={e => setAcceptIdentity(e.target.checked)}
                      sx={{
                        width: '16px',
                        height: '100%',
                        color: '#BFBFBF',
                        '&.Mui-checked': {
                          color: 'Primary.main',
                        },
                      }}
                    />

                    <NGText
                      text={t(
                        Localization('invitation', 'accept-relate-identity'),
                      )}
                      onClick={() => {
                        setAcceptIdentity(!acceptIdentity);
                      }}
                      fontSize={'12px'}
                    />
                  </Stack>
                </Stack>
              </Stack>
            </Stack>
            <Stack width={'327px'} height="56px">
              <Button
                disabled={!desktopChecked || !acceptIdentity}
                onClick={() => setActiveStep(1)}
                variant="contained"
                sx={{
                  width: '100%',
                  p: '16px 32px',
                  height: '56px',
                  fontSize: '16px',
                  bgcolor: 'Primary.main',
                  borderRadius: '6px',
                  mt: '123px',
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
                }}>
                {t(Localization('title', 'validate'))}
              </Button>
            </Stack>
          </Stack>
        )}
      </Drawer>
      {/*  Button Start*/}{' '}
      {activeStep === 1 && (
        <Stack
          width={'100%'}
          justifyContent={'center'}
          alignItems={'center'}
          position={'absolute'}
          boxShadow={5}
          bgcolor={'white'}
          zIndex={1}
          bottom={0}>
          <Stack
            sx={{
              width: '375px',
              p: '24px 32px',
            }}>
            <Stack
              sx={{
                justifyContent: 'center',
                alignItems: 'center',
              }}>
              <Button
                onClick={() => validateTemplateId()}
                variant="contained"
                sx={{
                  width: '311px',
                  p: '16px 103px',
                  height: '56px',
                  bgcolor: 'Primary.main',
                  fontSize: '16px',
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
                }}>
                {t(Localization('invitation', 'start'))}
              </Button>
              {/* </NavLink> */}
            </Stack>
          </Stack>
        </Stack>
      )}
      {/*  =====================*/}
      <NGDialog
        open={desktopToggle}
        sx={{
          '& .MuiPaper-root': {
            boxSizing: 'border-box',
            borderRadius: '16px',
          },
        }}
        sxProp={{
          contentsSx: {
            p: 0,
          },
        }}
        contentDialog={
          <AddATeamContent
            desktopChecked={desktopChecked}
            setDesktopChecked={setDesktopChecked}
          />
        }
      />
    </InvitationLayout>
  );
};

export default AdvanceInvitationPages;

type IAddATeamContent = {
  desktopChecked: boolean;
  setDesktopChecked: React.Dispatch<React.SetStateAction<boolean>>;
};

const AddATeamContent = ({
  desktopChecked,
  setDesktopChecked,
}: IAddATeamContent): JSX.Element => {
  const {t} = useTranslation();
  return (
    <Stack gap={'29px'} width="549px" height="369px" p={'50px'}>
      <Stack
        gap={'28px'}
        alignItems={'center'}
        width={'449px'}
        height={'184px'}>
        <Stack>
          <img
            src={certignaLogo}
            style={{width: '170px', height: 'auto'}}
            alt={'Logo'}
          />
        </Stack>
        <Stack
          height="116px"
          width={'100%'}
          gap={'24px'}
          alignItems={'flex-start'}>
          <Stack width="100%" height="52px">
            <NGText
              fontSize={'18px'}
              fontWeight="400"
              myStyle={{
                width: '100%',
                textAlign: 'center',
              }}
              text={`Pour continuer, consultez les Conditions Générales dUtilisation de la plateforme.`}
            />
          </Stack>
          <Stack
            sx={{
              width: '100%',
              height: '40px',
              p: '10px 2px',
              border: `1px solid ${desktopChecked ? defaultColor : '#E9E9E9'}`,
              borderRadius: '6px',
              alignItem: 'center',
            }}
            gap={'8px'}
            direction={'row'}>
            <Checkbox
              size="small"
              checked={desktopChecked}
              onChange={e => setDesktopChecked(e.target.checked)}
              sx={{
                color: '#BFBFBF',
                '&.Mui-checked': {
                  color: defaultColor,
                },
              }}
            />

            <NGText
              text={t(
                Localization('invitation', 'i-have-read-and-accept-the-GCU'),
              )}
              onClick={() => {
                setDesktopChecked(!desktopChecked);
              }}
              fontSize={'12px'}
            />
          </Stack>
        </Stack>
      </Stack>
      <Stack width={'100%'}>
        <Button
          disabled={!desktopChecked}
          // onClick={() => setActiveStep(1)}
          variant="contained"
          sx={{
            minHeight: 0,
            minWidth: 0,
            width: '100%',
            height: '56px',
            // p: '16px 32px',
            fontSize: '16px',
            bgcolor: colorPrimary,
            borderRadius: '6px',
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
          }}>
          {t(Localization('title', 'validate'))}
        </Button>
      </Stack>
    </Stack>
  );
};
