import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  //private baseUrl = 'http://localhost:9081/api';
  private baseUrl = 'http://localhost:9083/service-api/api';

  constructor(private http: HttpClient) {}

  start(): Observable<boolean> {
    return this.http.get<boolean>(this.baseUrl + '/start');
  }

  moveRight(): Observable<boolean> {
    return this.http.get<boolean>(this.baseUrl + '/moveRight');
  }

  moveLeft(): Observable<boolean> {
    return this.http.get<boolean>(this.baseUrl + '/moveLeft');
  }

  moveDown(): Observable<boolean> {
    return this.http.get<boolean>(this.baseUrl + '/moveDown');
  }
}
