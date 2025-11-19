import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultaTicket } from './consulta-ticket';

describe('ConsultaTicket', () => {
  let component: ConsultaTicket;
  let fixture: ComponentFixture<ConsultaTicket>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultaTicket]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsultaTicket);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
