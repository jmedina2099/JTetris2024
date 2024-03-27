import { Injectable } from '@angular/core';
import { Observable, Subscriber, EMPTY } from 'rxjs';
import { Box } from 'src/app/model/figure/box';
import { EventSourcePolyfill } from 'event-source-polyfill';
import { AppService } from '../app/app.service';

const EventSource = EventSourcePolyfill;

@Injectable({
  providedIn: 'root',
})
export class EventSourceService {
  constructor(private app: AppService) {}

  observeMessages(url: string): Observable<Box> {
    const auth = sessionStorage.getItem('authorization');
    console.log('===> auth = ' + auth);
    if (auth === null) {
      return EMPTY;
    }
    return new Observable<Box>((observer: Subscriber<Box>) => {
      const eventSource = new EventSource(url, {
        withCredentials: true,
        lastEventIdQueryParameterName: 'Last-Event-Id',
        headers: {
          Authorization: auth,
          'Content-Type': 'text/event-stream',
        },
      });
      eventSource.addEventListener('message', messageEvent => {
        if (messageEvent.data != undefined) {
          observer.next(JSON.parse(messageEvent.data) as Box);
        }
      });
      eventSource.addEventListener('error', () => {
        observer.complete();
      });
      return () => eventSource.close();
    });
  }
}
