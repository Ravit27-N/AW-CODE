import {textColor} from '@/components/ng-group-avatar/NGGroupAvatar';
import {LuApproval} from '@/constant/NGContant';
import {store} from '@/redux';
import {getNameByFirstIndex} from '@/utils/common/HandlerFirstName_LastName';
import {Core, WebViewerInstance} from '@pdftron/webviewer';

export const useCanvas = ({instance}: {instance: WebViewerInstance | null}) => {
  const {tempFiles} = store.getState().authentication;

  const setContext = (
    context: CanvasRenderingContext2D,
    canvas: HTMLCanvasElement,
    img: HTMLImageElement,
    index: number,
  ): void => {
    context.roundRect(2, 5, 400, 100, 20);
    context.strokeStyle = textColor[index];
    context.stroke();
    context.font = 'bold 20px Arial';
  };

  const annotManager = (
    annotationManager: Core.AnnotationManager,
    stampAnnot: Core.Annotations.StampAnnotation,
  ) => {
    annotationManager.addAnnotation(stampAnnot);
    annotationManager.showAnnotation(stampAnnot);
    annotationManager.selectAnnotation(stampAnnot);
  };

  const setCanvasApproved = async (
    firstName: string,
    lastName: string,
    index: number,
  ) => {
    const {Tools, Annotations, annotationManager} = instance!.Core;
    let curentSignatory;
    let documentId;
    store.getState().authentication.annotations.forEach(annotation => {
      if (annotation.dMention) {
        curentSignatory = annotation.signatoryId;
      }
    });
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
    setContext(context, canvas, img, index);
    context.drawImage(img, 16, 40, 30, 40);

    context.fillText(LuApproval, 60, 62);
    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = instance?.Core.documentViewer.getCurrentPage()!;
    stampAnnot.X = 100;
    stampAnnot.Y = 250;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.Subject = `${LuApproval}-${curentSignatory}-${documentId}-Approval-Signatory`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotManager(annotationManager, stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Approved']);
    }
    return tool;
  };

  const setCanvasParaph = async (
    firstName: string,
    lastName: string,
    index: number,
  ) => {
    const {Tools, Annotations, annotationManager, documentViewer} =
      instance!.Core;

    let curentSignatory;
    let documentId;

    store.getState().authentication.annotations.forEach(annotation => {
      if (annotation.dParaph) {
        curentSignatory = annotation.signatoryId!;
      }
    });

    tempFiles.forEach(file => {
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
    setContext(context, canvas, img, index);
    context.drawImage(img, 16, 33, 30, 40);
    context.fillText(getNameByFirstIndex(`${firstName} ${lastName}`), 60, 62);

    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = instance?.Core.documentViewer.getCurrentPage()!;
    stampAnnot.X =
      documentViewer.getPageWidth(documentViewer.getCurrentPage()) - 200;
    stampAnnot.Y =
      documentViewer.getPageHeight(documentViewer.getCurrentPage()) - 60;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.Subject = `${LuApproval}-${curentSignatory}-${documentId}-Paraph-Signatory`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotManager(annotationManager, stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Paraph']);
    }
    return tool;
  };

  const setCanvasSignatory = async (
    firstName: string,
    lastName: string,
    index: number,
  ): Promise<any> => {
    const {Tools, Annotations, annotationManager} = instance!.Core;
    let currentSignatory;
    let currentSignatoryName;
    let documentId;
    const {annotations, tempFiles} = store.getState().authentication;
    annotations.forEach(annotation => {
      if (annotation.dCreateStamp) {
        currentSignatory = annotation.signatoryId!;
        currentSignatoryName = annotation.signatoryName;
      }
    });
    tempFiles.forEach(file => {
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
    setContext(context, canvas, img, index);
    context.drawImage(img, 16, 40, 30, 40);
    context.fillText(
      `${
        firstName.slice(0, 1).toUpperCase() +
        firstName.slice(1, firstName.length).toLowerCase()
      } ${
        lastName.slice(0, 1).toUpperCase() +
        lastName.slice(1, lastName.length).toLowerCase()
      }`,
      60,
      62,
    );

    const stampAnnot = new Annotations.StampAnnotation();
    stampAnnot.PageNumber = instance?.Core.documentViewer.getCurrentPage()!;
    stampAnnot.X = 100;
    stampAnnot.Y = 250;
    stampAnnot.Width = 190;
    stampAnnot.Height = 50;
    stampAnnot.NoResize = true;
    stampAnnot.Subject = `${currentSignatoryName}-${currentSignatory}-${documentId}-Signatory-Lu et approuvé`;
    stampAnnot.setImageData(canvas.toDataURL('image/png', 1));
    annotManager(annotationManager, stampAnnot);
    if (tool instanceof Tools.RubberStampCreateTool) {
      tool.setStandardStamps([canvas.toDataURL('image/png', 1), 'Approved']);
    }
    return tool;
  };

  return {setCanvasParaph, setCanvasApproved, setCanvasSignatory};
};
