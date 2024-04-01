import { Injectable } from '@angular/core';
import { Observable, Subscriber, EMPTY } from 'rxjs';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { AppService } from '../app/app.service';
import { Figure } from 'src/app/model/figure/figure';
import { Board } from 'src/app/model/board/board';

const EventSource = EventSourcePolyfill;

@Injectable({
  providedIn: 'root',
})
export class EventSourceService {
  constructor(private app: AppService) {}

  observeFigure(url: string): Observable<Figure> {
    //console.log( '---> observeFigure' );
    const auth = sessionStorage.getItem('authorization');
    //console.log('===> auth = ' + auth);
    if (auth === null) {
      return EMPTY;
    }
    return new Observable<Figure>((observer: Subscriber<Figure>) => {
      const eventSource = new EventSource(url, {
        withCredentials: true,
        lastEventIdQueryParameterName: 'Last-Event-Id',
        headers: {
          Authorization: auth,
          'Content-Type': 'text/event-stream',
          'Cache-Control': 'no-cache',
          Connection: 'keep-alive',
          'X-Accel-Buffering': 'no',
        },
        heartbeatTimeout: 60 * 60 * 1000,
      });
      eventSource.addEventListener('message', messageEvent => {
        //console.log( '---> observeFigure.message ='+messageEvent.data );
        if (messageEvent.data != undefined) {
          observer.next(JSON.parse(messageEvent.data) as Figure);
        }
      });
      eventSource.addEventListener('error', () => {
        //console.log( '---> observeFigure.error!' );
        observer.complete();
      });
      return () => eventSource.close();
    });
  }

  observeBoard(url: string): Observable<Board> {
    //console.log( '---> observeBoard' );
    const auth = sessionStorage.getItem('authorization');
    //console.log('===> auth = ' + auth);
    if (auth === null) {
      return EMPTY;
    }
    return new Observable<Figure>((observer: Subscriber<Board>) => {
      const eventSource = new EventSource(url, {
        withCredentials: true,
        lastEventIdQueryParameterName: 'Last-Event-Id',
        headers: {
          Authorization: auth,
          'Content-Type': 'text/event-stream',
          'Cache-Control': 'no-cache',
          Connection: 'keep-alive',
          'X-Accel-Buffering': 'no',
        },
        heartbeatTimeout: 60 * 60 * 1000,
      });
      eventSource.addEventListener('message', messageEvent => {
        //console.log( '---> observeBoard.message ='+messageEvent.data );
        if (messageEvent.data != undefined) {
          observer.next(JSON.parse(messageEvent.data) as Board);
        }
      });
      eventSource.addEventListener('error', () => {
        //console.log( '---> observeBoard.error!' );
        observer.complete();
      });
      return () => eventSource.close();
    });
  }
}
