import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Credentials } from 'src/app/components/login/login.component';
import { finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Greeting } from 'src/app/components/home/home.component';
import { Observable } from 'rxjs';

interface Principal {
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class AppService {
  private baseUrl = environment.baseUrl;

  authenticated = false;
  credentials: Credentials = {
    username: '',
    password: '',
  };

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  getResource(): Observable<Greeting> {
    return this.http.get<Greeting>(this.baseUrl + '/resource');
  }

  authenticate(
    credentials: Credentials,
    callbackSuccess: () => void,
    callbackFailure: () => void
  ) {
    const headers = new HttpHeaders(
      credentials
        ? {
            authorization:
              'Basic ' +
              btoa(credentials.username + ':' + credentials.password),
          }
        : {}
    );
    this.http
      .get<Principal>(this.baseUrl + '/user', { headers: headers })
      .subscribe({
        next: (response: Principal) => {
          if (response.name) {
            this.authenticated = true;
            this.credentials = credentials;
          } else {
            this.authenticated = false;
            this.credentials = {
              username: '',
              password: '',
            };
          }
          return callbackSuccess && callbackSuccess();
        },
        error: () => {
          return callbackFailure && callbackFailure();
        },
      });
  }

  logout() {
    this.http
      .post(this.baseUrl + '/logout', {})
      .pipe(
        finalize(() => {
          this.authenticated = false;
          this.router.navigateByUrl('/login');
        })
      )
      .subscribe();
  }
}
