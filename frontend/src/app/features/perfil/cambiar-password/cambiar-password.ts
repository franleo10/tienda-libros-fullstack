import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../../core/services/auth';
import { PerfilService } from '../../../services/perfil';

function passwordsCoinciden(control: AbstractControl): ValidationErrors | null {
  return control.get('passwordNueva')?.value === control.get('confirmarPassword')?.value
    ? null
    : { noCoinciden: true };
}

@Component({
  selector: 'app-cambiar-password',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cambiar-password.html',
  styleUrl: './cambiar-password.css',
})
export class CambiarPassword {
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly perfilService = inject(PerfilService);
  private readonly auth = inject(Auth);

  protected readonly guardando = signal(false);
  protected readonly mensajeError = signal('');

  protected readonly formulario = this.fb.nonNullable.group(
    {
      passwordActual: ['', Validators.required],
      passwordNueva: ['', [Validators.required, Validators.minLength(6)]],
      confirmarPassword: ['', Validators.required],
    },
    { validators: passwordsCoinciden }
  );

  protected guardar() {
    if (this.formulario.invalid || this.guardando()) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { passwordActual, passwordNueva } = this.formulario.getRawValue();
    this.guardando.set(true);
    this.mensajeError.set('');
    this.perfilService.cambiarPassword({ passwordActual, passwordNueva }).subscribe({
      next: ({ token }) => {
        this.auth.actualizarToken(token);
        this.guardando.set(false);
        void this.router.navigateByUrl('/perfil');
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.mensajeError.set(
          error.status === 400 && typeof error.error === 'string'
            ? error.error
            : 'No se pudo cambiar la contraseña. Intentá de nuevo.'
        );
      },
    });
  }
}
