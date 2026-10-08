import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Auth } from '../../../core/services/auth';

function passwordsCoinciden(control: AbstractControl): ValidationErrors | null {

  if (control.get('password')?.value === control.get('confirmarPassword')?.value) {
    return null;
  } else {
    return { noCoinciden: true }
  }

}



@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');

  formulario = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(50)]],
    edad: [null as number | null, [Validators.required, Validators.min(1)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmarPassword: ['', Validators.required]
  }, { validators: passwordsCoinciden });

  protected alEnviar() {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.auth.register(this.formulario.value.nombre!, this.formulario.value.edad!, this.formulario.value.email!, this.formulario.value.password!).subscribe({
      next: (respuesta) => { this.cargando.set(false) },
      error: (error) => { this.cargando.set(false); if (error.status === 409) { this.mensajeError.set(error.error) } else { this.mensajeError.set('No se pudo registrar. Intentá de nuevo.') } },
    });


  }
}
