import {
  KeySignatureLevel,
  Participant,
  tokenRoleName,
} from '@/constant/NGContant';
import {DTable} from '@/pages/end-user/sidebar/models/sidenav/sections/TableSection';
import {IRecipient} from '@/pages/form/process-upload/type';
import {getFirstNameAndLastName} from '@/utils/common/HandlerFirstName_LastName';
import {createSlice} from '@reduxjs/toolkit';
import {TemplateInterface} from '../profile/template/templateSlide';
import {IDocumentDetails} from '../project-management/project';
import {
  IAction,
  IAnnotaions,
  IAuthentication,
  IProcessAdvancedSignature,
  IProjectDetailActions,
  ISelectEnvoiById,
  ISignatureLevels,
  ITempCreateModel,
  KeyAdvanceSignatureProcessDocumentType,
} from './type';

const initialState: IAuthentication = {
  user: {
    username: '',
  },
  files: [],
  tempFiles: [],
  signatories: [],
  approvals: [],
  viewers: [],
  recipients: [],
  role: null,
  isLoading: true,
  isSignout: false,
  userToken: null,
  sid: null,
  C_UUID: null,
  USER_COMPANY: null,
  invitation: {
    inviter: null ?? 'Cristina Games',
    receiver: null ?? 'You',
    timeDelivery: null ?? Date.now(),
    documents: null ?? 2,
    projectName: null ?? 'Crédit immobilier ~ Paris Xvit',
  },
  annotations: [],
  project: {
    id: null,
    name: null,
    orderSign: false,
    orderApprove: false,
    template: null,
    step: null,
  },
  selectEnvoiData: null,
  activeActorEnvoi: null,
  projectDetailActions: {
    'modified-date': false,
  },
  createModel: null,
  storeModel: null,
  createProjectActiveRole: null,
  signatureLevels: {
    signatureLevel: KeySignatureLevel.NONE,
    channelReminder: '',
    companyUuid: '',
    documentTerms: '',
    identityTerms: '',
    personalTerms: '',
    fileType: [''],
  },
  processAdvanceSignature: {
    documentBack: null,
    documentCountry: '',
    documentFront: null,
    documentRotation: 0,
    documentType: KeyAdvanceSignatureProcessDocumentType.NONE,
  },
  gbc: {
    title: '',
    description: 0,
  },
};

export const authSlice = createSlice({
  name: 'authentication',
  initialState,
  reducers: {
    storeCreateProjectActiveRole: (
      store,
      {
        payload,
      }: {
        payload: {
          role: Participant;
          id: string | number;
        };
      },
    ) => {
      store.createProjectActiveRole = payload;
    },

    storeModel: (state, {payload}: {payload: DTable | null}) => {
      state.storeModel = payload;
    },
    storeCreateModel: (
      state,
      {payload}: {payload: ITempCreateModel | null},
    ) => {
      const data = payload;
      state.createModel = data;
    },
    projectDetailAction: (
      state,
      {payload}: {payload: {[k in IProjectDetailActions]: boolean}},
    ) => {
      state.projectDetailActions = payload;
    },
    /*** Reset redux all steps*/
    resetToInitial: state => {
      const {
        user,
        userToken,
        role,
        invitation,
        sid,
        C_UUID,
        USER_COMPANY,
        storeModel,
        ...rest
      } = initialState;
      return {
        ...state,
        ...rest,
        project: {
          ...state.project,
          step: '',
          template: null,
        },
      };
    },
    /*** Set active signatory */
    setActiveActorRole: (
      state,
      action: {
        payload: {
          id: number;
          signatoryName: string;
          role:
            | Participant.Approval
            | Participant.Signatory
            | Participant.Receipt
            | Participant.Viewer;
          email?: string;
        };
      },
    ) => {
      const {id, role, signatoryName, email} = action.payload;
      return {
        ...state,
        activeActorEnvoi: {
          id,
          role,
          signatoryName,
          email,
        },
      };
    },
    /*** Update envoi data */
    updateEnvoiByRole: (
      state,
      action: {
        payload: {
          role:
            | Participant.Approval
            | Participant.Signatory
            | Participant.Receipt
            | Participant.Viewer;
          data: {
            id: number | null;
            name: string;
            title: string;
            description: string;
            expired: Date;
          };
        };
      },
    ) => {
      const {data, role} = action.payload;
      state.selectEnvoiData = {...state.selectEnvoiData, [role]: data};
    },
    /*** Store select envoi data */
    storeEnvoiByRole: (
      state,
      action: {
        payload: {
          projectDetails: Array<{
            expiresDate: number;
            id: number;
            messageInvitation: string;
            titleInvitation: string;
            type:
              | Participant.Approval
              | Participant.Receipt
              | Participant.Signatory;
          }>;
          signatories: Pick<IRecipient, 'role'>[];
        };
      },
    ) => {
      const {signatories, projectDetails} = action.payload;
      const data: ISelectEnvoiById = {};
      signatories.forEach(({role}) => {
        const detail = projectDetails.find(p => p.type === role);
        if (!detail) {
          return (data[role!] = {
            id: null,
            name: '',
            description: '',
            title: '',
            expired: new Date(),
          });
        }
        return (data[role!] = {
          id: detail.id,
          name: '',
          description: detail.messageInvitation,
          title: detail.titleInvitation,
          expired: new Date(detail.expiresDate),
        });
      });

      return {
        ...state,
        selectEnvoiData: data,
      };
    },

    /*** Store signature template */
    storeSignatureTemplate: (
      state,
      action: {payload: {template: TemplateInterface}},
    ) => {
      const {template} = action.payload;
      return {
        ...state,
        project: {
          ...state.project,
          template: {
            ...template,
            participants: [
              ...(Array.from(
                {length: template.approval},
                () => 'approval',
              ) as Participant[]),
              ...(Array.from(
                {length: template.signature},
                () => 'signatory',
              ) as Participant[]),
            ],
          },
        },
      };
    },
    /*** Store projectId **/
    storeProject: (
      state,
      action: {
        payload: {
          project: {
            id: string;
            name: string;
            orderSign: boolean;
            orderApprove: boolean;
            step: string;
          };
        };
      },
    ) => {
      const {id, name, orderSign, orderApprove, step} = action.payload.project;
      return {
        ...state,
        project: {
          ...state.project,
          orderSign,
          orderApprove,
          id,
          name,
          step,
        },
      };
    },
    /*** Sign in store token **/
    signIn: (state, action: IAction) => {
      const {token, role, username} = action.payload;
      return {
        ...state,
        userToken: token,
        role: role as string,
        user: {username},
      };
    },
    /*** Sign out clear token **/
    signOut: state => {
      localStorage.removeItem(tokenRoleName);
      return {...state, userToken: null, role: null};
    },
    /*** Refresh store token **/
    refreshToken: (state, action: IAction) => {
      const {token, role, name, sid} = action.payload.value;
      return {
        ...state,
        userToken: token,
        role: role as string,
        user: {username: name},
        sid,
      };
    },
    /***
     * it's used to store C UUID corporateID, CompanyInfo
     * */
    storeUserCompanyInfo: (state, action: IAction) => {
      const {C_UUID, USER_COMPANY} = action.payload.value;
      return {
        ...state,
        C_UUID,
        USER_COMPANY,
      };
    },
    /*** Store signatories **/
    storeSignatories: (state, action: IAction) => {
      const {data} = action.payload;
      state.signatories = data;
    },
    /*** Store approvals */
    storeApprovals: (state, action: IAction) => {
      const {data} = action.payload;

      state.approvals = data;
    },
    /*** Store viewers */
    storeViewers: (state, action: IAction) => {
      const {data} = action.payload;
      state.viewers = data;
    },
    /*** Store Recipients */
    storeRecipient: (state, action: IAction) => {
      const {data} = action.payload;
      state.recipients = data;
    },
    /*** Store files */
    storeFile: (state, action: IAction) => {
      const {file} = action.payload;
      state.files = state.files.concat(file);
    },

    /*** Store project detail for a step 4th `envoi`*/
    storeSendInvitation: (state, action: IAction) => {
      state.invitation = action.payload;
    },
    /*** Clear signatories*/
    clearSignatory: state => {
      state.signatories = [];
    },
    /*** Clear approvals*/
    clearApproval: state => {
      state.approvals = [];
    },
    /*** Clear views*/
    clearViewers: state => {
      state.viewers = [];
    },
    clearReceptient: state => {
      state.recipients = [];
    },
    /*** Store temporary files*/
    storeTempFile: (state, action: IAction) => {
      const {file, name, documentId} = action.payload;
      state.tempFiles = state.tempFiles.concat({
        active: !state.tempFiles.length,
        name,
        fileUrl: file,
        documentId,
      });
    },
    /** Clear temporary files*/
    clearTempFiles: state => {
      state.tempFiles = [];
    },
    /** Update temporary files*/
    updateTempFile: (state, action: IAction) => {
      const {index} = action.payload;
      const tempFiles: {
        active: boolean;
        fileUrl: string;
        name: string;
        documentId: string;
      }[] = [];
      state.tempFiles.forEach(file => {
        tempFiles.push({
          active: false,
          fileUrl: file.fileUrl,
          name: file.name,
          documentId: file.documentId,
        });
      });
      tempFiles[index].active = !tempFiles[index].active;

      state.tempFiles = tempFiles;
    },
    resetUpdateDocumentToAnnotaitons: state => {
      return {
        ...state,
        annotations: state.annotations.map(anno => {
          return {
            ...anno,
            documentDetails: [],
          };
        }),
      };
    },

    /** Update documentDetail to annotations*/
    updateDocumentToAnnotaitons: (
      state,
      action: {
        payload: {
          documentDetail: IDocumentDetails;
        };
      },
    ) => {
      const {documentDetail} = action.payload;

      return {
        ...state,
        annotations: state.annotations.map(anno => {
          if (anno.signatoryId === documentDetail.signatoryId) {
            let dMention = anno.dMention;
            let assoMention: string[] | number[] | null = anno.annotaionmention;
            let assoStamp: string[] | number[] | null = anno.annotationStamp;
            let assoParaph: string | number | null = anno.annotationParaph;
            let dCreateStamp = anno.dCreateStamp;
            let dParaph = anno.dParaph;
            if (
              documentDetail.type?.split('-')[0].toLowerCase() === 'approval'
            ) {
              dMention = false;
              assoMention = [documentDetail.type?.split('-')[1].toLowerCase()];
            } else if (
              documentDetail.type?.split('-')[0].toLowerCase() === 'paraph'
            ) {
              dParaph = false;
              assoParaph = documentDetail.type?.split('-')[1].toLowerCase();
            } else if (
              documentDetail.type?.split('-')[0].toLowerCase() === 'signatory'
            ) {
              dCreateStamp = false;
              assoStamp = [documentDetail.type?.split('-')[1].toLowerCase()];
            }

            return {
              ...anno,
              annotaionmention: assoMention,
              annotationStamp: assoStamp,
              annotationParaph: assoParaph,
              dMention,
              dCreateStamp,
              dParaph,
              documentDetails: anno.documentDetails?.find(
                i => i.id === documentDetail.id,
              )
                ? [...anno.documentDetails!]
                : [...anno.documentDetails!, documentDetail],
            };
          } else {
            return anno;
          }
        }),
      };
    },
    /** Store invitations*/
    storeInvitation: (state, action) => {
      const {invitation} = action.payload as Pick<
        IAuthentication,
        'invitation'
      >;
      const {inviter, timeDelivery, receiver, documents, projectName} =
        invitation;

      return {
        ...state,
        invitation: {
          inviter,
          receiver,
          timeDelivery,
          documents,
          projectName,
        },
      };
    },
    // Store annotations
    storeAnnotation: (
      state,
      action: {payload: {signatories: IRecipient[]}},
    ) => {
      const tempAnnotations = [...action.payload.signatories];
      const newAnnotations: IAnnotaions[] = [];
      tempAnnotations.forEach(data => {
        return newAnnotations.push({
          annotaionmention: [],
          annotationParaph: null,
          annotationStamp: [],
          dCreateStamp: false,
          dMention: false,
          dParaph: false,
          pan: false,
          signatoryId: data.id!,
          sortOrder: data.sortOrder,
          signatoryName: getFirstNameAndLastName(
            `${data.firstName} ${data.lastName}`,
          ),
          documentDetails: [],
        });
      });

      return {...state, annotations: [...state.annotations, ...newAnnotations]};
    },

    storeAnnotationExist: (
      state,
      action: {payload: {signatories: IRecipient[]}},
    ) => {
      const tempAnnotations = [...action.payload.signatories];
      const newAnnotations: IAnnotaions[] = [];

      state.annotations.forEach(annotate => {
        tempAnnotations.reverse().forEach(signatory => {
          if (annotate.sortOrder === signatory.sortOrder) {
            newAnnotations.push({
              ...annotate,
              signatoryId: signatory.id!,
              signatoryName: getFirstNameAndLastName(
                `${signatory.firstName} ${signatory.lastName}`,
              ),
            });
          }
        });
      });

      state.annotations = newAnnotations;
    },
    /** Reset annotation active*/
    resetAnnotationActive: (
      state,
      action: {payload: {signatoryId: number | string}},
    ) => {
      const {signatoryId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.signatoryId === signatoryId) {
            return {
              ...x,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
              pan: true,
            };
          }
          return {
            ...x,
            dCreateStamp: false,
            dMention: false,
            dParaph: false,
            pan: false,
          };
        }),
      };
    },
    /** Update annotationsActive*/
    updateAnnotaionsActive: (
      state,
      actions: {
        payload: {
          signatoryId: string | number;
          toolName: string;
          annotations: IAnnotaions[];
        };
      },
    ) => {
      const {signatoryId, toolName} = actions.payload;

      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.signatoryId === signatoryId) {
            if (toolName === 'Signature') {
              return {
                ...x,
                dCreateStamp: true,
                dMention: false,
                dParaph: false,
                pan: false,
              };
            } else if (toolName === 'Mention') {
              return {
                ...x,
                dCreateStamp: false,
                dMention: true,
                dParaph: false,
                pan: false,
              };
            } else if (toolName === 'Paraph') {
              return {
                ...x,
                dCreateStamp: false,
                dMention: false,
                dParaph: true,
                pan: false,
              };
            }
          }
          return {
            ...x,
            dCreateStamp: false,
            dMention: false,
            dParaph: false,
            pan: false,
          };
        }),
      };
    },
    updateAnnoSignature: (
      state,
      action: {
        payload: {annotationId: string; signatoryId: string | number};
      },
    ) => {
      const {annotationId, signatoryId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.signatoryId === signatoryId) {
            return {
              ...x,
              annotationStamp: [annotationId],
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          }
          return x;
        }),
      };
    },

    updateAnnomention: (
      state,
      action: {
        payload: {annotationId: string; signatoryId: string | number};
      },
    ) => {
      const {annotationId, signatoryId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.signatoryId === signatoryId) {
            return {
              ...x,
              annotaionmention: [annotationId],
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          }
          return x;
        }),
      };
    },

    updateAnnoParaph: (
      state,
      action: {
        payload: {annotationId: string | number; signatoryId: string | number};
      },
    ) => {
      const {annotationId, signatoryId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.signatoryId === signatoryId) {
            return {
              ...x,
              annotationParaph: annotationId,
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          }
          return x;
        }),
      };
    },

    /** Update annotations*/
    updateAnnotaions: (
      state,
      action: {
        payload: {annotationId: string};
      },
    ) => {
      const {annotationId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.dMention) {
            return {
              ...x,
              annotaionmention: x.annotaionmention
                ? ([...x.annotaionmention, annotationId] as string[])
                : [annotationId],
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          } else if (x.dCreateStamp) {
            return {
              ...x,
              annotationStamp: x.annotationStamp
                ? ([...x.annotationStamp, annotationId] as string[])
                : [annotationId],
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          } else if (x.dParaph) {
            return {
              ...x,
              annotationParaph: annotationId,
              pan: false,
              dCreateStamp: false,
              dMention: false,
              dParaph: false,
            };
          }
          return x;
        }),
      };
    },
    /***Delete annotation*/
    deleteAnnotations: (
      state,
      action: {payload: {subject: string; annotationId: string | number}},
    ) => {
      const {annotationId} = action.payload;
      return {
        ...state,
        annotations: state.annotations.map(x => {
          if (x.annotaionmention.length > 0) {
            return {
              ...x,
              annotaionmention: x.annotaionmention.filter(
                item => item !== annotationId,
              ),
              dMention: !!x.annotaionmention.find(
                item => item === annotationId,
              ),
              dCreateStamp: false,
              dParaph: false,
              pan: false,
            };
          } else if (x.annotationParaph === annotationId) {
            return {
              ...x,
              annotationParaph: null,
              dParaph: true,
              dMention: false,
              dCreateStamp: false,
              pan: false,
            };
          } else if (x.annotationStamp.length > 0) {
            return {
              ...x,
              annotationStamp: x.annotationStamp.filter(
                item => item !== annotationId,
              ),
              dCreateStamp: !!x.annotationStamp.find(
                item => item === annotationId,
              ),
              dMention: false,
              dParaph: false,
              pan: false,
            };
          }
          return x;
        }),
      };
    },
    setOrderApprove: (state, action: {payload: {orderApprove: boolean}}) => {
      const {orderApprove} = action.payload;
      return {
        ...state,
        project: {
          ...state.project,
          orderApprove,
        },
      };
    },
    setOrderSignature: (state, action: {payload: {orderSign: boolean}}) => {
      const {orderSign} = action.payload;
      return {
        ...state,
        project: {
          ...state.project,
          orderSign,
        },
      };
    },
    /**  set signature levels **/
    setSignatureLevels: (state, action: {payload: ISignatureLevels}) => {
      state.signatureLevels = action.payload;
    },
    /**  set process advanced signature **/
    setProcessAdvancedSignature: (
      state,
      action: {payload: IProcessAdvancedSignature},
    ) => {
      state.processAdvanceSignature = action.payload;
    },
  },
});

export const {
  storeCreateProjectActiveRole,
  signOut,
  refreshToken,
  storeSignatories,
  storeApprovals,
  storeRecipient,
  storeCreateModel,
  storeViewers,
  storeTempFile,
  updateTempFile,
  storeAnnotationExist,
  clearTempFiles,
  clearSignatory,
  clearApproval,
  clearReceptient,
  clearViewers,
  storeAnnotation,
  updateAnnoParaph,
  updateAnnotaions,
  resetAnnotationActive,
  updateAnnotaionsActive,
  storeUserCompanyInfo,
  deleteAnnotations,
  setOrderSignature,
  setOrderApprove,
  resetToInitial,
  updateDocumentToAnnotaitons,
  resetUpdateDocumentToAnnotaitons,
  storeProject,
  storeSignatureTemplate,
  storeEnvoiByRole,
  updateEnvoiByRole,
  setActiveActorRole,
  projectDetailAction,
  storeModel,
  setSignatureLevels,
  setProcessAdvancedSignature,
} = authSlice.actions;
export default authSlice.reducer;
