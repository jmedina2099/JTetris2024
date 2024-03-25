import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'src/app/model/message/message';
import { Box } from 'src/app/model/figure/box';
import { EventSourceService } from './event-source.service';
import { take } from 'rxjs/operators';
import { Credentials } from 'src/app/components/login/login.component';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  //private baseUrl = 'http://localhost:9081/api';
  private baseUrl = 'http://localhost:9083/service-api/api'; // gateway

  constructor(
    private sseService: EventSourceService,
    private http: HttpClient
  ) {}

  createHeaders(credentials: Credentials) {
    return new HttpHeaders(credentials ? {
      authorization: 'Basic ' + btoa(credentials.username + ':' + credentials.password)
    } : {});
  }

  start(credentials: Credentials): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/start', { headers: this.createHeaders(credentials) });
  }

  moveRight(credentials: Credentials): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/moveRight',credentials)
      .pipe(take(4));
  }

  moveLeft(credentials: Credentials): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/moveLeft',credentials)
      .pipe(take(4));
  }

  moveDown(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/moveDown');
  }

  rotateRight(credentials: Credentials): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/rotateRight',credentials)
      .pipe(take(4));
  }

  rotateLeft(credentials: Credentials): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/rotateLeft',credentials)
      .pipe(take(4));
  }

  bottomDown(credentials: Credentials): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/bottomDown',credentials)
      .pipe(take(4));
  }
}
