import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BrowseButtonComponent } from './browse-button.component';

describe('BrowseButtonComponent', () => {
  let component: BrowseButtonComponent;
  let fixture: ComponentFixture<BrowseButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BrowseButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BrowseButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
