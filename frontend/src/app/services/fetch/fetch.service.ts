import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  //private baseUrl = 'http://localhost:9081/api';
  private baseUrl = 'http://localhost:9083/service-api/api'; // gateway

  constructor(private http: HttpClient) {}

  start(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/start');
  }

  moveRight(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/moveRight');
  }

  moveLeft(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/moveLeft');
  }

  moveDown(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/moveDown');
  }

  rotateRight(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/rotateRight');
  }

  rotateLeft(): Observable<string> {
    return this.http.get<string>(this.baseUrl + '/rotateLeft');
  }

}
