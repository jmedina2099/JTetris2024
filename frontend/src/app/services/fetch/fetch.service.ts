import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'src/app/model/message/message';
import { Box } from 'src/app/model/figure/box';
import { EventSourceService } from './event-source.service';
import { take } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  private baseUrl = environment.baseUrl;

  constructor(
    private sseService: EventSourceService,
    private http: HttpClient
  ) {}

  start(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/start');
  }

  moveRight(): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/moveRight')
      .pipe(take(4));
  }

  moveLeft(): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/moveLeft')
      .pipe(take(4));
  }

  rotateRight(): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/rotateRight')
      .pipe(take(4));
  }

  rotateLeft(): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/rotateLeft')
      .pipe(take(4));
  }

  bottomDown(): Observable<Box> {
    return this.sseService
      .observeMessages(this.baseUrl + '/bottomDown')
      .pipe(take(4));
  }
}
