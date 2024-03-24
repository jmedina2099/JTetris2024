import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Message } from 'src/app/model/message/message';
import { Figure } from 'src/app/model/figure/figure';
import { Box } from 'src/app/model/figure/box';
import { EventSourceService } from './event-source.service';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class FetchService {
  //private baseUrl = 'http://localhost:9081/api';
  private baseUrl = 'http://localhost:9083/service-api/api'; // gateway

  constructor(private sseService: EventSourceService, private http: HttpClient) {}

  start(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/start');
  }

  moveRight(): Observable<Box> {
    return this.sseService.observeMessages(this.baseUrl + '/moveRight')
      .pipe( 
        map((box : Box) => {
          return box;
        }),
        take(4) );
    //return this.http.get<Box>(this.baseUrl + '/moveRight', {observe: "events", responseType: "json", reportProgress: true } );
  }

  moveLeft(): Observable<Box> {
    return this.sseService.observeMessages(this.baseUrl + '/moveLeft')
      .pipe( 
        map((box : Box) => {
          return box;
        }),
        take(4) );
    //return this.http.get<Message>(this.baseUrl + '/moveLeft');
  }

  moveDown(): Observable<Message> {
    return this.http.get<Message>(this.baseUrl + '/moveDown');
  }

  rotateRight(): Observable<Box> {
    return this.sseService.observeMessages(this.baseUrl + '/rotateRight')
      .pipe( 
        map((box : Box) => {
          return box;
        }),
        take(4) );
    //return this.http.get<Message>(this.baseUrl + '/rotateRight');
  }

  rotateLeft(): Observable<Box> {
    return this.sseService.observeMessages(this.baseUrl + '/rotateLeft')
      .pipe( 
        map((box : Box) => {
          return box;
        }),
        take(4) );
    //return this.http.get<Message>(this.baseUrl + '/rotateLeft');
  }

  bottomDown(): Observable<Box> {
    return this.sseService.observeMessages(this.baseUrl + '/bottomDown')
      .pipe( 
        map((box : Box) => {
          return box;
        }),
        take(4) );
    //return this.http.get<Message>(this.baseUrl + '/bottomDown');
  }
}
