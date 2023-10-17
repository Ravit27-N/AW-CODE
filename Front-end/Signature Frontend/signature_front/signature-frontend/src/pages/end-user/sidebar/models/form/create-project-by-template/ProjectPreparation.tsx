import {
  NGAddProject,
  NGArrowBackFill,
  NGDoc,
  NGParticipant,
  NGPen,
  NGThreeDotsVert,
  NGTrash,
} from '@/assets/Icon';
import {NGCirclePlus} from '@/assets/iconExport/Allicon';
import NGDropzoneComponent from '@/components/ng-dropzone/NGDropzone.component';
import NGGroupAvatar, {
  textColor,
} from '@/components/ng-group-avatar/NGGroupAvatar';
import NGText from '@/components/ng-text/NGText';
import {
  KeySignatureLevel,
  LuApproval,
  Participant,
  SIGNING_PROCESS,
  STEP,
  UNKOWNERROR,
} from '@/constant/NGContant';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {
  AddMore,
  AntSwitch,
  TitleAddMore,
} from '@/pages/form/process-upload/edit-pdf/other/common';
import {store} from '@/redux';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {
  deleteAnnotations,
  resetUpdateDocumentToAnnotaitons,
  setOrderApprove,
  setOrderSignature,
  storeCreateProjectActiveRole,
  storeProject,
  storeTempFile,
  updateAnnotaions,
  updateDocumentToAnnotaitons,
} from '@/redux/slides/authentication/authenticationSlide';
import {IAnnotaions} from '@/redux/slides/authentication/type';
import {
  IGetDocument,
  useAddProjectMutation,
  useLazyViewDocumentQuery,
  useUpdateProjectStepThreeMutation,
} from '@/redux/slides/project-management/project';
import {router} from '@/router';
import {
  ErrorServer,
  HandleException,
  IResponseServer,
} from '@/utils/common/HandleException';
import {
  getFirstNameAndLastName,
  getNameByFirstIndex,
} from '@/utils/common/HandlerFirstName_LastName';
import {ArrayReverse} from '@/utils/common/Reverse';
import {$isarray} from '@/utils/request/common/type';
import mentionSVG from '@assets/svg/mention.svg';
import paraphSVG from '@assets/svg/paraph.svg';
import {PersonSVG} from '@assets/svg/person/person';
import {SettingSVG} from '@assets/svg/setting/setting';
import signatureSVG from '@assets/svg/signature.svg';
import {TextSVG} from '@assets/svg/text/text';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import {
  Backdrop,
  Box,
  Button,
  CircularProgress,
  Divider,
  IconButton,
  List,
  ListItemButton,
  Stack,
} from '@mui/material';
import {IPayloadParameters} from '@pages/end-user/sidebar/models/form/create-model/CreateModel';
import {ISubmitIndex} from '@pages/end-user/sidebar/models/form/create-project-by-template/CreateProject';
import AddParticipants from '@pages/end-user/sidebar/models/form/create-project-by-template/preparation/AddParticipant';
import ProjectPreparationChamp from '@pages/end-user/sidebar/models/form/create-project-by-template/preparation/p-champs/ProjectPreparationChamp';
import ProjectPrepartionSettings from '@pages/end-user/sidebar/models/form/create-project-by-template/preparation/p-settings/ProjectPrepartionSettings';
import {styles} from '@pages/form/process-upload/edit-pdf/other/css.style';
import WebViewer, {WebViewerInstance} from '@pdftron/webviewer';
import {t} from 'i18next';
import {enqueueSnackbar} from 'notistack';
import type {PDFDocumentProxy} from 'pdfjs-dist';
import React from 'react';
import {
  Control,
  FieldErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';
import {Document, Page} from 'react-pdf/dist/esm/entry.vite';
import env from '../../../../../../../env.config';

type IHoldFile = {
  fileUrl: string;
  name: string;
  active: boolean;
};

type IProjectPreparation = {
  control: Control<IPayloadParameters, any>;
  errors: FieldErrors<IPayloadParameters>;
  setValue: UseFormSetValue<IPayloadParameters>;
  getValues: UseFormGetValues<IPayloadParameters>;
  setOptionReminder: React.Dispatch<React.SetStateAction<boolean>>;
  setSubmitIndex: React.Dispatch<React.SetStateAction<ISubmitIndex>>;
  setActiveStep: (value: ((prevState: number) => number) | number) => void;
  submitIndex: ISubmitIndex;
};

const ProjectPreparation = (props: IProjectPreparation) => {
  const {
    errors,
    setValue,
    getValues,
    control,
    setOptionReminder,
    setActiveStep: setStepActive,
    submitIndex,
    setSubmitIndex,
  } = props;
  const {
    storeModel,
    signatories: dSignatories,
    approvals: dApprovals,
    recipients: dRecipients,
    viewers: dViewers,
    tempFiles,
    project,
  } = useAppSelector(state => state.authentication);

  const view = React.useRef<HTMLElement | null>(null);
  const [instance, setInstance] = React.useState<WebViewerInstance | null>(
    null,
  );
  const [activeStep, setActiveStep] = React.useState(0);
  const [annotationsReady, setAnnotationReady] = React.useState(false);
  const dispatch = useAppDispatch();
  const [trigger, setTrigger] = React.useState(false);
  const [isReady, setIsReady] = React.useState(false);
  const [storeFiles, setFiles] = React.useState<string[]>([]);
  const [holdFile, setHoldFile] = React.useState<Array<IHoldFile>>([]);
  const {recipient, approvals, signatories, id, viewer} = storeModel!;
  const [display, setDisplay] = React.useState({
    approval: true,
    signatory: true,
    recipient: true,
    viewer: true,
  });

  const [currentPage, setCurrentPage] = React.useState<number>(1);
  const [numPages, setNumPages] = React.useState<
    {index: number; pages: number}[]
  >(
    Array.from({length: storeFiles?.length ? storeFiles.length : 1}, () => ({
      index: 0,
      pages: 0,
    })),
  );

  const [addProject, {isLoading: addProjectLoading}] = useAddProjectMutation();
  const [viewDocument, {isLoading: viewDocumentLoading}] =
    useLazyViewDocumentQuery();
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

  const handleAddProject = async (fileUploads: File[]): Promise<void> => {
    const formData = new FormData();
    fileUploads.forEach(file => {
      formData.append('files', file);
    });
    formData.append('name', 'Signature');
    formData.append('step', '1');
    formData.append('status', '1');
    formData.append('templateId', id);

    try {
      const getViewFile = async (doc: IGetDocument) => {
        const resFiles = await viewDocument({docId: doc.fileName}).unwrap();
        setHoldFile(prev => [
          ...prev,
          {
            active: true,
            fileUrl: `data:application/pdf;base64,${resFiles}`,
            name: doc.originalFileName,
          },
        ]);
        dispatch(
          storeTempFile({
            name: doc.fileName,
            documentId: doc.id,
            file: `data:application/pdf;base64,${resFiles}`,
          }),
        );
      };
      const res = await addProject(formData).unwrap();
      res.documents.forEach(doc => {
        getViewFile(doc);
      });

      store.dispatch(
        storeProject({
          project: {
            id: res.id.toString(),
            name: res.name,
            orderSign: res.orderSign,
            orderApprove: res.orderApprove,
            step: res.step,
          },
        }),
      );
      router.navigate(
        `${Route.MODEL}/project/${res.id}/template/${res.templateId}`,
      );
    } catch (error) {
      /** */
      enqueueSnackbar(HandleException((error as any).status) ?? UNKOWNERROR, {
        variant: 'errorSnackbar',
      });
    }
  };

  const handleUpload = async (
    files: File[],
    // event: DropEvent,
  ): Promise<void> => {
    await handleAddProject(files);
  };

  const handleUploadError =
    (): // fileRejecttion: FileRejection[], // event: DropEvent,
    void => {
      /** */
    };

  const handleTrigger = (role: Participant, index: number) => {
    setTrigger(true);
    store.dispatch(
      storeCreateProjectActiveRole({
        role,
        id: index,
      }),
    );
  };

  React.useEffect(() => {
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
      view.current!,
    ).then(async instance => {
      const {annotationManager, documentViewer} = instance.Core;

      documentViewer.addEventListener('pageComplete', () => {
        setAnnotationReady(true);
        annotationManager.enableRedaction();
      });
      annotationManager.addEventListener(
        'annotationChanged',
        (annotations: any[], action) => {
          if (action === 'add') {
            const annot = annotations[0];
            const id = annot['$i']['trn-associated-number'] as string | number;
            dispatch(updateAnnotaions({annotationId: '' + id}));
          } else if (action === 'modify') {
            return;
          } else if (action === 'delete') {
            annotations.forEach((annot: any) => {
              const id = annot['$i']['trn-associated-number'] as
                | string
                | number;

              const {Subject} = annot;
              dispatch(deleteAnnotations({annotationId: id, subject: Subject}));
            });
          }
        },
      );
      setInstance(instance);
    });
  }, []);

  /* upload document details (sign/sign-invisible) */
  React.useEffect(() => {
    const handleUploadStepThree = async () => {
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
            let signatoryId = 0;
            dSignatories.forEach(item => {
              if (
                item.sortOrder === parseInt(subject.split('-')[1], 10) ||
                item.id === parseInt(subject.split('-')[1], 10)
              ) {
                signatoryId = Number(item.id);
              }
            });

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
          return setStepActive(STEP.STEP2);
        }
        try {
          const res = await updateProjectStepThree({
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
          }).unwrap();
          dispatch(resetUpdateDocumentToAnnotaitons());
          if (res) {
            res.documents.forEach((doc: any) => {
              doc.documentDetails.forEach((detail: any) => {
                dispatch(updateDocumentToAnnotaitons({documentDetail: detail}));
              });
            });
          }

          setStepActive(STEP.STEP2);
        } catch (e) {
          enqueueSnackbar(ErrorServer(e as IResponseServer) ?? UNKOWNERROR, {
            variant: 'errorSnackbar',
          });
        }
      }
    };

    if (submitIndex.documentDetail) {
      handleUploadStepThree().then(r => r);
    }

    return () => setSubmitIndex(prev => ({...prev, documentDetail: false}));
  }, [submitIndex.documentDetail]);

  React.useEffect(() => {
    if (instance) {
      const files = tempFiles.map(({documentId, ...rest}) => {
        return {...rest};
      }) as IHoldFile[];
      setHoldFile(files);
    }
  }, [instance]);

  React.useMemo(() => {
    if (annotationsReady) {
      const annotation = ArrayReverse<IAnnotaions>(
        store.getState().authentication.annotations,
      );
      return annotation.forEach((ann, index: number) => {
        if (ann.documentDetails?.length! > 0) {
          return ann.documentDetails?.forEach(docDetail => {
            if (docDetail.type?.split('-')[0].toLowerCase() === 'signatory') {
              setCanvasSignatory(
                ann.signatoryName,
                dSignatories.findIndex(item => item.id === ann.signatoryId),
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
                dSignatories.findIndex(item => item.id === ann.signatoryId),
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
                dSignatories.findIndex(item => item.id === ann.signatoryId),
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
  }, [annotationsReady]);

  return (
    <Stack direction="row" alignItems="center">
      <canvas
        id="myCanvas"
        width="410px"
        height="120px"
        style={{display: 'none', border: '#333 10px solid'}}></canvas>
      <canvas
        id="myCanvas-2"
        width="410px"
        height="120px"
        style={{display: 'none', border: '#333 10px solid'}}></canvas>
      <canvas
        id="myCanvas-3"
        width="410px"
        height="120px"
        style={{display: 'none', border: '#333 10px solid'}}></canvas>
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
      {/* Document sidebar */}
      <Stack
        width="240px"
        height={`calc(100vh - 60px)`}
        sx={{
          borderRight: `2px solid #E9E9E9`,
        }}>
        <Stack
          direction="row"
          p={2}
          sx={{
            borderBottom: `1.5px solid #E9E9E9`,
          }}
          gap="12px"
          alignItems="center"
          width="100%">
          <NGDoc
            sx={{
              mt: '-2px',
              fontSize: '20px',
            }}
          />
          <NGText
            myStyle={{
              fontSize: '18px',
              fontWeight: 600,
              width: '144px',
            }}
            text="Documents"
          />
          <NGCirclePlus
            sx={{
              color: 'Primary.main',
              fontSize: '20px',
            }}
          />
        </Stack>
        {holdFile.length > 0 ? (
          <ViewReactPDF
            setReady={setIsReady}
            setCurrentPage={setCurrentPage}
            currentPage={currentPage}
            files={holdFile}
            numPages={numPages}
            setNumPages={setNumPages}
            instance={instance}
          />
        ) : (
          <NoDocument />
        )}
      </Stack>

      {/* Default center page */}
      <Box
        ref={view}
        sx={{
          display: isReady ? 'flex' : 'none',
          height: `calc(100vh - 60px)`,
          width: `calc(100% - 626px)`,
        }}></Box>
      {!isReady && (
        <Stack
          width={`calc(100% - 566px)`}
          height={`calc(100vh - 60px)`}
          gap="10px"
          sx={{
            alignItems: 'center',
            justifyContent: 'center',
          }}>
          <NGAddProject
            sx={{
              width: '269px',
              height: '269px',
            }}
          />
          <NGDropzoneComponent
            multiple={false}
            contentSx={{
              gap: '10px',
            }}
            accept={{
              'application/pdf': ['.pdf'],
            }}
            sx={[{height: '164px', width: '436px', borderStyle: 'none'}]}
            titleSx={{
              fontSize: '16px',
            }}
            title={
              t(
                Localization(
                  'upload-document',
                  'drag-and-drop-your-files-here',
                ),
              )!
            }
            field={t(Localization('upload-document', 'adding-files'))!}
            supportFormatText={t(
              Localization('upload-document', 'documents-support'),
              {size: import.meta.env.VITE_MAX_FILE_SIZE_UPLOAD / 1000000},
            )}
            alertMessage={<></>}
            handleUpload={handleUpload}
            handleUploadError={handleUploadError}
          />
        </Stack>
      )}

      {/* sidebar */}
      <Stack direction="row" height={`calc(100vh - 60px)`}>
        {/* participants sidebar */}
        {activeStep === 0 && (
          <>
            <Stack
              width="326px"
              sx={{
                borderLeft: `2px solid #E9E9E9`,
                position: 'absolute',
                height: 'calc(100vh - 60px)',
                right: '60px',
              }}>
              <Stack
                sx={{
                  borderBottom: `1.5px solid #E9E9E9`,
                }}>
                <TitleAddMore
                  name={t(Localization('table', 'participants'))}
                  icon={<NGParticipant sx={{color: 'Primary.main'}} />}
                  //   onClick={handleAddMore}
                />
              </Stack>

              {/* Viewers */}
              {viewer > 0 && (
                <>
                  <AddMore
                    name={t(Localization('upload-signatories', 'viewers'))}
                    sxProps={{
                      px: '16px',
                      bgcolor: '#F0F1F380',
                    }}
                    icon={
                      display.viewer ? (
                        <KeyboardArrowDownIcon />
                      ) : (
                        <KeyboardArrowUpIcon />
                      )
                    }
                    onClick={() =>
                      setDisplay({...display, viewer: !display.viewer})
                    }
                    num={viewer}
                  />
                  <Divider sx={{borderBottomWidth: 1}} />

                  <Stack
                    sx={{
                      p: '12px 16px',
                      display: display.approval ? 'flex' : 'none',
                    }}
                    gap="14px">
                    <Stack gap="8px">
                      {Number(project.step) > 1 &&
                        dViewers.map(item => (
                          <AddInParticipants
                            key={item.id}
                            onClick={() =>
                              handleTrigger(Participant.Viewer, Number(item.id))
                            }
                            firstName={item.firstName!}
                            lastName={item.lastName!}
                          />
                        ))}
                      {Number(project.step) <= 1 &&
                        Array.from({length: approvals}, (_, index) => {
                          const data = dViewers.find(item => item.id === index);
                          return data ? (
                            <AddInParticipants
                              key={index}
                              onClick={() =>
                                handleTrigger(Participant.Viewer, index)
                              }
                              firstName={data.firstName!}
                              lastName={data.lastName!}
                            />
                          ) : (
                            <AddParticipant
                              key={index}
                              index={index}
                              type="viewer"
                              onClick={() =>
                                handleTrigger(Participant.Viewer, index)
                              }
                            />
                          );
                        })}
                    </Stack>
                  </Stack>
                </>
              )}

              {/* Approvals */}
              {approvals > 0 && (
                <>
                  <AddMore
                    name={t(Localization('upload-signatories', 'approvals'))}
                    sxProps={{
                      px: '16px',
                      bgcolor: '#F0F1F380',
                    }}
                    icon={
                      display.approval ? (
                        <KeyboardArrowDownIcon />
                      ) : (
                        <KeyboardArrowUpIcon />
                      )
                    }
                    onClick={() =>
                      setDisplay({...display, approval: !display.approval})
                    }
                    num={approvals}
                  />
                  <Divider sx={{borderBottomWidth: 1}} />

                  <Stack
                    sx={{
                      p: '12px 16px',
                      display: display.approval ? 'flex' : 'none',
                    }}
                    gap="14px">
                    <Stack gap="10px">
                      <Stack direction="row" gap="8px" alignItems="center">
                        <AntSwitch
                          overridecolor="green"
                          checked={
                            storeModel?.signProcess === SIGNING_PROCESS.COSIGN
                              ? false
                              : project.orderApprove
                          }
                          onChange={(
                            event: React.ChangeEvent<HTMLInputElement>,
                          ) => {
                            if (
                              storeModel?.signProcess === SIGNING_PROCESS.COSIGN
                            ) {
                              return;
                            }
                            return dispatch(
                              setOrderApprove({
                                orderApprove: event.target.checked,
                              }),
                            );
                          }}
                        />
                        <NGText
                          myStyle={{
                            fontSize: '12px',
                          }}
                          text={t(
                            Localization(
                              'upload-signatories',
                              'define-a-approving-order',
                            ),
                          )}
                        />
                      </Stack>
                    </Stack>

                    <Stack gap="8px">
                      {Number(project.step) > 1 &&
                        dApprovals.map(item => (
                          <AddInParticipants
                            key={item.id}
                            onClick={() =>
                              handleTrigger(
                                Participant.Approval,
                                Number(item.id),
                              )
                            }
                            firstName={item.firstName!}
                            lastName={item.lastName!}
                          />
                        ))}
                      {Number(project.step) <= 1 &&
                        Array.from({length: approvals}, (_, index) => {
                          const data = dApprovals.find(
                            item => item.id === index,
                          );
                          return data ? (
                            <AddInParticipants
                              key={index}
                              onClick={() =>
                                handleTrigger(Participant.Approval, index)
                              }
                              firstName={data.firstName!}
                              lastName={data.lastName!}
                            />
                          ) : (
                            <AddParticipant
                              key={index}
                              index={index}
                              type="approval"
                              onClick={() =>
                                handleTrigger(Participant.Approval, index)
                              }
                            />
                          );
                        })}
                    </Stack>
                  </Stack>
                </>
              )}

              {/* Signatories */}
              {signatories > 0 && (
                <>
                  <Divider sx={{borderBottomWidth: 1}} />
                  <AddMore
                    sxProps={{
                      px: '16px',
                      bgcolor: '#F0F1F380',
                    }}
                    name={t(Localization('upload-signatories', 'signatories'))}
                    icon={
                      display.signatory ? (
                        <KeyboardArrowDownIcon />
                      ) : (
                        <KeyboardArrowUpIcon />
                      )
                    }
                    onClick={() =>
                      setDisplay({...display, signatory: !display.signatory})
                    }
                    num={signatories}
                  />
                  <Divider sx={{borderBottomWidth: 1}} />
                  <Stack
                    sx={{
                      p: '12px 16px',
                      display: display.signatory ? 'flex' : 'none',
                    }}
                    gap="14px">
                    <Stack gap="10px">
                      <Stack direction="row" gap="8px" alignItems="center">
                        <AntSwitch
                          overridecolor="green"
                          checked={
                            storeModel?.signProcess === SIGNING_PROCESS.COSIGN
                              ? false
                              : project.orderSign
                          }
                          onChange={(
                            event: React.ChangeEvent<HTMLInputElement>,
                          ) => {
                            if (
                              storeModel?.signProcess === SIGNING_PROCESS.COSIGN
                            ) {
                              return;
                            }
                            return dispatch(
                              setOrderSignature({
                                orderSign: event.target.checked,
                              }),
                            );
                          }}
                        />
                        <NGText
                          myStyle={{
                            fontSize: '12px',
                          }}
                          text={t(
                            Localization(
                              'upload-signatories',
                              'define-a-signing-order',
                            ),
                          )}
                        />
                      </Stack>
                    </Stack>

                    <Stack gap="8px">
                      {Number(project.step) > 1 &&
                        dSignatories.map(item => (
                          <AddInParticipants
                            key={item.id}
                            onClick={() =>
                              handleTrigger(
                                Participant.Signatory,
                                Number(item.id),
                              )
                            }
                            firstName={item.firstName!}
                            lastName={item.lastName!}
                          />
                        ))}
                      {Number(project.step) <= 1 &&
                        Array.from({length: signatories}, (_, index) => {
                          const data = dSignatories.find(
                            item => item.id === index,
                          );

                          return data ? (
                            <AddInParticipants
                              key={index}
                              onClick={() =>
                                handleTrigger(Participant.Signatory, index)
                              }
                              firstName={data.firstName!}
                              lastName={data.lastName!}
                            />
                          ) : (
                            <AddParticipant
                              key={index}
                              index={index}
                              type="signatory"
                              onClick={() =>
                                handleTrigger(Participant.Signatory, index)
                              }
                            />
                          );
                        })}
                    </Stack>
                  </Stack>
                </>
              )}

              {/* Recipients */}
              {recipient > 0 && (
                <>
                  <Divider sx={{borderBottomWidth: 1}} />
                  <AddMore
                    sxProps={{
                      px: '16px',
                      bgcolor: '#F0F1F380',
                    }}
                    name={t(Localization('upload-signatories', 'receipts'))}
                    icon={
                      display.recipient ? (
                        <KeyboardArrowDownIcon />
                      ) : (
                        <KeyboardArrowUpIcon />
                      )
                    }
                    onClick={() =>
                      setDisplay({...display, recipient: !display.recipient})
                    }
                    num={recipient}
                  />
                  <Divider sx={{borderBottomWidth: 1}} />
                  <Stack
                    sx={{
                      p: '12px 16px',
                      display: display.recipient ? 'flex' : 'none',
                    }}
                    gap="14px">
                    <Stack gap="8px">
                      {Number(project.step) > 1 &&
                        dRecipients.map(item => (
                          <AddInParticipants
                            key={item.id}
                            onClick={() =>
                              handleTrigger(
                                Participant.Receipt,
                                Number(item.id),
                              )
                            }
                            firstName={item.firstName!}
                            lastName={item.lastName!}
                          />
                        ))}
                      {Number(project.step) <= 1 &&
                        Array.from({length: recipient}, (_, index) => {
                          const data = dRecipients.find(
                            item => item.id === index,
                          );
                          return data ? (
                            <AddInParticipants
                              key={index}
                              onClick={() =>
                                handleTrigger(Participant.Receipt, index)
                              }
                              firstName={data.firstName!}
                              lastName={data.lastName!}
                            />
                          ) : (
                            <AddParticipant
                              key={index}
                              index={index}
                              type="recipient"
                              onClick={() =>
                                handleTrigger(Participant.Receipt, index)
                              }
                            />
                          );
                        })}
                    </Stack>
                  </Stack>
                </>
              )}
            </Stack>
          </>
        )}

        {/* participants sign sidebar */}
        {activeStep === 1 && (
          <>
            <Stack width="374px" height={`calc(100vh - 60px)`}>
              <ProjectPreparationChamp
                instance={instance}
                setActiveStep={setActiveStep}
              />
            </Stack>
          </>
        )}

        {/* settings sidebar */}
        {activeStep === 2 && (
          <>
            {storeModel?.templateMessage ? (
              <ProjectPrepartionSettings
                control={control}
                errors={errors}
                setValue={setValue}
                getValues={getValues}
                setOptionReminder={setOptionReminder}
              />
            ) : (
              <></>
            )}
          </>
        )}

        <Stack width="60px" alignItems={'center'}>
          <Stack
            spacing={2}
            sx={{
              py: '20px',
              position: 'absolute',
              right: 0,
              height: `calc(100vh - 60px)`,
              borderLeft: '1px solid #E9E9E9',
            }}>
            <IconButton
              onClick={() => setActiveStep(0)}
              sx={{
                borderLeft: activeStep === 0 ? `3px solid` : '',
                borderColor: 'Primary.main',
                borderRadius: 0,
                p: 2,
              }}>
              <PersonSVG
                sx={{color: activeStep === 0 ? 'Primary.main' : '#000000'}}
              />
            </IconButton>
            <IconButton
              onClick={() => setActiveStep(1)}
              sx={{
                borderLeft: activeStep === 1 ? `3px solid` : '',
                borderColor: 'Primary.main',
                borderRadius: 0,
                p: 2,
              }}>
              <TextSVG
                sx={{color: activeStep === 1 ? 'Primary.main' : '#000000'}}
              />
            </IconButton>

            <IconButton
              onClick={() => setActiveStep(2)}
              sx={{
                borderLeft: activeStep === 2 ? '3px solid ' : '',
                borderColor: 'Primary.main',
                borderRadius: 0,
                p: 2,
              }}>
              <SettingSVG
                sx={{color: activeStep === 2 ? 'Primary.main' : '#000000'}}
              />
            </IconButton>
          </Stack>
        </Stack>
      </Stack>

      <Backdrop
        sx={{color: '#fff', zIndex: theme => theme.zIndex.modal + 1}}
        open={addProjectLoading || viewDocumentLoading}>
        <CircularProgress color="inherit" />
      </Backdrop>

      <AddParticipants trigger={trigger} setTrigger={setTrigger} />
    </Stack>
  );
};

export default ProjectPreparation;

const AddParticipant = ({
  index,
  type,
  onClick,
}: {
  index: number;
  type: 'signatory' | 'approval' | 'recipient' | 'viewer';
  onClick: () => void;
}) => {
  return (
    <Stack direction="row" alignItems="center" justifyContent="space-between">
      <Stack direction="row" gap="7px" alignItems="center">
        <Stack
          sx={{
            width: '32px',
            height: '32px',
            bgcolor: '#F0F0F0',
            borderRadius: '50%',
            border: '1px solid #BABABA',
            borderStyle: 'dashed',
          }}></Stack>
        <NGText
          myStyle={{
            fontSize: '12px',
            fontStyle: 'italic',
            fontWeight: 500,
            color: '#676767',
          }}
          text={t(Localization('table', type)) + ' ' + (index + 1)}
        />
      </Stack>

      <Stack>
        <Button
          onClick={onClick}
          variant={'text'}
          sx={{fontWeight: 'bold', textTransform: 'none'}}
          endIcon={
            <AddOutlinedIcon
              sx={{color: 'Primary.main', width: '15px', mt: '-2px'}}
            />
          }>
          <NGText
            text={t(Localization('create-project-by-template', 'assign'))}
            myStyle={{
              fontWeight: 600,
              fontSize: 11,
            }}
          />
        </Button>
      </Stack>
    </Stack>
  );
};

const AddInParticipants = ({
  firstName,
  lastName,
  onClick,
}: {
  firstName: string;
  lastName: string;
  onClick: () => void;
}) => {
  return (
    <Stack direction="row" alignItems="center" justifyContent="space-between">
      <Stack direction="row" gap="7px" alignItems="center">
        <NGGroupAvatar
          character={[getNameByFirstIndex(`${firstName} ${lastName}`)]}
        />
        <NGText
          myStyle={{
            fontSize: '12px',
            fontStyle: 'italic',
            fontWeight: 500,
            color: '#676767',
          }}
          text={getFirstNameAndLastName(`${firstName} ${lastName}`)}
        />
      </Stack>

      <Stack direction="row" gap="12px">
        <IconButton
          onClick={onClick}
          sx={{
            p: 0,
          }}>
          <NGPen
            sx={{
              width: '14px',
            }}
          />
        </IconButton>
        <IconButton
          sx={{
            p: 0,
          }}>
          <NGTrash
            sx={{
              width: '14px',
            }}
          />
        </IconButton>
      </Stack>
    </Stack>
  );
};

const NoDocument = () => {
  return (
    <Stack width="100%" alignItems="center">
      <Stack
        width="160px"
        justifyContent="center"
        height={`calc(100vh - 130px)`}>
        <NGText
          myStyle={{
            textAlign: 'center',
            fontSize: '12px',
          }}
          text={t(Localization('models-corporate', 'no-document'))}
        />
      </Stack>
    </Stack>
  );
};

type IViewReactPDF = {
  instance?: WebViewerInstance | null;
  currentPage: number;
  files: Array<IHoldFile>;
  setReady: React.Dispatch<React.SetStateAction<boolean>>;
  numPages: {
    index: number;
    pages: number;
  }[];
  setNumPages: React.Dispatch<
    React.SetStateAction<
      {
        index: number;
        pages: number;
      }[]
    >
  >;
  setCurrentPage: React.Dispatch<React.SetStateAction<number>>;
};

const ViewReactPDF = ({
  numPages,
  files,
  instance,
  currentPage,
  setNumPages,
  setReady,
  setCurrentPage,
}: IViewReactPDF) => {
  const {documentViewer, annotationManager} = instance!.Core;
  const heightPage = (index: number) => {
    return numPages[index].pages > 1
      ? `calc(100vh -  ${61 * (files.length + 1)}px)`
      : 'auto';
  };

  const onDocumentLoadSuccess = (
    {numPages: nextNumPages}: PDFDocumentProxy,
    index: number,
  ) => {
    const temp = [...numPages];
    temp[index].index = index;
    temp[index].pages = nextNumPages;
    setNumPages(temp);
    setReady(true);
  };

  React.useMemo(() => {
    instance?.UI.loadDocument(files[0]?.fileUrl, {extension: 'pdf'});
    return documentViewer.addEventListener('documentLoaded', async () => {
      const xfdfString = await annotationManager.exportAnnotations();
      const data = await documentViewer.getDocument().getFileData({xfdfString});
      const arr = new Uint8Array(data);
      const blob = new Blob([arr], {type: 'application/pdf'});
      URL.createObjectURL(blob);
      documentViewer.addEventListener(
        'pageNumberUpdated',
        async (e: number) => {
          setCurrentPage(e);
        },
      );
    });
  }, []);

  return (
    <List
      sx={{
        width: '100%',
        py: 0,
        ...styles.scrollbarHidden,
      }}>
      {$isarray(files) &&
        files?.map((file, index: number) => (
          <Stack
            key={`doc_${file.fileUrl}`}
            sx={{
              width: '100%',
              height: file.active ? heightPage(index) : '70px',
            }}>
            <Stack
              sx={{
                px: '1rem',
                justifyContent: 'center',
              }}>
              <IconButton
                // onClick={() => {
                //   if (currentView !== index) {
                //     instance?.UI.loadDocument(files[index].fileUrl, {
                //       documentId: `documentId_${index}`,
                //     });
                //     setCurrentView(index);
                //   }
                //   dispatch(updateTempFile({index}));
                // }}
                disableFocusRipple
                disableTouchRipple
                disableRipple
                sx={{
                  borderRadius: 0,
                  p: 0,
                  alignItems: 'center',
                }}>
                <Stack sx={{width: '100%'}}>
                  <Stack
                    direction={'row'}
                    alignItems={'center'}
                    justifyContent={'space-between'}
                    spacing={1}
                    sx={{width: '100%', height: '50px'}}>
                    <NGArrowBackFill
                      sx={{
                        fontSize: '16px',
                      }}
                    />
                    <Stack
                      direction={'row'}
                      sx={{alignItems: 'center'}}
                      spacing={1}>
                      <NGText
                        text={
                          file.name.length - 4 > 13
                            ? file.name.slice(0, 13) + '...' + '.pdf'
                            : file.name
                        }
                        myStyle={{fontSize: 14, fontWeight: 600}}
                      />
                    </Stack>
                    <Stack direction={'row'}>
                      <NGThreeDotsVert
                        sx={{
                          fontSize: '16px',
                        }}
                      />
                    </Stack>
                  </Stack>

                  {/* <Stack
                    direction={'row'}
                    sx={{alignItems: 'center', mb: '10px'}}
                    spacing={1}>
                    {numPages[index] ? (
                      <NGText
                        text={numPages[index].pages + ' pages'}
                        myStyle={{fontSize: 16}}
                      />
                    ) : (
                      <NGText text={'loading ...'} myStyle={{fontSize: 12}} />
                    )}
                  </Stack> */}
                </Stack>
              </IconButton>
            </Stack>
            <Divider sx={{borderBottomWidth: 1}} />

            <Stack
              sx={{
                display: file.active ? 'flex' : 'none',
                width: 'auto',
                height: 'auto',
                overflow: 'hidden',
                alignItems: 'center',
              }}>
              <List
                disablePadding
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  // ...styles.scrollbarHidden,
                  width: '100%',
                  // height: '300',
                  alignItems: 'center',
                  overflowY: 'auto',
                }}>
                <Document
                  file={file.fileUrl}
                  key={file.fileUrl}
                  loading={<Stack sx={{px: '1rem'}}>loading...</Stack>}
                  onLoadSuccess={(numPages: PDFDocumentProxy) =>
                    onDocumentLoadSuccess(numPages, index)
                  }>
                  {Array.from(new Array(numPages[index].pages), (el, index) => (
                    <Stack
                      key={`page_${index + 1}`}
                      justifyContent={'flex-start'}
                      sx={{width: 'auto'}}>
                      <Stack
                        direction={'row'}
                        sx={{width: 'auto'}}
                        justifyContent={'space-between'}
                        spacing={4}>
                        <NGText text={index + 1} myStyle={{width: '10px'}} />
                        <ListItemButton
                          disableRipple
                          disableTouchRipple
                          sx={{
                            '&:hover': {
                              bgcolor: 'transparent',
                              width: 'auto',
                            },
                            justifyContent: 'center',
                          }}
                          disableGutters
                          autoFocus={index + 1 === currentPage}>
                          <Stack
                            width="auto"
                            key={`page_${index + 1}`}
                            sx={{
                              boxShadow: 1,
                              p: '3px',
                              borderWidth: currentPage === index + 1 ? 1 : 0,
                              borderRadius: '6px',
                              borderColor:
                                currentPage === index + 1
                                  ? 'Primary.main'
                                  : undefined,
                              borderStyle:
                                currentPage === index + 1 ? 'solid' : undefined,
                            }}>
                            <Page
                              scale={0.17}
                              pageNumber={index + 1}
                              key={file.fileUrl}
                              onClick={e => {
                                e.preventDefault();
                                return documentViewer.setCurrentPage(
                                  index + 1,
                                  false,
                                );
                              }}
                            />
                          </Stack>
                        </ListItemButton>
                      </Stack>
                    </Stack>
                  ))}
                </Document>
              </List>
            </Stack>
            <Divider sx={{borderBottomWidth: 2}} />
          </Stack>
        ))}
    </List>
  );
};
