import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/services/app/app.service';

export interface Credentials {
  username: string;
  password: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  credentials: Credentials = {
    username: '',
    password: '',
  };

  constructor(
    private app: AppService,
    private router: Router
  ) {}

  login(): void {
    this.app.authenticate(
      this.credentials,
      () => {
        this.router.navigateByUrl('/jtetris');
      },
      () => {
        alert('Invalid credentials!');
      }
    );
  }
}
