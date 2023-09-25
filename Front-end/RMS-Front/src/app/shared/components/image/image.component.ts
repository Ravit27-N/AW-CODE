
import {
  Component,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css'],
})
export class ImageComponent implements OnChanges {
  @Input() photoUrl: any;
  @Input() fallback: string;

  profile: any;
  constructor(
    private oauth: OAuthService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    this.profile = `${environment.apiUrl}${changes.photoUrl.currentValue}?access_token=${this.oauth.getAccessToken()}`;  }
}
