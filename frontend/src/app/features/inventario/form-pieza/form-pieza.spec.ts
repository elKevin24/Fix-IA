import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormPiezaComponent } from './form-pieza';

describe('FormPiezaComponent', () => {
  let component: FormPiezaComponent;
  let fixture: ComponentFixture<FormPiezaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormPiezaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormPiezaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
