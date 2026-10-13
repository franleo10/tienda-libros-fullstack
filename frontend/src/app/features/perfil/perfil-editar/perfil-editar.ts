import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/services/auth';
import { PerfilService } from '../../../services/perfil';

@Component({
  selector: 'app-perfil-editar',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './perfil-editar.html',
  styleUrl: './perfil-editar.css',
})
export class PerfilEditar implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly perfilService = inject(PerfilService);
  private readonly auth = inject(Auth);

  protected readonly cargandoPerfil = signal(true);
  protected readonly guardando = signal(false);
  protected readonly mensajeError = signal('');

  protected readonly formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    edad: [1, [Validators.required, Validators.min(1)]],
  });

  ngOnInit() {
    this.perfilService.obtenerPerfil().subscribe({
      next: (perfil) => {
        this.formulario.setValue({
          nombre: perfil.nombre,
          email: perfil.email,
          edad: perfil.edad,
        });
        this.cargandoPerfil.set(false);
      },
      error: () => {
        this.cargandoPerfil.set(false);
        this.mensajeError.set('No se pudieron cargar los datos del perfil.');
      },
    });
  }

  protected guardar() {
    if (this.formulario.invalid || this.guardando()) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.guardando.set(true);
    this.mensajeError.set('');
    this.perfilService.actualizarPerfil(this.formulario.getRawValue()).subscribe({
      next: ({ token }) => {
        this.auth.actualizarToken(token);
        this.guardando.set(false);
        void this.router.navigateByUrl('/perfil');
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        if (error.status === 409) {
          const mensajesDeConflicto = [
            'El nombre de usuario que ingresó ya está ocupado.',
            'Ese email ya está registrado.',
          ];
          const mensaje = typeof error.error === 'string' ? error.error : '';
          this.mensajeError.set(
            mensajesDeConflicto.includes(mensaje)
              ? mensaje
              : 'El nombre de usuario o el email que ingresaste ya está ocupado.'
          );
          return;
        }

        this.mensajeError.set('No se pudieron guardar los cambios. Revisá los datos e intentá de nuevo.');
      },
    });
  }
}
