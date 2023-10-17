import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {
  resetAnnotationActive,
  updateAnnotaionsActive,
} from '@/redux/slides/authentication/authenticationSlide';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import {
  Backdrop,
  Box,
  CircularProgress,
  Divider,
  Grid,
  IconButton,
  Stack,
  SxProps,
} from '@mui/material';
import React from 'react';
import {useTranslation} from 'react-i18next';

import {SixDoctsSVG} from '@/assets/svg/6dots/sixDots';
import {
  MentionSVG,
  ParaSVG,
  SignatureSVG,
} from '@/assets/svg/annotations/Annotation';
import {TextSVG} from '@/assets/svg/text/text';
import {bg, textColor} from '@/components/ng-group-avatar/NGGroupAvatar';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {store} from '@/redux';
import NGText from '@components/ng-text/NGText';
import ArrowDropDownIcon from '@mui/icons-material/ArrowDropDown';
import ArrowDropUpIcon from '@mui/icons-material/ArrowDropUp';
import {
  AddMore,
  Participants,
  TitleAddMore,
} from '@pages/form/process-upload/edit-pdf/other/common';
import {IRecipient} from '@pages/form/process-upload/type';
import {WebViewerInstance} from '@pdftron/webviewer';
import {useCanvasByTemplate} from './useCanvasByTemplate';

const annoStyles: SxProps = {
  px: 0.5,
  borderRadius: 1,
  borderWidth: 1,
  borderStyle: 'solid',
  flexGrow: 1,
  display: 'flex',
  justifyContent: 'flex-start',
};

const ProjectPreparationChamp = ({
  setActiveStep,
  instance,
}: {
  setActiveStep: React.Dispatch<React.SetStateAction<number>>;
  instance: WebViewerInstance | null;
}) => {
  const {t} = useTranslation();
  const {setCanvasApproved, setCanvasParaph, setCanvasSignatory} =
    useCanvasByTemplate({
      instance,
    });
  // Data from redux
  const {
    signatories: secondSignatories,
    approvals: secondApprovals,
    viewers: secondViewers,
    recipients: secondRecipients,
    tempFiles,
    annotations,
    storeModel,
  } = useAppSelector(state => state.authentication);
  const {theme} = useAppSelector(state => state.enterprise);

  // Display participants
  const [display, setDisplay] = React.useState<{
    approvals: boolean;
    signatories: boolean;
    participants: boolean;
    recipients: boolean;
    viewers: boolean;
  }>({
    approvals: false,
    signatories: true,
    recipients: false,
    viewers: false,
    participants: true,
  });
  const [deleteProgress] = React.useState(false);

  /*
   ** Signatories static data
   */
  const [signatories] = React.useState<IRecipient[]>(secondSignatories);
  const [approvals] = React.useState<IRecipient[]>(secondApprovals);
  const [viewers] = React.useState<IRecipient[]>(secondViewers);
  const [recipients] = React.useState<IRecipient[]>(secondRecipients);
  /*
    const [annoActive, setAnnoactive] = React.useState<
      | 'D.AnnotationCreateFreeText'
      | 'D.AnnotationCreateStamp'
      | 'A.AnnotationCreateFreeText'
      | 'A.AnnotationCreateStamp'
      | 'A.AnnotationCreateRubberStamp'
      | 'A.CheckBoxFormFieldCreateTool'
      | 'A.RadioButtonFormFieldCreateTool'
      | 'Pan'
    >('Pan');
 */

  const [displayAnno, setDisplayAnno] = React.useState<
    {
      [k: number]: boolean;
    }[]
  >(
    Array.from(
      {
        length: secondSignatories.length,
      },
      (item, index: number) => ({
        [secondSignatories[index].id!]: index === 0,
      }),
    ),
  );

  const handleAddMore = () => {
    setActiveStep(v => v - 1);
  };
  return (
    <Stack sx={{width: '100%', border: `1px solid #E9E9E9`}} height="100%">
      <TitleAddMore
        name={t(Localization('pdf-edit', 'fields'))}
        icon={<TextSVG sx={{color: 'Primary.main'}} />}
        onClick={handleAddMore}
      />
      <Divider sx={{borderBottomWidth: 1}} />

      {/* Participants */}
      <AddMore name={t(Localization('table', 'participants'))} />
      <Divider sx={{borderBottomWidth: 1}} />

      {/* Viewers */}
      {viewers.length > 0 && (
        <>
          <AddMore
            name={t(Localization('upload-signatories', 'viewers'))}
            icon={
              display.viewers ? (
                <KeyboardArrowDownIcon />
              ) : (
                <KeyboardArrowUpIcon />
              )
            }
            onClick={() => setDisplay({...display, viewers: !display.viewers})}
            num={viewers.length}
          />
          <Divider sx={{borderBottomWidth: 1}} />
        </>
      )}
      <Stack sx={{display: display.viewers ? 'flex' : 'none'}}>
        {viewers.length > 0 ? (
          viewers.map(({lastName: name, firstName, id}, index: number) => (
            <Stack sx={{px: '2rem'}} key={id}>
              <Participants
                avatarSx={{
                  bgcolor: bg[index],
                  color: textColor[index],
                }}
                name={`${firstName} ${name}`}
                index={index}
                onClick={() => {
                  const annoActive = [...displayAnno];
                  annoActive[index][id as any] = !annoActive[index][id as any];
                  setDisplayAnno(annoActive);
                }}
              />
              <Divider sx={{borderBottomWidth: 1}} />
            </Stack>
          ))
        ) : (
          <NGText text={'NO data'} />
        )}
      </Stack>

      {/* Approvals */}
      {approvals.length > 0 && (
        <>
          <AddMore
            name={t(Localization('upload-signatories', 'approvals'))}
            icon={
              display.approvals ? (
                <KeyboardArrowDownIcon />
              ) : (
                <KeyboardArrowUpIcon />
              )
            }
            onClick={() =>
              setDisplay({...display, approvals: !display.approvals})
            }
            num={approvals.length}
          />
          <Divider sx={{borderBottomWidth: 1}} />
        </>
      )}
      <Stack sx={{display: display.approvals ? 'flex' : 'none'}}>
        {approvals.length ? (
          approvals.map(({lastName: name, firstName, id}, index: number) => (
            <Stack sx={{px: '2rem'}} key={id}>
              <Participants
                avatarSx={{
                  bgcolor: bg[index],
                  color: textColor[index],
                }}
                name={`${firstName} ${name}`}
                index={index}
                onClick={() => {
                  const annoActive = [...displayAnno];
                  annoActive[index][id as any] = !annoActive[index][id as any];
                  setDisplayAnno(annoActive);
                }}
              />
              <Divider sx={{borderBottomWidth: 1}} />
            </Stack>
          ))
        ) : (
          <></>
        )}
      </Stack>
      {signatories.length > 0 && (
        <>
          <AddMore
            name={t(Localization('upload-signatories', 'signatories'))}
            icon={
              display.signatories ? (
                <KeyboardArrowDownIcon />
              ) : (
                <KeyboardArrowUpIcon />
              )
            }
            onClick={() =>
              setDisplay({...display, signatories: !display.signatories})
            }
            num={secondSignatories.length}
          />
          <Divider sx={{borderBottomWidth: 1}} />
        </>
      )}
      <Stack
        sx={{
          display: display.signatories ? 'flex' : 'none',
          height: 'auto',
          overflow: 'hidden',
          overflowY: 'scroll',
          ...StyleConstant.scrollNormal,
        }}>
        {signatories.length && annotations.length ? (
          signatories.map(
            ({lastName: name, firstName, id, sortOrder}, index: number) => (
              <Stack key={id}>
                <Stack sx={{px: '2rem'}}>
                  <Participants
                    avatarSx={{
                      bgcolor: bg[index],
                      color: textColor[index],
                    }}
                    name={`${firstName} ${name}`}
                    index={index}
                    icon={
                      displayAnno[index][id as any] ? (
                        <ArrowDropDownIcon />
                      ) : (
                        <ArrowDropUpIcon />
                      )
                    }
                    onClick={() => {
                      const annoActive = [...displayAnno];
                      annoActive[index][id as any] =
                        !annoActive[index][id as any];
                      setDisplayAnno(annoActive);
                    }}
                  />
                  {tempFiles.length > 0 &&
                    storeModel?.signatories === signatories.length && (
                      <Box
                        sx={{
                          width: '100%',
                          py: 2,
                          display: displayAnno[index][id as any]
                            ? 'flex'
                            : 'none',
                        }}>
                        <Grid
                          container
                          rowSpacing={1}
                          columnSpacing={{xs: 1, sm: 2, md: 2}}>
                          {/* Mention */}
                          <Grid item xs={12} lg={6} xl={6}>
                            <IconButton
                              // disabled={
                              //   !!annotations.find(x => x.signatoryId === id)!
                              //     .annotationStamp
                              // }
                              onClick={async () => {
                                if (
                                  annotations.find(
                                    x => x.sortOrder === sortOrder,
                                  )!.dCreateStamp
                                ) {
                                  instance!.UI.setToolMode('Pan');
                                  const {Tools} = instance!.Core;
                                  const tool =
                                    instance?.Core.documentViewer.getTool(
                                      'AnnotationCreateRubberStamp',
                                    );
                                  if (
                                    tool instanceof Tools.RubberStampCreateTool
                                  ) {
                                    tool.setStandardStamps([]);
                                  }
                                  store.dispatch(
                                    resetAnnotationActive({signatoryId: id!}),
                                  );
                                } else {
                                  store.dispatch(
                                    updateAnnotaionsActive({
                                      signatoryId: id!,
                                      toolName: 'Signature',
                                      annotations,
                                    }),
                                  );
                                  await setCanvasSignatory(
                                    firstName!,
                                    name!,
                                    index,
                                  );
                                }
                              }}
                              sx={{
                                borderColor: annotations.find(
                                  x => x.sortOrder === sortOrder,
                                )!.dCreateStamp
                                  ? 'Primary.main'
                                  : undefined,
                                ...annoStyles,
                                width: '100%',
                                '&.MuiButtonBase-root.Mui-disabled': {
                                  bgcolor: '#A9A9A9',
                                  borderColor: 'Primary.main',
                                  opacity: '50%',
                                },
                              }}>
                              <SixDoctsSVG
                                sx={{pt: 0.5, color: 'Primary.main'}}
                                stroke={theme[0].mainColor!}
                              />
                              <SignatureSVG sx={{pt: 0.7}} />
                              <NGText
                                myStyle={{fontSize: '12px'}}
                                text={t(Localization('pdf-edit', 'signature'))}
                              />
                            </IconButton>
                          </Grid>

                          {/* Signature */}
                          <Grid item xs={12} lg={6} xl={6}>
                            <IconButton
                              // disabled={
                              //   !!annotations.find(x => x.signatoryId === id)!
                              //     .annotaionmention
                              // }
                              onClick={async () => {
                                if (
                                  annotations.find(
                                    x => x.sortOrder === sortOrder,
                                  )!.dMention
                                ) {
                                  instance!.UI.setToolMode('Pan');
                                  store.dispatch(
                                    resetAnnotationActive({signatoryId: id!}),
                                  );
                                } else {
                                  store.dispatch(
                                    updateAnnotaionsActive({
                                      signatoryId: id!,
                                      toolName: 'Mention',
                                      annotations,
                                    }),
                                  );
                                  await setCanvasApproved(
                                    firstName!,
                                    name!,
                                    index,
                                  );
                                }
                              }}
                              sx={{
                                borderColor: annotations.find(
                                  x => x.sortOrder === sortOrder,
                                )!.dMention
                                  ? 'Primary.main'
                                  : undefined,
                                ...annoStyles,
                                width: '100%',
                                '&.MuiButtonBase-root.Mui-disabled': {
                                  bgcolor: '#A9A9A9',
                                  borderColor: 'Primary.main',
                                  opacity: '50%',
                                },
                              }}>
                              <SixDoctsSVG
                                sx={{
                                  pt: 0.5,
                                }}
                                stroke={theme[0].mainColor!}
                              />
                              <MentionSVG sx={{pt: 0.5}} />
                              <NGText
                                myStyle={{fontSize: '12px'}}
                                text={t(Localization('pdf-edit', 'mention'))}
                              />
                            </IconButton>
                          </Grid>

                          {/* Paraph */}
                          {index === 0 && (
                            <Grid item xs={12} lg={6} xl={6}>
                              <IconButton
                                disabled={
                                  !!annotations.find(
                                    x => x.sortOrder === sortOrder,
                                  )!.annotationParaph
                                }
                                onClick={async () => {
                                  if (
                                    annotations.find(
                                      x => x.sortOrder === sortOrder,
                                    )!.dParaph
                                  ) {
                                    instance!.UI.setToolMode('Pan');
                                    store.dispatch(
                                      resetAnnotationActive({signatoryId: id!}),
                                    );
                                  } else {
                                    store.dispatch(
                                      updateAnnotaionsActive({
                                        signatoryId: id!,
                                        toolName: 'Paraph',
                                        annotations,
                                      }),
                                    );
                                    await setCanvasParaph(
                                      firstName!,
                                      name!,
                                      index,
                                    );
                                  }
                                }}
                                sx={{
                                  borderColor: annotations.find(
                                    x => x.sortOrder === sortOrder,
                                  )!.dParaph
                                    ? 'Primary.main'
                                    : undefined,
                                  ...annoStyles,
                                  width: '100%',
                                  '&.MuiButtonBase-root.Mui-disabled': {
                                    bgcolor: '#A9A9A9',
                                    borderColor: 'Primary.main',
                                    opacity: '50%',
                                  },
                                }}>
                                <SixDoctsSVG
                                  sx={{pt: 0.5}}
                                  stroke={theme[0].mainColor!}
                                />
                                <ParaSVG sx={{pt: 0.5}} />
                                <NGText
                                  myStyle={{fontSize: '12px'}}
                                  text={t(Localization('pdf-edit', 'initial'))}
                                />
                              </IconButton>
                            </Grid>
                          )}
                          {/* <Grid item xs={12} lg={6} xl={6}>
                    <IconButton
                      sx={{
                        ...annoStyles,
                        width: '100%',
                        '&.MuiButtonBase-root.Mui-disabled': {
                          bgcolor: '#A9A9A9',
                          borderColor: 'Primary.main',
                          opacity: '50%',
                        },
                      }}>
                      <SixDoctsSVG
                        sx={{pt: 0.5}}
                        stroke={theme[0].mainColor!}
                      />
                      <ParaSVG sx={{pt: 0.5}} />
                      <NGText
                        myStyle={{fontSize: '12px'}}
                        text={t(Localization('pdf-edit', 'initial'))}
                      />
                    </IconButton>
                  </Grid> */}

                          {/* CheckBox */}
                          {/* <Grid item xs={12} lg={6} xl={6}>
                    <IconButton
                      sx={{
                        ...annoStyles,
                        width: '100%',
                        '&.MuiButtonBase-root.Mui-disabled': {
                          bgcolor: '#A9A9A9',
                          borderColor: '#D6056A',
                          opacity: '50%',
                        },
                      }}>
                      <SixDoctsSVG
                        sx={{pt: 0.5}}
                        stroke={theme[0].mainColor!}
                      />
                      <CheckBoxSVG sx={{pt: 0.5}} />
                      <NGText
                        myStyle={{fontSize: '12px'}}
                        text={t(Localization('pdf-edit', 'checkbox'))}
                      />
                    </IconButton>
                  </Grid> */}

                          {/* Button radio */}
                          {/* <Grid item xs={12} lg={6} xl={6}>
                    <IconButton
                      sx={{
                        ...annoStyles,
                        width: '100%',
                        '&.MuiButtonBase-root.Mui-disabled': {
                          bgcolor: '#A9A9A9',
                          borderColor: '#D6056A',
                          opacity: '50%',
                        },
                      }}>
                      <SixDoctsSVG
                        sx={{pt: 0.5}}
                        stroke={theme[0].mainColor!}
                      />
                      <RadioSVG sx={{pt: 0.5}} />
                      <NGText
                        myStyle={{fontSize: '12px'}}
                        text={t(Localization('pdf-edit', 'radio'))}
                      />
                    </IconButton>
                  </Grid> */}

                          {/* <Grid item xs={6} sx={{flexGrow: 1}}>
                  <IconButton
                    onClick={() => {
                      instance!.UI.setToolMode(
                        'RadioButtonFormFieldCreateTool',
                      );
                      if (annoActive === 'A.RadioButtonFormFieldCreateTool') {
                        instance!.UI.setToolMode('Pan');
                        setAnnoactive('Pan');
                      } else {
                        instance!.UI.setToolMode(
                          'RadioButtonFormFieldCreateTool',
                        );
                        setAnnoactive('A.RadioButtonFormFieldCreateTool');
                      }
                    }}
                    sx={{
                      borderColor:
                        annoActive === 'A.RadioButtonFormFieldCreateTool'
                          ? '#D6056A'
                          : undefined,
                      ...annoStyles,
                      width: '100%',
                    }}>
                    <SixDoctsSVG sx={{pt: 0.5}} />
                    <RadioSVG sx={{pt: 0.5}} />
                    <Typography sx={{color: '#000000'}}>
                      {t(Localization('pdf-edit', 'radio'))}
                    </Typography>
                  </IconButton>
                </Grid> */}
                        </Grid>
                      </Box>
                    )}
                </Stack>
                <Divider sx={{borderBottomWidth: 1}} />
              </Stack>
            ),
          )
        ) : (
          <></>
        )}
      </Stack>
      {recipients.length > 0 && (
        <>
          <AddMore
            name={t(Localization('upload-signatories', 'receipts'))}
            icon={
              display.recipients ? (
                <KeyboardArrowDownIcon />
              ) : (
                <KeyboardArrowUpIcon />
              )
            }
            onClick={() =>
              setDisplay({...display, recipients: !display.recipients})
            }
            num={recipients.length}
          />
          <Divider sx={{borderBottomWidth: 2}} />
        </>
      )}
      <Stack sx={{display: display.recipients ? 'flex' : 'none'}}>
        {recipients.length ? (
          recipients.map(({lastName: name, firstName, id}, index: number) => (
            <Stack sx={{px: '2rem'}} key={id}>
              <Participants
                avatarSx={{
                  bgcolor: bg[index],
                  color: textColor[index],
                }}
                name={`${firstName} ${name}`}
                index={index}
                onClick={() => {
                  const annoActive = [...displayAnno];
                  annoActive[index][id as any] = !annoActive[index][id as any];
                  setDisplayAnno(annoActive);
                }}
              />
              <Divider sx={{borderBottomWidth: 2}} />
            </Stack>
          ))
        ) : (
          <></>
        )}
      </Stack>

      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={deleteProgress}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </Stack>
  );
};

export default ProjectPreparationChamp;
