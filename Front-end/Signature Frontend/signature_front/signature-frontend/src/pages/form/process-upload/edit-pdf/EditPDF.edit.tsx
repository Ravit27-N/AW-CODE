import {PersonSVG} from '@/assets/svg/person/person';
import {SettingSVG} from '@/assets/svg/setting/setting';
import {TextSVG} from '@/assets/svg/text/text';
import {textColor} from '@/components/ng-group-avatar/NGGroupAvatar';
import {
  KeySignatureLevel,
  LuApproval,
  STEP,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  clearTempFiles,
  deleteAnnotations,
  resetUpdateDocumentToAnnotaitons,
  setSignatureLevels,
  storeTempFile,
  updateAnnotaions,
  updateDocumentToAnnotaitons,
} from '@/redux/slides/authentication/authenticationSlide';
import {useLazyGetSpecificSignatureLevelsQuery} from '@/redux/slides/corporate-admin/corporateSettingSlide';
import {
  projectSlide,
  useGetProjectByIdQuery,
  useUpdateProjectStepThreeMutation,
} from '@/redux/slides/project-management/project';
import {getNameByFirstIndex} from '@/utils/common/HandlerFirstName_LastName';
import {splitUserCompany} from '@/utils/common/String';
import {$isarray} from '@/utils/request/common/type';
import mentionSVG from '@assets/svg/mention.svg';
import paraphSVG from '@assets/svg/paraph.svg';
import signatureSVG from '@assets/svg/signature.svg';
import {
  Backdrop,
  Box,
  CircularProgress,
  IconButton,
  Stack,
} from '@mui/material';
import WebViewer, {WebViewerInstance} from '@pdftron/webviewer';
import React from 'react';
import env from '../../../../../env.config';
import EditLeftSide from './EditLeftSide.edit';
import {SnackBarMui} from './other/common';
import Champ from './rightSide/Champ';

const EditPDF = ({
  setActiveStep,
  setUploadStep3,
  uploadStep3,
}: {
  setActiveStep: React.Dispatch<React.SetStateAction<number>>;
  uploadStep3: boolean;
  setUploadStep3: React.Dispatch<React.SetStateAction<boolean>>;
}) => {
  const viewer = React.useRef<HTMLElement | null>(null);
  const [responseInfo, setResponseInfo] = React.useState<{
    severity: 'error' | 'warning' | 'info' | 'success';
    message: string;
  } | null>(null);
  const {project} = useAppSelector(state => state.authentication);
  const [activeStep, setStep] = React.useState<0 | 1 | 2>(1);
  const [uploadLoading, setUploadLoading] = React.useState(false);
  const [loading, setLoading] = React.useState(false);
  const dispatch = useAppDispatch();
  const {tempFiles, signatureLevels} = useAppSelector(
    state => state.authentication,
  );
  const [instance, setInstance] = React.useState<WebViewerInstance | null>(
    null,
  );
  const {
    data: documentsData,
    currentData: documentsCurrentData,
    isLoading,
    isSuccess,
  } = useGetProjectByIdQuery({
    id: project.id!,
  });
  // Update project step 3 endpoint
  const [updateProjectStepThree] = useUpdateProjectStepThreeMutation();

  // Set canvas signatory
  const setCanvasSignatory = (
    signatoryName: string,
    index: number,
    associate_number: number,
    instance: WebViewerInstance | null,
    x: number,
    y: number,
    detailId: number | string,
    pageNum: number,
    signatoryId: number | string,
    text: string,
  ) => {
    const {Tools, Annotations, annotationManager} = instance!.Core;
    let documentId;
    store.getState().authentication.tempFiles.forEach(file => {
      if (file.active) {
        documentId = file.documentId;
      }
    });
    annotationManager.deselectAllAnnotations();
    const tool = instance?.Core.documentViewer.getTool(
      'AnnotationCreateRubberStamp',
    );
    const canvas = document.getElementById('myCanvas') as HTMLCanvasElement;
    const img = document.getElementById('img-sign') as HTMLImageElement;
    const context = canvas?.getContext('2d')!;
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.roundRect(2, 5, 400, 100, 20);
    context.strokeStyle = textColor[index];
    context.stroke();
    context.drawImage(img, 16, 40, 30, 40);
    context.font = 'bold 20px Arial';
    context.fillStyle = 'black';
    context.fillText(signatoryName, 60, 62);

    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = pageNum;
    stampAnnot.X = x;
    stampAnnot.Y = y;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.setAssociatedNumber(associate_number);
    stampAnnot.Subject = `${text}-${signatoryId}-${documentId}-Signatory-Lu et approuvé-${detailId}`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotationManager.addAnnotation(stampAnnot);
    annotationManager.showAnnotation(stampAnnot);
    annotationManager.selectAnnotation(stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Approved']);
    }
    return tool;
  };

  // Set canvas approve
  const setCanvasApproved = async (
    index: number,
    associate_number: number,
    instance: WebViewerInstance | null,
    x: number,
    y: number,
    detailId: number | string,
    pageNum: number,
    signatoryId: number | string,
  ) => {
    const {Tools, Annotations, annotationManager} = instance!.Core;
    let documentId;
    store.getState().authentication.tempFiles.forEach(file => {
      if (file.active) {
        documentId = file.documentId;
      }
    });
    annotationManager.deselectAllAnnotations();
    const tool = instance?.Core.documentViewer.getTool(
      'AnnotationCreateRubberStamp',
    );
    const canvas = document.getElementById('myCanvas-2') as HTMLCanvasElement;
    const img = document.getElementById('img-mention') as HTMLImageElement;
    const context = canvas?.getContext('2d')!;
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.roundRect(2, 5, 400, 100, 20);
    context.strokeStyle = textColor[index];
    context.stroke();
    context.drawImage(img, 16, 40, 30, 40);
    context.font = 'bold 20px Arial';
    context.fillStyle = 'black';
    context.fillText(LuApproval, 60, 62);
    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = pageNum;
    stampAnnot.X = x;
    stampAnnot.Y = y;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.setAssociatedNumber(associate_number);
    stampAnnot.Subject = `${LuApproval}-${signatoryId}-${documentId}-Approval-Signatory-${detailId}`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotationManager.addAnnotation(stampAnnot);
    annotationManager.showAnnotation(stampAnnot);
    annotationManager.selectAnnotation(stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Approved']);
    }
    return tool;
  };

  // Set canvas approve
  const setCanvasParaph = async (
    index: number,
    associate_number: number,
    instance: WebViewerInstance | null,
    x: number,
    y: number,
    detailId: number | string,
    pageNum: number,
    signatoryId: number | string,
    signatoryName: string,
  ) => {
    const {Tools, Annotations, annotationManager} = instance!.Core;
    let documentId;
    store.getState().authentication.tempFiles.forEach(file => {
      if (file.active) {
        documentId = file.documentId;
      }
    });
    annotationManager.deselectAllAnnotations();
    const tool = instance?.Core.documentViewer.getTool(
      'AnnotationCreateRubberStamp',
    );
    const canvas = document.getElementById('myCanvas-3') as HTMLCanvasElement;
    const img = document.getElementById('img-paraph') as HTMLImageElement;
    const context = canvas?.getContext('2d')!;
    context.clearRect(0, 0, canvas.width, canvas.height);
    context.roundRect(2, 5, 400, 100, 20);
    context.strokeStyle = textColor[index];
    context.stroke();
    context.drawImage(img, 16, 33, 30, 40);
    context.font = 'bold 20px Arial';
    context.fillStyle = 'black';
    context.fillText(getNameByFirstIndex(signatoryName), 60, 62);
    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = pageNum;
    stampAnnot.X = x - 180;
    stampAnnot.Y = y - 40;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.setAssociatedNumber(associate_number);
    stampAnnot.Subject = `${LuApproval}-${signatoryId}-${documentId}-Paraph-Signatory-${detailId}`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotationManager.addAnnotation(stampAnnot);
    annotationManager.showAnnotation(stampAnnot);
    annotationManager.selectAnnotation(stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Paraph']);
    }
    return tool;
  };

  const handleClose = () => {
    setResponseInfo(null);
  };

  // Load page web-viewer
  React.useEffect(() => {
    dispatch(clearTempFiles());
    if (isSuccess) {
      dispatch(
        setSignatureLevels({
          ...signatureLevels,
          signatureLevel: documentsData.signatureLevel,
        }),
      );
      documentsData.documents.forEach(async (doc: any) => {
        doc.documentDetails.forEach((detail: any) => {
          dispatch(updateDocumentToAnnotaitons({documentDetail: detail}));
        });
        const res = await dispatch(
          projectSlide.endpoints.viewDocument.initiate(
            {
              docId: doc.fileName,
            },
            {forceRefetch: true},
          ),
        ).unwrap();
        if (res) {
          dispatch(
            storeTempFile({
              file: `data:application/pdf;base64,${res}`,
              name: doc.originalFileName,
              documentId: doc.id,
            }),
          );
        }
      });

      WebViewer(
        {
          fullAPI: true,
          path: env.VITE_PUBLIC_FILE_PATH,
          disabledElements: ['ribbons', 'toolsHeader'],
          annotationUser: 'Veng',
          enableAnnotationNumbering: true,
          autoFocusNoteOnAnnotationSelection: true,
          loadAsPDF: true,
        },
        viewer.current!,
      ).then(async instance => {
        const {annotationManager, documentViewer} = instance.Core;

        documentViewer.addEventListener('pageComplete', () => {
          setLoading(true);
          annotationManager.enableRedaction();
        });
        annotationManager.addEventListener(
          'annotationChanged',
          (annotations: any[], action) => {
            if (action === 'add') {
              const annot = annotations[0];
              const id = annot['$i']['trn-associated-number'] as string;

              dispatch(updateAnnotaions({annotationId: id}));
            } else if (action === 'modify') {
              return;
            } else if (action === 'delete') {
              annotations.forEach((annot: any) => {
                const id = annot['$i']['trn-associated-number'] as
                  | string
                  | number;
                const {Subject} = annot;
                dispatch(
                  deleteAnnotations({annotationId: id, subject: Subject}),
                );
              });
            }
          },
        );

        setInstance(instance);
      });
    }
  }, [isSuccess]);

  // Store back annotation from backend server
  React.useMemo(() => {
    if (loading) {
      return store
        .getState()
        .authentication.annotations.forEach((ann, index: number) => {
          if (ann.documentDetails?.length! > 0) {
            return ann.documentDetails?.forEach(docDetail => {
              if (docDetail.type?.split('-')[0].toLowerCase() === 'signatory') {
                setCanvasSignatory(
                  ann.signatoryName,
                  index,
                  parseInt(docDetail.type?.split('-')[1], 10),
                  instance,
                  docDetail.x!,
                  docDetail.y!,
                  docDetail.id!,
                  docDetail.pageNum!,
                  docDetail.signatoryId!,
                  docDetail.text!,
                );
              } else if (
                docDetail.type?.split('-')[0].toLowerCase() === 'approval'
              ) {
                setCanvasApproved(
                  index,
                  parseInt(docDetail.type?.split('-')[1], 10),
                  instance,
                  docDetail.x!,
                  docDetail.y!,
                  docDetail.id!,
                  docDetail.pageNum!,
                  docDetail.signatoryId!,
                );
              } else if (
                docDetail.type?.split('-')[0].toLowerCase() === 'paraph'
              ) {
                setCanvasParaph(
                  index,
                  parseInt(docDetail.type?.split('-')[1], 10),
                  instance,
                  docDetail.x!,
                  docDetail.y!,
                  docDetail.id!,
                  docDetail.pageNum!,
                  docDetail.signatoryId!,
                  ann.signatoryName,
                );
              }
            });
          }
          return null;
        });
    }
  }, [loading]);
  /** get company UUID form redux **/
  const company = splitUserCompany(
    store.getState().authentication.USER_COMPANY!,
  );
  /** end point for call specific signature levels **/
  const [trigger] = useLazyGetSpecificSignatureLevelsQuery();
  /** fetching the first time when companyId have or changed **/
  React.useEffect(() => {
    const handleFetch = async (
      uuid: string,
      signatureLevel: KeySignatureLevel,
    ) => {
      const res = await trigger({
        signatureLevel,
        uuid,
      }).unwrap();
      if (res) {
        dispatch(
          setSignatureLevels({
            companyUuid: res.companyUuid,
            signatureLevel: res.signatureLevel,
            companyChannel: res.companyChannel,
            companyFileType: res.companyFileType,
            fileType: res.fileType,
            documentTerms: res.documentTerms,
            personalTerms: res.personalTerms,
            identityTerms: res.identityTerms,
            channelReminder: res.channelReminder,
          }),
        );
      }
    };
    if (
      company.companyUuid &&
      documentsCurrentData &&
      documentsCurrentData.signatureLevel
    ) {
      handleFetch(
        company.companyUuid,
        signatureLevels.signatureLevel === KeySignatureLevel.NONE
          ? documentsCurrentData.signatureLevel
          : signatureLevels.signatureLevel,
      ).then(r => r);
    }
  }, [documentsCurrentData]);

  // Upload annotation to backend server
  React.useEffect(() => {
    const handleUploadStepThree = async () => {
      setUploadLoading(true);

      if (instance) {
        const annos = instance?.Core.annotationManager.getAnnotationsList();
        const annotation = annos
          .filter(x => {
            if (x.Subject !== null)
              return (
                x.Subject.includes('Approval') ||
                x.Subject.includes('Signatory') ||
                x.Subject.includes('Paraph')
              );
          })
          .map(ann => {
            const width = ann.getWidth();
            const height = ann.getHeight();
            const subject = ann.Subject;
            const x =
              subject.split('-')[3] === 'Paraph'
                ? ann.getX() + 180
                : ann.getX();
            const y =
              subject.split('-')[3] === 'Paraph' ? ann.getY() + 40 : ann.getY();
            const text = `${subject.split('-')[0]}`;
            const signatoryId = parseInt(subject.split('-')[1], 10);
            const fontName = 'Arial';
            const fontSize = 8;
            const contentType = 'text';
            const fileName = '';
            const textAlign = 1;
            const documentId = parseInt(subject.split('-')[2], 10);
            const type =
              subject.split('-')[3] + '-' + ann.getAssociatedNumber();
            const pageNum = ann.getPageNumber();
            const id = subject.split('-')[5] ?? '';

            return {
              x,
              y,
              width,
              height,
              text,
              signatoryId,
              fontName,
              fontSize,
              contentType,
              fileName,
              textAlign,
              pageNum,
              documentId,
              type,
              id,
            };
          });
        if (annotation.length < 1) {
          return;
        }
        try {
          return await updateProjectStepThree({
            id: project.id!,
            documents: [],
            signatories: [],
            name: 'Signature',
            details: [],
            documentDetails: annotation,
            signatureLevel: KeySignatureLevel.SIMPLE,
            orderSign: store.getState().authentication.project.orderSign,
            status: '1',
            step: 3,
          })
            .unwrap()
            .then(data => {
              return data;
            })
            .catch(e => {
              return e;
            });
        } catch (e) {
          return {
            error: {message: UNKOWNERROR},
          };
        }
      }
    };
    if (uploadStep3) {
      handleUploadStepThree().then(res => {
        setUploadStep3(false);
        setUploadLoading(false);
        dispatch(resetUpdateDocumentToAnnotaitons());
        if (res) {
          res.documents.forEach((doc: any) => {
            doc.documentDetails.forEach((detail: any) => {
              dispatch(updateDocumentToAnnotaitons({documentDetail: detail}));
            });
          });
        }
        if (res) {
          const {error} = res;
          if (error) {
            const {message} = res.error;
            if (message) {
              setResponseInfo({
                message,
                severity: 'error',
              });
            } else {
              setResponseInfo({
                message: res.error,
                severity: 'error',
              });
            }
          } else {
            setActiveStep(STEP.STEP4);
          }
        } else {
          setActiveStep(STEP.STEP4);
        }

        return null;
      });
    }
  }, [uploadStep3]);

  return isLoading ? (
    <>loading...</>
  ) : (
    <Stack
      direction={'row'}
      justifyContent={'space-between'}
      sx={{width: '100%', height: 'auto', bgcolor: '#ffffff'}}
      spacing={0.2}>
      <canvas
        id="myCanvas"
        width="410px"
        height="120px"
        style={{
          display: 'none',
        }}></canvas>
      <canvas
        id="myCanvas-2"
        width="410px"
        height="120px"
        style={{
          display: 'none',
          border: '#333 10px solid',
        }}></canvas>
      <canvas
        id="myCanvas-3"
        width="410px"
        height="120px"
        style={{
          display: 'none',
          border: '#333 10px solid',
        }}></canvas>
      <img
        src={signatureSVG}
        id="img-sign"
        width={'10px'}
        height={'10px'}
        style={{display: 'none'}}
        alt={'signatureSVG'}
      />
      <img
        src={mentionSVG}
        id="img-mention"
        width={'10px'}
        height={'10px'}
        style={{display: 'none'}}
        alt={'mentionSVG'}
      />
      <img
        src={paraphSVG}
        id="img-paraph"
        width={'10px'}
        height={'10px'}
        style={{display: 'none'}}
        alt={'paraphSVG'}
      />
      <Stack sx={{width: '20%'}}>
        {/* Edit Left Side */}
        {instance && tempFiles.length ? (
          <EditLeftSide
            instance={instance}
            pdfFiles={$isarray(tempFiles) ? [...tempFiles] : []}
          />
        ) : (
          <>loading...</>
        )}
      </Stack>
      {/* Pdf view by Pdf-tron  */}
      <Box ref={viewer} sx={{height: 'auto', width: '50%'}}></Box>

      {loading ? (
        <Stack width={'30%'} direction={'row'}>
          {/* Edit right Side */}

          {activeStep === STEP.STEP2 && (
            <Champ instance={instance} setActiveStep={setActiveStep} />
          )}
          <Stack sx={{width: '60px'}} alignItems={'center'}>
            <Stack spacing={2} sx={{py: '20px'}}>
              <IconButton
                // onClick={() => setStep(0)}
                sx={{
                  borderLeft: activeStep === 0 ? `3px solid Primary.main` : '',
                  borderRadius: 0,
                  p: 2,
                }}>
                <PersonSVG
                  sx={{color: activeStep === 0 ? 'aPrimary.main' : 'black'}}
                />
              </IconButton>
              <IconButton
                onClick={() => setStep(1)}
                sx={{
                  borderLeft: activeStep === 1 ? `3px solid Primary.main` : '',
                  borderRadius: 0,
                  p: 2,
                }}>
                <TextSVG
                  sx={{color: activeStep === 1 ? 'Primary.main' : 'black'}}
                />
              </IconButton>

              <IconButton
                // onClick={() => setStep(2)}
                sx={{
                  borderLeft: activeStep === 2 ? '3px solid Primary.main' : '',
                  borderRadius: 0,
                  p: 2,
                }}>
                <SettingSVG
                  sx={{color: activeStep === 2 ? 'Primary.main' : 'black'}}
                />
              </IconButton>
            </Stack>
          </Stack>
        </Stack>
      ) : (
        <Stack width={'30%'} direction={'row'}>
          loading...
        </Stack>
      )}
      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
        open={uploadLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      {responseInfo && (
        <SnackBarMui
          open={!!responseInfo}
          handleClose={handleClose}
          message={responseInfo?.message ?? ''}
          severity={responseInfo?.severity ?? 'info'}
        />
      )}
    </Stack>
  );
};

export default EditPDF;
