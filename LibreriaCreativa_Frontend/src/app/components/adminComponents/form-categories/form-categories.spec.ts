import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormCategories } from './form-categories';

describe('FormCategories', () => {
  let component: FormCategories;
  let fixture: ComponentFixture<FormCategories>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormCategories]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormCategories);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
