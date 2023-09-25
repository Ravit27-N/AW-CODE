import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListUserDivisionFilterComponent } from './list-user-division-filter.component';

describe('ListUserDivisionFilterComponent', () => {
  let component: ListUserDivisionFilterComponent;
  let fixture: ComponentFixture<ListUserDivisionFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListUserDivisionFilterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListUserDivisionFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
