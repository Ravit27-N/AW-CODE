import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {DomSanitizer, SafeStyle} from '@angular/platform-browser';

@Component({
    selector: 'app-aw-avatar',
    templateUrl: './aw-avatar.component.html',
    styleUrls: ['./aw-avatar.component.scss'],
})
export class AwAvatarComponent implements OnChanges {
    @Input() awTitle = '';
    @Input() profileImgURL = '';
    @Input() color: 'primary' | 'secondary' | 'danger' | 'success' = 'danger';
    bgColor: 'primary' | 'secondary' | 'danger' | 'success' = 'success';
    imageNotLoaded = false;

    constructor(private domSanitizer: DomSanitizer) {
    }

    ngOnChanges(changes: SimpleChanges) {
        this.imageNotLoaded = !this.profileImgURL;
        if (
            changes.profileImgURL &&
            changes.profileImgURL.currentValue !== changes.profileImgURL.previousValue
        ) {
            this.loadImage();
        }

        // Calculate bgColor based on awTitle
        if (this.awTitle) {
            const firstChar = this.awTitle.charAt(0).toLowerCase();

            const boundary = Math.ceil(26 / 4);

            if (firstChar >= 'a' && firstChar <= String.fromCharCode('a'.charCodeAt(0) + boundary - 1)) {
                this.bgColor = 'primary';
            } else if (firstChar >= String.fromCharCode('a'.charCodeAt(0) + boundary)
                && firstChar <= String.fromCharCode('a'.charCodeAt(0) + 2 * boundary - 1)) {
                this.bgColor = 'secondary';
            } else if (firstChar >= String.fromCharCode('a'.charCodeAt(0) + 2 * boundary)
                && firstChar <= String.fromCharCode('a'.charCodeAt(0) + 3 * boundary - 1)) {
                this.bgColor = 'danger';
            } else if (firstChar >= String.fromCharCode('a'.charCodeAt(0) + 3 * boundary)
                && firstChar <= 'z') {
                this.bgColor = 'success';
            } else {
                this.bgColor = 'primary';
            }
        }
    }

    bypassStyle(path: string): SafeStyle {
        return this.domSanitizer.bypassSecurityTrustStyle(`background: url('${path}')`);
    }

    private loadImage(): void {
        this.imageNotLoaded = false;
        if (this.profileImgURL) {
            const img = new Image();
            img.src = this.profileImgURL;
            img.onerror = () => {
                this.imageNotLoaded = true;
            };
        } else {
            this.imageNotLoaded = true;
        }
    }
}
