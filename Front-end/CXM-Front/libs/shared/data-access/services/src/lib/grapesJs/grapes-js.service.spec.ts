import { TestBed } from '@angular/core/testing';

import { GrapesJsService } from './grapes-js.service';

describe('GrapesJsService', () => {
  let service: GrapesJsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GrapesJsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
