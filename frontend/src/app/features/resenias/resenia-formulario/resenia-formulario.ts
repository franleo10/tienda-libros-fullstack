import { Component, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Resenias } from '../../../services/resenias';
import { ReseniaDato, ReseniaNueva } from '../../../models/resenia';
import { Estrellas } from '../../../shared/estrellas/estrellas';

@Component({
  selector: 'app-resenia-formulario',
  imports: [ReactiveFormsModule, Estrellas],
  templateUrl: './resenia-formulario.html',
  styleUrl: './resenia-formulario.css',
})
export class ReseniaFormulario {
  private fb = inject(FormBuilder);
  private reseniaService = inject(Resenias);

  idLibro = input.required<number>();
  creada = output<ReseniaDato>();

  protected readonly enviando = signal(false);
  protected readonly mensajeError = signal('');

  formulario = this.fb.group({
    calificacion: [0, [Validators.required, Validators.min(1)]],
    texto: ['', [Validators.required, Validators.maxLength(1000)]]
  });

  enviar() {
    if (this.formulario.invalid) return;
    this.mensajeError.set('');
    this.enviando.set(true);

    const valores = this.formulario.value;
    const resenia: ReseniaNueva = {
      texto: valores.texto!,
      calificacion: valores.calificacion!
    };

    this.reseniaService.crear(this.idLibro(), resenia).subscribe({
      next: (creada) => { this.enviando.set(false); this.creada.emit(creada); this.formulario.reset({ calificacion: 0, texto: '' })},
      error: (error) => { if(typeof error.error === 'string'){this.mensajeError.set(error.error)}else{this.mensajeError.set('No se pudo enviar la reseña')}; this.enviando.set(false) },
    });

  }
}