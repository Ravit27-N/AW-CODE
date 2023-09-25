import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmsParameterNewComponent } from './sms-parameter-new.component';

describe('SmsParameterNewComponent', () => {
  let component: SmsParameterNewComponent;
  let fixture: ComponentFixture<SmsParameterNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SmsParameterNewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SmsParameterNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
