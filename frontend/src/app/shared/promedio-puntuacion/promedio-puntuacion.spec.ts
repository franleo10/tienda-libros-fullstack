import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PromedioPuntuacion } from './promedio-puntuacion';

describe('PromedioPuntuacion', () => {
  let component: PromedioPuntuacion;
  let fixture: ComponentFixture<PromedioPuntuacion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PromedioPuntuacion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PromedioPuntuacion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
