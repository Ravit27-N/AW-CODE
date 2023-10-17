import {store} from '@/redux';
import {baseQuery} from '@/redux/slides/RTKBaseQuery';
import {Verb} from '@/utils/request/interface/type';
import {$isarray} from '@/utils/request/common/type';
import {Participant, SIGNING_PROCESS} from '@/constant/NGContant';
import {graviteeTransactionId} from '@/utils/common/Gravitee';
import {formatBaseAPI} from '@/utils/common/ApiFacade';

export interface TypeTemplate {
  approval: number;
  format: number;
  id: number;
  level: number;
  name: string;
  signProcess: SIGNING_PROCESS;
  signature: number;
  participants: Participant[];
}

interface DownloadDocumentInterface {
  id: number;
  fileName?: string;
  fullPath?: string;
  contentLength?: number;
  signatoryId: number;
  documentId?: number;
}

export interface Detail {
  titleInvitation: string;
  messageInvitation: string;
  projectId: number | string;
  type:
    | Participant.Approval
    | Participant.Signatory
    | Participant.Receipt
    | Participant.Viewer;
}
const URL = `${formatBaseAPI(baseQuery.projectManagement)}${baseQuery.api}${
  baseQuery.v1
}`;
export const getFilePdf = async ({
  flowId,
  docId,
}: {
  flowId: string;
  docId: string;
}): Promise<string> => {
  try {
    const res = await fetch(
      `${formatBaseAPI(
        baseQuery.signProcess,
        `/${flowId.split('?')[0]}`,
      )}/api/documents/view/` +
        flowId +
        '&docId=' +
        docId,
      {
        method: Verb.Get,
        headers: {
          'Content-Type': 'application/json',
        },
      },
    );
    return res.text();
  } catch (e) {
    return '';
  }
};
/**
 * It is used to preview file in project detail tab Documents. It returns base64.
 * */
export const getFilePdfProjectDetail = async ({docName}: {docName: string}) => {
  try {
    const res = await fetch(
      `${URL}/projects/view-documents?docName=` + docName,
      {
        method: Verb.Get,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ` + store.getState().authentication.userToken,
          'X-Gravitee-Transaction-Id': graviteeTransactionId(),
        },
      },
    );
    return res.text();
  } catch (e) {
    return '';
  }
};

/**
 * It is used for download current with authentication
 * */
export const downloadCurrentDocumentProject = async (docId: string) => {
  try {
    const res = await fetch(`${URL}/documents/${docId}/download/current`, {
      method: Verb.Get,
      headers: {
        'Content-Type': 'blob',
        Authorization: `Bearer ` + store.getState().authentication.userToken,
        'X-Gravitee-Transaction-Id': graviteeTransactionId(),
      },
    });
    const blob = await res.blob();
    const file = window.URL.createObjectURL(blob);
    handleDownloadFileToLocal(blob);

    return file;
  } catch (e) {
    console.warn(e);
  }
};

/**
 * It is used for a download a current file for a specific actor or signatory with authentication.
 * */

export const downloadCurrentDocumentSignatory = async (signatoryId: number) => {
  try {
    const res = await fetch(
      `${URL}/signatories/${signatoryId}/signed-documents`,
      {
        method: Verb.Get,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ` + store.getState().authentication.userToken,
          'X-Gravitee-Transaction-Id': graviteeTransactionId(),
        },
      },
    );
    const listDocuments: DownloadDocumentInterface[] = await res.json();
    if ($isarray(listDocuments)) {
      for (const doc of listDocuments) {
        const res1 = await fetch(
          `${URL}/signatories/document/${doc.id}/download`,
          {
            method: Verb.Get,
            headers: {
              'Content-Type': 'blob',
              Authorization:
                `Bearer ` + store.getState().authentication.userToken,
              'X-Gravitee-Transaction-Id': graviteeTransactionId(),
            },
          },
        );
        const blob = await res1.blob();
        const file = window.URL.createObjectURL(blob);
        handleDownloadFileToLocal(blob);

        window.open(file);
      }
    }
    //   else signatory don't signed files yet
  } catch (e) {
    return e;
  }
};

/**
 * It is used for a download original file
 * */

export const downloadOrignalDocumentProject = async (docId: string) => {
  try {
    const res = await fetch(`${URL}/documents/${docId}/download/original`, {
      method: Verb.Get,
      headers: {
        'Content-Type': 'blob',
        Authorization: `Bearer ` + store.getState().authentication.userToken,
        'X-Gravitee-Transaction-Id': graviteeTransactionId(),
      },
    });
    const blob = await res.blob();
    const file = window.URL.createObjectURL(blob);
    handleDownloadFileToLocal(blob);

    window.open(file);
  } catch (e) {
    console.warn(e);
  }
};

/**
 * Use for a download pdf file to local device
 * */
export const handleDownloadFileToLocal = (
  blob: Blob,
  fileName = 'Document.pdf',
) => {
  const link = document.createElement('a');

  link.href = window.URL.createObjectURL(blob);
  link.setAttribute('download', fileName);

  // Append to html link element page
  document.body.appendChild(link);

  // Start download
  link.click();

  // Clean up and remove the link
  if (link.parentNode) link.parentNode.removeChild(link);
};
