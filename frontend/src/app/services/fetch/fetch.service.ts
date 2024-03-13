import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'src/app/model/message/message';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  //private baseUrl = 'http://localhost:9081/api';
  private baseUrl = 'http://localhost:9083/service-api/api'; // gateway

  constructor(private http: HttpClient) {}

  start(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/start');
  }

  moveRight(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/moveRight');
  }

  moveLeft(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/moveLeft');
  }

  moveDown(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/moveDown');
  }

  rotateRight(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/rotateRight');
  }

  rotateLeft(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/rotateLeft');
  }

}
