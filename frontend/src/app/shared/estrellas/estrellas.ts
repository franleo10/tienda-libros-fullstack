import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-estrellas',
  imports: [],
  templateUrl: './estrellas.html',
  styleUrl: './estrellas.css',
})
export class Estrellas {
  valor = input(0);
  editable = input(false);
  puntuacionElegida = output<number>();
  protected readonly estrellas = [1, 2, 3, 4, 5];

  elegir(n: number) {
    if (!this.editable()) return;
    this.puntuacionElegida.emit(n);
  }
}
