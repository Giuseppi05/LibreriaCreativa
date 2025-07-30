import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormProducts } from './form-products';

describe('FormProducts', () => {
  let component: FormProducts;
  let fixture: ComponentFixture<FormProducts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormProducts]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormProducts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
