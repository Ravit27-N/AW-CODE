import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientModificationPageComponent } from './client-modification-page.component';

describe('ClientModificationPageComponent', () => {
  let component: ClientModificationPageComponent;
  let fixture: ComponentFixture<ClientModificationPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ClientModificationPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientModificationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
