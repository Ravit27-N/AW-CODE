/* eslint-disable object-shorthand */
/* eslint-disable prefer-arrow/prefer-arrow-functions */
import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from '../../../environments/environment';
import { getAssetPrefix } from '../utils';

declare const $: any;
declare const elFinder: any;

@Component({
  selector: 'app-file-manager',
  templateUrl: './file-manager.component.html',
})
export class FileManagerComponent implements AfterViewInit, OnDestroy {
  @Output() fileChoose = new EventEmitter<string>();
  @Input() width: string | number;
  @Input() height: string | number;
  @Input() start?: string = null;

  @ViewChild('elfinderEL', { static: true })
  shellElement: ElementRef;

  private elfinder;

  constructor(private oauth: OAuthService) {}

  ngOnDestroy(): void {
    if (this.elfinder) {
      this.elfinder.destroy();
    }
  }

  ngAfterViewInit(): void {
    // eslint-disable-next-line no-underscore-dangle
    const myCommands = elFinder.prototype._options.commands;
    elFinder.prototype.i18.en.messages.TextArea = 'Edit';

    const recieveFileCallback = (file: any, instance: any) => {
      const url = file.url;
      const absolutefile = instance.convAbsUrl(url);
      this.fileChoose.emit(absolutefile);
    };

    let hash = null;
    if (this.start) {
      hash =
        'A_' +
        btoa(this.start)
          .replace(/\+/g, '-')
          .replace(/\//g, '_')
          .replace(/=/g, '.')
          .replace(/\.+$/, '');
    }

    const options = {
      baseUrl: `${getAssetPrefix()}/assets/lib/`,
      startPathHash: hash,
      useBrowserHistory: false,
      commands: myCommands,
      url: `${environment.apiUrl}${environment.rmsContextPath}/connector`,
      rememberLastDir: true,
      customHeaders: {
        // eslint-disable-next-line @typescript-eslint/naming-convention
        Authorization: `Bearer ${this.oauth.getAccessToken()}`,
      },
      height: this.height || '700px',
      width: this.width,
      uiOptions: {
        toolbar: [
          ['back', 'forward'],
          ['home', 'up'],
          ['reload'],
          ['mkdir', 'mkfile', 'upload'],
          ['open', 'download'],
          ['undo', 'redo'],
          ['info'],
          ['quicklook'],
          ['copy', 'cut', 'paste'],
          ['rm'],
          ['duplicate', 'rename', 'edit'],
          ['selectall', 'selectnone', 'selectinvert'],
          ['view', 'sort', 'help'],
          ['search'],
        ],
        tree: {
          openRootOnLoad: false,
          syncTree: true,
        },
      },
      commandsOptions: {
        quicklook: {
          width: 800,
          height: 650,
        },
      },
      getFileCallback: recieveFileCallback,
      handlers: {
        dblclick: function (event, elfinderInstance) {
          event.preventDefault();
          elfinderInstance
            .exec('getfile')
            .done(function () {
              elfinderInstance.exec('open');
            })
            .fail(function () {
              elfinderInstance.exec('open');
            });
        },
      },
    };

    this.elfinder = $(this.shellElement.nativeElement)
      .elfinder(options)
      .elfinder('instance');
  }
}
