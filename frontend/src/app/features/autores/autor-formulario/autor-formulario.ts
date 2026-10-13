import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Observable, finalize } from 'rxjs';
import { AutorGuardado } from '../../../models/autor';
import { Autor } from '../../../services/autor';

function noSoloEspacios(control: AbstractControl): ValidationErrors | null {
  const valor: unknown = control.value;
  return typeof valor === 'string' && valor.length > 0 && valor.trim().length === 0
    ? { whitespace: true }
    : null;
}

type CampoAutor = 'nombre' | 'apellido';

@Component({
  selector: 'app-autor-formulario',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './autor-formulario.html',
  styleUrl: './autor-formulario.css',
})
export class AutorFormulario implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly autorService = inject(Autor);

  protected readonly formulario = new FormGroup({
    nombre: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100), noSoloEspacios],
    }),
    apellido: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(100), noSoloEspacios],
    }),
  });
  protected readonly idAutor = signal<number | null>(null);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly cargaFallida = signal(false);
  protected readonly mensajeError = signal('');
  protected readonly editando = signal(false);

  ngOnInit(): void {
    const idParametro = this.route.snapshot.paramMap.get('id');
    if (idParametro === null) {
      return;
    }

    const id = Number(idParametro);
    if (!Number.isInteger(id) || id <= 0) {
      this.cargaFallida.set(true);
      this.mensajeError.set('El identificador del autor no es válido.');
      return;
    }

    this.editando.set(true);
    this.idAutor.set(id);
    this.cargarAutor(id);
  }

  protected mensajeCampo(campo: CampoAutor): string {
    const control = this.formulario.controls[campo];
    if (control.hasError('required')) {
      return campo === 'nombre' ? 'El nombre es obligatorio.' : 'El apellido es obligatorio.';
    }
    if (control.hasError('whitespace')) {
      return campo === 'nombre'
        ? 'El nombre no puede contener únicamente espacios.'
        : 'El apellido no puede contener únicamente espacios.';
    }
    if (control.hasError('maxlength')) {
      return campo === 'nombre'
        ? 'El nombre no puede superar los 100 caracteres.'
        : 'El apellido no puede superar los 100 caracteres.';
    }
    return '';
  }

  protected enviar(): void {
    if (this.cargando() || this.guardando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const autor: AutorGuardado = {
      nombre: this.formulario.controls.nombre.value.trim(),
      apellido: this.formulario.controls.apellido.value.trim(),
    };
    const id = this.idAutor();
    let solicitud: Observable<string>;
    if (this.editando()) {
      if (id === null) {
        this.mensajeError.set('No se pudo identificar el autor que querés editar.');
        return;
      }
      solicitud = this.autorService.actualizar(id, autor);
    } else {
      solicitud = this.autorService.crear(autor);
    }

    this.guardando.set(true);
    this.mensajeError.set('');
    solicitud.pipe(finalize(() => this.guardando.set(false))).subscribe({
      next: () => {
        void this.router.navigate(['/autores'], {
          queryParams: { resultado: this.editando() ? 'actualizado' : 'creado' },
        });
      },
      error: (error: HttpErrorResponse) => this.mensajeError.set(this.mensajeDeError(error)),
    });
  }

  private cargarAutor(id: number): void {
    this.cargando.set(true);
    this.cargaFallida.set(false);
    this.autorService
      .detalle(id)
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe({
        next: (autor) => {
          this.formulario.patchValue({ nombre: autor.nombre, apellido: autor.apellido });
        },
        error: (error: HttpErrorResponse) => {
          this.cargaFallida.set(true);
          this.mensajeError.set(
            error.status === 401
              ? 'Iniciá sesión para editar autores.'
              : error.status === 403
                ? 'No tenés permiso para editar autores.'
                : error.status === 404
                  ? 'No se encontró el autor que querés editar.'
                  : 'No se pudieron cargar los datos del autor. Intentá de nuevo.'
          );
        },
      });
  }

  private mensajeDeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con el servidor. Revisá tu conexión e intentá de nuevo.';
    }
    if (error.status === 401) {
      return 'Tu sesión venció. Iniciá sesión nuevamente para guardar el autor.';
    }
    if (error.status === 403) {
      return 'No tenés permiso para realizar esta acción.';
    }
    if (error.status === 404) {
      return 'No se encontró el autor que querés editar.';
    }
    if (error.status === 409) {
      return this.textoRespuesta(error.error) || 'Ya existe un autor con ese nombre y apellido.';
    }
    if (error.status === 400) {
      return this.textoRespuesta(error.error) || 'Revisá los datos ingresados e intentá de nuevo.';
    }
    return 'Ocurrió un error al guardar el autor. Intentá de nuevo más tarde.';
  }

  private textoRespuesta(respuesta: unknown): string {
    if (typeof respuesta === 'string' && respuesta.trim().length > 0) {
      return respuesta;
    }
    if (
      typeof respuesta === 'object' &&
      respuesta !== null &&
      'message' in respuesta &&
      typeof respuesta.message === 'string'
    ) {
      return respuesta.message;
    }
    return '';
  }
}
