import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { Injectable, NgModule } from '@angular/core';
import { AuthDataAccessModule } from '@cxm-smartflow/auth/data-access';
import {
  Attributes,
  IntersectionObserverHooks,
  LazyLoadImageModule,
  LAZYLOAD_IMAGE_HOOKS
} from 'ng-lazyload-image';
import { ImageComponent } from './image.component';

@Injectable() 
export class LazyLoadImageHooks extends IntersectionObserverHooks {
  loadImage({ imagePath }: Attributes): Promise<string> {
    return fetch(imagePath, {
      headers: {
        Authorization: `Bearer ${localStorage.access_token}`,
      },
    })
      .then((res) => res.blob())
      .then((blob) => URL.createObjectURL(blob));
  }
}

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    LazyLoadImageModule,
    AuthDataAccessModule.forRoot(),
  ],
  declarations: [ImageComponent],
  exports: [ImageComponent],
  providers: [{ provide: LAZYLOAD_IMAGE_HOOKS, useClass: LazyLoadImageHooks }],
})

export class SharedUiImageModule {}
