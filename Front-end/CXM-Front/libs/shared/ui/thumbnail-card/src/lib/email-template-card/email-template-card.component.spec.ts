import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailTemplateCardComponent } from './email-template-card.component';

describe('EmailTemplateCardComponent', () => {
  let component: EmailTemplateCardComponent;
  let fixture: ComponentFixture<EmailTemplateCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmailTemplateCardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailTemplateCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
