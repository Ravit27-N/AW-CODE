/* eslint-disable @typescript-eslint/ban-ts-comment */
// @ts-ignore
import * as grapesjs from 'grapesjs';
import 'grapesjs-preset-newsletter';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class GrapesJsService {
  /**
   * Method for initialize grapesJs.
   * @param container
   * @param fromElement
   * @returns
   */
  public initializeGrapes(container: string, fromElement: boolean = true): any {
    return grapesjs.init({
      container: container,
      fromElement: fromElement,
      width: 'auto',
      plugins: ['gjs-preset-newsletter'],
      pluginsOpts: {
        'gjs-preset-newsletter': {
          modalTitleImport: 'Import template',
        }
      },
      colorPicker: {
        appendTo: 'parent',
        offset: { top: 26, left: -166 },
      },
      deviceManager: {
        devices: [
          {
            name: 'Desktop',
            width: '',
            priority: 3,
          },
          {
            name: 'Tablet',
            width: '768px',
            // widthMedia: '991px',
            priority: 2,
          },
          {
            name: 'Mobile',
            width: '360px',
            // height: '640px',
            // widthMedia: '767px',
            priority: 1,
          },
        ]
      },
      assetManager: {
      }
    });
  }
}
