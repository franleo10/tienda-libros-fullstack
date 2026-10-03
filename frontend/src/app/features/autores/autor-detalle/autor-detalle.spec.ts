import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutorDetalle } from './autor-detalle';

describe('AutorDetalle', () => {
  let component: AutorDetalle;
  let fixture: ComponentFixture<AutorDetalle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutorDetalle]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AutorDetalle);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
