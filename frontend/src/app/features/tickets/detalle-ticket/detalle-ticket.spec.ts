import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleTicketComponent } from './detalle-ticket';

describe('DetalleTicketComponent', () => {
  let component: DetalleTicketComponent;
  let fixture: ComponentFixture<DetalleTicketComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleTicketComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
