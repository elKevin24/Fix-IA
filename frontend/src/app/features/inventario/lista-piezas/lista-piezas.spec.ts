import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaPiezasComponent } from './lista-piezas';

describe('ListaPiezasComponent', () => {
  let component: ListaPiezasComponent;
  let fixture: ComponentFixture<ListaPiezasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListaPiezasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListaPiezasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
