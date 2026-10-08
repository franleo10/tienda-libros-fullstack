import { Component, inject, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  protected readonly cargando = signal(false);
  protected readonly mensajeError = signal('');

  formulario = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected alEnviar(){
    this.cargando.set(true);
    this.mensajeError.set('');
    this.auth.login(this.formulario.value.email!, this.formulario.value.password!).subscribe({
    next: (respuesta) => {this.cargando.set(false)},
    error: (error) => { console.log('Login falló', error); this.cargando.set(false); this.mensajeError.set('No se pudo iniciar sesión. Revisá tu email y contraseña.')},
    });
  }

}
