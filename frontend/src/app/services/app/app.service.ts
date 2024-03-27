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
  private fetchUrl = environment.baseUrl + environment.userUri;

  authenticated = sessionStorage.getItem('authorization') !== null;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  getResource(): Observable<Greeting> {
    return this.http.get<Greeting>(this.fetchUrl + '/resource');
  }

  authenticate(
    credentials: Credentials,
    callbackSuccess: () => void,
    callbackFailure: () => void
  ) {
    const auth =
      'Basic ' + btoa(credentials.username + ':' + credentials.password);
    const headers = new HttpHeaders(
      credentials
        ? {
            authorization: auth,
          }
        : {}
    );
    this.http
      .get<Principal>(this.fetchUrl + '/credentials', { headers: headers })
      .subscribe({
        next: (response: Principal) => {
          if (response.name) {
            this.authenticated = true;
            sessionStorage.setItem('authorization', auth);
          } else {
            this.authenticated = false;
            sessionStorage.removeItem('authorization');
          }
          return callbackSuccess && callbackSuccess();
        },
        error: () => {
          return callbackFailure && callbackFailure();
        },
      });
  }

  logout() {
    sessionStorage.removeItem('authorization');
    this.http
      .post(environment.baseUrl + '/logout', {})
      .pipe(
        finalize(() => {
          this.authenticated = false;
          this.router.navigateByUrl('/login');
        })
      )
      .subscribe();
  }
}
