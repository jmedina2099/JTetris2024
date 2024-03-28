import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Box } from 'src/app/model/figure/box';
import { EventSourceService } from './event-source.service';
import { take } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  private fetchUrl = environment.baseUrl + environment.gameUri;

  constructor(
    private sseService: EventSourceService,
    private http: HttpClient
  ) {}

  start(): Observable<Box> {
    return this.sseService
      .observeMessages(this.fetchUrl + '/start')
      .pipe(take(4));
    //return this.http.post<Message>(this.fetchUrl + '/start', null);
  }

  moveRight(): Observable<Box> {
    return this.sseService
      .observeMessages(this.fetchUrl + '/moveRight')
      .pipe(take(4));
  }

  moveLeft(): Observable<Box> {
    return this.sseService
      .observeMessages(this.fetchUrl + '/moveLeft')
      .pipe(take(4));
  }

  rotateRight(): Observable<Box> {
    return this.sseService
      .observeMessages(this.fetchUrl + '/rotateRight')
      .pipe(take(4));
  }

  rotateLeft(): Observable<Box> {
    return this.sseService
      .observeMessages(this.fetchUrl + '/rotateLeft')
      .pipe(take(4));
  }

  bottomDown(): Observable<Box> {
    return this.sseService.observeMessages(this.fetchUrl + '/bottomDown');
    //return this.http.post<void>(this.fetchUrl + '/bottomDown', null);
  }
}
