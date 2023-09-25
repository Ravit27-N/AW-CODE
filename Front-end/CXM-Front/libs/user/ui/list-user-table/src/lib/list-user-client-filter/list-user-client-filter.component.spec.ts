import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListUserClientFilterComponent } from './list-user-client-filter.component';

describe('ListUserClientFilterComponent', () => {
  let component: ListUserClientFilterComponent;
  let fixture: ComponentFixture<ListUserClientFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListUserClientFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListUserClientFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
