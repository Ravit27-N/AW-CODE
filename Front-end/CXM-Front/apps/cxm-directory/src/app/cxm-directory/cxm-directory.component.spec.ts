import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CxmDirectoryComponent } from './cxm-directory.component';

describe('CxmDirectoryComponent', () => {
  let component: CxmDirectoryComponent;
  let fixture: ComponentFixture<CxmDirectoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CxmDirectoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CxmDirectoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
