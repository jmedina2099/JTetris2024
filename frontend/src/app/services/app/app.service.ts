import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Credentials } from 'src/app/components/login/login.component';

interface Principal {
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class AppService {

  authenticated = false;
  credentials : Credentials = {
    username: '',
    password: ''
  };

  constructor(private http: HttpClient) {
  }

  authenticate(credentials: Credentials, callback: Function) {

    const headers = new HttpHeaders(credentials ? {
      authorization: 'Basic ' + btoa(credentials.username + ':' + credentials.password)
    } : {});

    this.http.get<Principal>('user', { headers: headers }).subscribe((response: Principal) => {
      if (response.name) {
        this.authenticated = true;
        this.credentials = credentials;
      } else {
        this.authenticated = false;
        this.credentials = {
          username: '',
          password: ''
        }
      }
      return callback && callback();
    });

  }
}
