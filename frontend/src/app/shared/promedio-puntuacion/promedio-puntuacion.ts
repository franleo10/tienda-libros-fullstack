import { Component, computed, input } from '@angular/core';
import { ReseniaDeLibro } from '../../models/libro';

@Component({
  selector: 'app-promedio-puntuacion',
  imports: [],
  templateUrl: './promedio-puntuacion.html',
  styleUrl: './promedio-puntuacion.css',
})
export class PromedioPuntuacion {
  resenias = input<ReseniaDeLibro[]>([]);

  protected readonly cantidad = computed(() => this.resenias().length);

  protected readonly promedio = computed(() => {
    if (this.cantidad() === 0) {
      return 0;
    }
    const suma = this.resenias().reduce((total, r) => total + r.calificacion, 0);
    return suma / this.cantidad();
  });

  private readonly formato = new Intl.NumberFormat('es-AR', {
    minimumFractionDigits: 1,
    maximumFractionDigits: 1,
  });

  protected formatear(n: number) {
    return this.formato.format(n);
  }
}